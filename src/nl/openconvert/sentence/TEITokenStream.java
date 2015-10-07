package nl.openconvert.sentence;

/*
import impact.ee.tagger.Context;
import impact.ee.tagger.Corpus;
import impact.ee.tagger.DummyMap;
import impact.ee.tagger.EnumerationWithContext;
import impact.ee.tagger.SimpleCorpus;
*/

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import nl.openconvert.tei.TEITagClasses;
import nl.openconvert.util.XML;
import nl.openconvert.util.XML.NodeAction;

//import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.ranges.Range;

/**
 * Todo: separate 2 functionalities
 * - sentence splitting
 * - support for tagging by implementing the "Corpus" interface
 * @author jesse
 *
 */
public class TEITokenStream implements TokenWindow // , Corpus
{
	Document document;
	List<TEIToken> tokens = new ArrayList<TEIToken>();	
	Set<Element> sentenceSplittingElements = new HashSet<Element>();
	
	
	int currentPosition=-1;
    Map<Element,TEIToken> element2TokenMap = new HashMap<Element,TEIToken>();

    
    public TEITokenStream()
    {
    	
    }
    
	public TEITokenStream(Document d)
	{
		
		this.document = d;
		sentenceSplittingElements = TEITagClasses.getSentenceSplittingElements(d);
		
		List<Element> l = TEITagClasses.getTokenElements(d);
		for (Element e: l)
		{
			TEIToken t;
			if (e.getNodeName().contains("w"))
			{
				t = new TEIToken(e);
			} else
			{
				t = new TEIPunctuation(e);
			}
			element2TokenMap.put(e,t);
			tokens.add(t); // zinsgrenzen.......
		}
		
		// mark all last elements of <p>-like as sentence final
		// BUT: need to decide on a good set of sentence splitting elements
		
		for (Element p: sentenceSplittingElements)
		{
			List<Element> tokzInP = XML.getElementsByTagname(p, 
					TEITagClasses.tokenTagNames, false);
			if (tokzInP.size() > 0)
			{
				Element te = tokzInP.get(tokzInP.size()-1);
				element2TokenMap.get(te).setIsEOS(true);
			}
		}
	
		currentPosition=0;
	}

	public void tagSentences()
	{
		XML.NodeAction action = new XML.NodeAction()
		{
			@Override
			public boolean action(Node n) 
			{
				return tagSentencesInElement(n);
			}
		};
		XML.preorder(document, action);
	}
	
	public void tagSentencesOld()
	{		
		nSentences = 0;
		// Fout: deze procedure doet dubbel werk (en nest dus <s> binnen <s>) als
		// elementen in sentenceSplittingElements binnen elkaar genest zijn
		for (Element p: sentenceSplittingElements)
		{
			
			tagSentencesInElement(p);
		}
	}

	
	// todo speed this up... no need to look at all ancestors ..
	
	private boolean breakForbidden(Element t)
	{
		Set<Node> a = XML.getAncestors(t);
		for (Node n:a)
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
				if (nl.openconvert.tei.TEITagClasses.noSentenceBreakIn((Element) n))
				{
					Element n1 = (Element) n;
					if (hasNoWordsBefore(n1,t) || hasNoWordsAfter(n1,t))
					{} else return true;
				}
		}
		return false;
	}
	
	private boolean tagSentencesInElement(Node n) 
	{
		if (!(n.getNodeType() == Node.ELEMENT_NODE))
			return false;
		Element p = (Element) n;
		if (!sentenceSplittingElements.contains(p))
		{
			return false;
		}
		List<Element> tokenElements = 
				XML.getElementsByTagname(p, TEITagClasses.tokenTagNames, false);
		
		boolean reattachDots  = true;
		boolean unwrappedWords = false;
		if (tokenElements.size() > 0)
		{
			int sStart=0;
			int sEnd=-1;
			int k=0;
			Set<Element> reattachableDots = new HashSet<Element>();
			int nWrappedWords=0;
			
			for (Element e: tokenElements)
			{
				Token t = element2TokenMap.get(e); // silly really
				if (t.getIsEOS() && !breakForbidden(e))
				{
					Element s = makeSentenceElement(tokenElements, sStart,k);
					nSentences++;
					if (nSentences % 100 == 0)
					{
						//String sentence = s!=null?s.getTextContent():"(null)";
						//System.err.println("sentence splitting:... "  + nSentences + " : " + sentence.replaceAll("\\s+"," "));
					}
					if (s != null)
						nWrappedWords += k - sStart +1;
					if (true || s != null) // Arbitraty lengthening of sentences will not work...
					{
						sStart = k+1;
					}
					
				} // ToDo improve re-attach dot if non-sentence-final .....
				else if (reattachDots && !t.isWord()) // should keep this for the end...
				{
					String s = t.getContent();
					if (s.equals(".") && k > 0)
					    reattachableDots.add(e);		
				}
				k++;
			}
			if (nWrappedWords < k)
			{
				// System.err.println("!!problem in (nW=" +  nWrappedWords + ",k= " + k   + "): " + XML.NodeToString(n));
				unwrappedWords = true;
			}
			k=0;
			if (reattachDots) for (Element e: tokenElements)
			{
				if (reattachableDots.contains(e))
				{
					String s = e.getTextContent();
					Element prev = tokenElements.get(k-1);
					e.getParentNode().removeChild(e);
					prev.setTextContent(prev.getTextContent() + s);
				}
				k++;
			}
			if (unwrappedWords)
			{
				wrapStrayWordsIn((Element) n);
			}
		}
		return true;
	}
	
	private void wrapStrayWordsIn(Element n)
	{
		if (n.getNodeName().equals("s"))
			return;
		
		List<Element> children = XML.getAllSubelements(n, false);
		boolean inWordGroup = false;
		int startOfGroup = -1;
		for (int k=0; k < children.size(); k++)
		{
			Element c = children.get(k);
			if (TEITagClasses.isTokenElement(c))
			{
				if (!inWordGroup)
					startOfGroup = k;
				inWordGroup = true;
			} else
			{
				if (inWordGroup)
				{
					Element s = makeSentenceElement(children, startOfGroup,k-1);
					if (s != null)
						System.err.println("created sentence chunk: " + XML.NodeToString(s));
					inWordGroup = false;
				}
				wrapStrayWordsIn(c);
			}
		}
		if (inWordGroup)
		{
			Element s = makeSentenceElement(children, startOfGroup,children.size()-1);
			if (s != null)
				System.err.println("created sentence chunk: " + XML.NodeToString(s));
		}
	}
	
	// ToDo: probeer op te lossen as surroundContent faalt!
	// gedoe met ranges was te langzaam... Daarom maar op een andere manier.
	
	private Element makeSentenceElement(List<Element> tokzInP, int sStart, int k)
	{
		try
		{
			Element w1 = tokzInP.get(sStart);
			Element w2 = tokzInP.get(k);
			if (w1 == null)
				return null;
			Element p1 = (Element) w1.getParentNode();
			Element p2 = (Element) w2.getParentNode();
			
			if (p1 == p2)
			{
				return createSentenceFromTo(p1, w1, w2);
			} else // try to fix by moving up...
			{
				/*
				 * Fixing if possible IF:
				 * There is a common ancestor p of p1 and p2
				 * s.t. the range between the 
				 * [ancestor-or-self a1 of e1 child of p such that no token in a1 precedes e1]
				 * and
				 * [ancestor-or-self a2 of e2 child of p such that no token in a2 follows e2]
				 */
				
				String word1 = XML.NodeToString(w1);
				String word2 = XML.NodeToString(w2);
				
				Node commonAncestor = XML.findCommonAncestor(w1, w2);
				/*
				 * We need:
				 * startNode is a child of commonAncestor
				 * startNode contains w1
				 * startNode does not have words before w1
				 * endNode is a child of commonAncestor
				 * endNode contains w2
				 * endNode does not have after w2
				 * 
				 */
				if (commonAncestor == null || commonAncestor.getNodeType()!=Node.ELEMENT_NODE)
				{
					return null;
				}
				Node startNode=w1;
				boolean found1 = false;
				
				while (startNode != null)
				{
					if (startNode.getParentNode() == commonAncestor)
					{
						if (hasNoWordsBefore(startNode,w1))
						{
							found1=true;
							break;
						} else
							break;
					}
					startNode = startNode.getParentNode();
				}
				Node endNode=w2;
				boolean found2 = false;
				while (endNode != null)
				{
					if (endNode.getParentNode() == commonAncestor)
					{
						if (hasNoWordsAfter(endNode,w2))
						{
							found2=true;
							break;
						} else
							break;
					}
					endNode = endNode.getParentNode();
				}
				
				if (found1 && found2)
				{
					/*
					System.err.println("HOERA: "  + XML.NodeToString(startNode) + " -- "
							 + XML.NodeToString(endNode));
					*/
					Element s = this.createSentenceFromTo((Element) commonAncestor, (Element) startNode, (Element) endNode);
					s.setAttribute("fixed", "true");
					return s;
				} else
				{
					// TODO: as a last resort, split the sentence in parts
					// which can be tagged.
					
					System.err.println("AHOOPS: failed to tag sentence from "  + word1 + " to "  + word2);
					System.err.println("common ancestor: " + commonAncestor);
					
					//XML.NodeToString(commonAncestor));
				}
				// Node endNode = childOfPAncestorOfe1, startNode has no character data
				// after w2
				return null;
			}
		} catch (Exception e)
		{
			System.err.println("Warning: failed to tag sentence because of hierarchy conflict");
		}
		return null;
	}

	private Element createSentenceFromTo(Element p1, Element e1, Element e2) 
	{
		Node before = e1.getPreviousSibling();
		Node after = e2.getNextSibling();
		Element s =  document.createElement("s");
		Node e = e1;
		while (e != null)
		{
			Node next = e.getNextSibling();
			p1.removeChild(e);
			s.appendChild(e);
			if (e == e2 || e == null)
				break;
			e = next;
		}; 
		try
		{
			if (after != null)
				p1.insertBefore(s,after);
			else p1.appendChild(s);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.err.println("Problem: s=" + XML.NodeToString(s));
			System.err.println("p1 = " + XML.NodeToString(p1));
			System.err.println("e1 = " + XML.NodeToString(e1));
			System.err.println("e2 = " + XML.NodeToString(e2));
			System.err.println("after= " + after  +  " -- " + after.getParentNode());
		}
		return s;
	}
	
	private boolean hasNoWordsAfter(Node startNode, Element e2) 
	{
		List<Element> words = nl.openconvert.tei.TEITagClasses.getTokenElements((Element) startNode);
		if (words.size() == 0)
			return true;
		Element w0 = words.get(words.size()-1);
		short comp = e2.compareDocumentPosition(w0);
		if (comp == Node.DOCUMENT_POSITION_FOLLOWING)
			return false;
		return true;
	}

	private boolean hasNoWordsBefore(Node startNode, Element e1) 
	{
		// TODO Auto-generated method stub
		List<Element> words = nl.openconvert.tei.TEITagClasses.getTokenElements((Element) startNode);
		if (words.size() == 0)
			return true;
		Element w0 = words.get(0);
		short comp = e1.compareDocumentPosition(w0);
		if (comp == Node.DOCUMENT_POSITION_PRECEDING)
			return false;
		return true;
	}
	//Range range;
	private int nSentences;
	
	

	@Override
	public boolean shift(int by) 
	{
		// TODO Auto-generated method stub
		if (currentPosition+ by < tokens.size() && currentPosition + by >= 0)
		{
			currentPosition += by;
			return true;
		} 
		return false;
	}

	/**
	 * Probleem bij eerste zin: linker context deugt niet.
	 */
	@Override
	public Token getToken(int rel) 
	{
		// TODO Auto-generated method stub
		if (currentPosition + rel < tokens.size() && currentPosition + rel >= 0)
			return tokens.get(currentPosition+rel);
		return null;
	}

	@Override
	public Token getToken() 
	{
		// TODO Auto-generated method stub
		return getToken(0);
	}

	/*
	@Override
	public Iterable<Context> enumerate() 
	{
		EnumerationWithContext e = 
				new EnumerationWithContext(Map.class, 
						new IteratorEnumeration(this.tokens.iterator()), new DummyMap());
		SimpleCorpus sc = new SimpleCorpus(e);
		// TODO Auto-generated method stub
		return sc;
	}
	*/
}

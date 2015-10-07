package nl.openconvert.util;

/*
 * ParseUtils.java
 *
 */



import java.util.*;
import java.io.*;
import java.net.URI;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import java.util.HashSet;

public class XML extends Object
{

	private PrintStream stdOutput;
	//private BufferedWriter IdFilename, IssuePage, IssuePageArticle, ArticleMetadata, ArticleZoning, IssueMeta;
	protected boolean printZoning = false;
	Document currentDocument = null;

	// vindt geen <x> binnen <x> (is hier ook niet de bedoeling)

	public static abstract class NodeAction
	{
		public abstract boolean action(Node n); // true: klaar, geen recursie
	}
	
	static public void preorder(Document d, NodeAction action)
	{
		Element r = d.getDocumentElement();
		preorder(r,action);
	}
	
	static public void preorder(Node n, NodeAction action)
	{
		if (!action.action(n))
		{
			NodeList children = n.getChildNodes();
			for (int k = 0; k < children.getLength(); k++)
			{
				preorder(children.item(k), action);
			}
		}
	}
	
	public static void findElementsByNameAndAttribute(List<Element> list, Element e, String elementName, String attName, String attValue, boolean recursive) // nonrecursive
	{
		NodeList children = e.getChildNodes();
		for (int k = 0; k < children.getLength(); k++)
		{
			Node c = children.item(k);
			if (c.getNodeType() == Node.ELEMENT_NODE)
			{
				Element child = (Element) c;
				String a;
				if (child.getNodeName().equalsIgnoreCase(elementName) && (a = child.getAttribute(attName)) != null && a.equals(attValue))
				{
					list.add(child);
					if (recursive) findElementsByNameAndAttribute(list, child, elementName, attName, attValue, true);
				}
				else
				{
					findElementsByNameAndAttribute(list, child, elementName, attName, attValue, recursive);
				}
			}
		}
	}
	
	public static void findElementsByName(List<Element> list, Element e, String elementName, boolean recursive) // nonrecursive
	{
		NodeList children = e.getChildNodes();
		for (int k = 0; k < children.getLength(); k++)
		{
			Node c = children.item(k);
			if (c.getNodeType() == Node.ELEMENT_NODE)
			{
				Element child = (Element) c;
				if (child.getNodeName().equalsIgnoreCase(elementName))
				{
					list.add(child);
					if (recursive) 
						findElementsByName(list, child, elementName, true);
				}
				else
				{
					findElementsByName(list, child, elementName, recursive);
				}
			}
		}
	}
	
	public static void findElementsByName(List<Element> list, Element e, Set<String> elementNames, boolean recursive) // nonrecursive
	{
		NodeList children = e.getChildNodes();
		for (int k = 0; k < children.getLength(); k++)
		{
			Node c = children.item(k);
			if (c.getNodeType() == Node.ELEMENT_NODE)
			{
				Element child = (Element) c;
				if (elementNames.contains(child.getNodeName())  || elementNames.contains(child.getLocalName()))
				{
					list.add(child);
					if (recursive) 
						findElementsByName(list, child, elementNames, true);
				}
				else
				{
					findElementsByName(list, child, elementNames, recursive);
				}
			}
		}
	}
	
	public static void getAllSubelements(List<Element> list, Element e, boolean recursive) // nonrecursive
	{
		NodeList children = e.getChildNodes();
		for (int k = 0; k < children.getLength(); k++)
		{
			Node c = children.item(k);
			if (c.getNodeType() == Node.ELEMENT_NODE)
			{
				Element child = (Element) c;
				
				{
					list.add(child);
					if (recursive) 
						 getAllSubelements(list, child, true);
				}
				
			}
		}
	}
	// vindt wel <x> binnen <x>

	public static void findAllElementsByName(List<Element> list, Element e, String elementName) // recursive
	{
		if (e.getNodeName().equalsIgnoreCase(elementName))
		{
			list.add(e);
		};
		NodeList children = e.getChildNodes();
		for (int k = 0; k < children.getLength(); k++)
		{
			Node c = children.item(k);
			if (c.getNodeType() == Node.ELEMENT_NODE)
			{
				Element child = (Element) c;
				findAllElementsByName(list, child, elementName);
			}
		}
	}

	public static List<Element> getElementsByTagname(Element e, String elementName, boolean recursive)
	{
		List<Element> list = new ArrayList<Element>();
		if (e != null)
			findElementsByName(list, e, elementName, recursive);
		return list;
	}
	
	public static List<Element> getElementsByTagname(Element e, Set<String> elementNames, boolean recursive)
	{
		List<Element> list = new ArrayList<Element>();
		if (e != null)
			findElementsByName(list, e, elementNames, recursive);
		return list;
	}
	
	public static List<Element> getElementsByTagname(Element e, String[] elementNames, boolean recursive)
	{
		Set<String> s = new HashSet<String>();
		for (String x: elementNames) s.add(x);
		List<Element> list = new ArrayList<Element>();
		if (e != null)
			findElementsByName(list, e, s, recursive);
		return list;
	}
	
	public static List<Element>  getAllSubelements(Element e, boolean recursive)
	{
		List<Element> list = new ArrayList<Element>();
		if (e != null)
			getAllSubelements(list, e, recursive);
		return list;
	}

	public static List<Element> getElementsByTagnameAndAttribute(Element e, String elementName, String attName, String attValue, boolean recursive)
	{
		List<Element> list = new ArrayList<Element>();
		findElementsByNameAndAttribute(list, e, elementName, attName, attValue, recursive);
		return list;
	}

	public static Element getElementByTagname(Element e, String elementName)
	{
		List<Element> list = getElementsByTagname(e, elementName, false);
		if (list.size() > 0)
		{
			return list.get(0);
		} else
		{
			return null;
		}
	}

	public static String getElementContent(Element e, String tagname)
	{
		Element x = getElementByTagname(e,tagname);
		if (x != null)   
		{
			return x.getTextContent();
		} else
		{
			return null;
		}
	}

	public XML()
	{
		try
		{
			stdOutput = new PrintStream(System.out,true, "UTF8");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	public static Element findFirstChild(Element e, String tagName)
	{
		NodeList l = e.getChildNodes();
		for (int i=0; i < l.getLength(); i++)
		{
			try 
			{ 
				Element c = (Element) l.item(i);
				if (c.getNodeName().equals(tagName)) return c;
			} catch (Exception ex)
			{
			}
		}
		return null;
	}

	public static Element findFirstChild(Element e, String[] tagNames)
	{
		NodeList l = e.getChildNodes();
		for (int i=0; i < l.getLength(); i++)
		{
			try
			{
				Element c = (Element) l.item(i);
				for (String tagName: tagNames)
					if (c.getNodeName().equals(tagName)) return c;
			} catch (Exception ex)
			{
			}
		}
		return null;
	}

	public static List<Element> findChildren(Element e, String[] tagNames)
	{
		NodeList l = e.getChildNodes();
		ArrayList<Element> r = new ArrayList<Element>();
		for (int i=0; i < l.getLength(); i++)
		{
			try
			{
				Element c = (Element) l.item(i);
				for (String tagName: tagNames)
					if (c.getNodeName().equals(tagName)) r.add(c);
			} catch (Exception ex)
			{
			}
		}
		return r;
	}


	public static void insertChildAfter(Node n, Node after, Node newChild)
	{
		Node afterAfter = null;
		if ((afterAfter = after.getNextSibling()) == null)
		{
			n.appendChild(newChild);
		} else
		{
			try
			{
				if (afterAfter.getParentNode() != n)
				{
					System.err.println("neee - dat MEEEN je niet");
				}
				n.insertBefore(newChild, afterAfter);
			} catch (Exception e)
			{
				System.err.println(n + ""  + 
						afterAfter + "" +  
						afterAfter.getParentNode() + "" + newChild);
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static boolean isWhiteSpaceNode(Node n)
	{
		String v = n.getNodeValue();
		if (v.matches("^\\s*$"))
		{
			return true;
		}
		return false;
	}

	public static boolean isWhiteSpaceOrPunctuationNode(Node n)
	{
		String v = n.getNodeValue();
		if (v.matches("^(\\s|;|\\.)*$"))
		{
			return true;
		}
		return false;
	}
	//parser.setFeature( "http://apache.org/xml/features/dom/defer-node-expansion", false );

	/*
	 * 	private boolean hasNoWordsAfter(Node startNode, Element e2) 
	{
		List<Element> words = tei.TEITagClasses.getTokenElements((Element) startNode);
		if (words.size() == 0)
			return true;
		Element w0 = words.get(words.size()-1);
		short comp = e2.compareDocumentPosition(w0);
		if (comp == Node.DOCUMENT_POSITION_FOLLOWING)
			return false;
		return true;
	}
	 */

	public static void collectNodesBetween(List<Node> nodes, Node currentNode, Node after, Node before)
	{
		boolean b1 = after.compareDocumentPosition(currentNode) == Node.DOCUMENT_POSITION_FOLLOWING;
		boolean b2 = before.compareDocumentPosition(currentNode) == Node.DOCUMENT_POSITION_PRECEDING;
		if (b1 && b2)
			nodes.add(currentNode);
		NodeList children = currentNode.getChildNodes();
		for (int k = 0; k < children.getLength(); k++)
		{
			Node c = children.item(k);
			Node next = c.getNextSibling();
			Node previous = c.getPreviousSibling();
			boolean x1 = 
					next != null && 
					after.compareDocumentPosition(next) == Node.DOCUMENT_POSITION_PRECEDING;
			boolean x2 = previous != null &&
					before.compareDocumentPosition(previous) == Node.DOCUMENT_POSITION_FOLLOWING;
			if (!x1 && !x2)
				collectNodesBetween(nodes,c,after,before);
		}
		// klaus 4 oct .... 3 wednesday // 5 mother birthday  
	}
	
	public static List<Node> collectNodesBetween(Node after, Node before)
	{
		 List<Node> list = new ArrayList<Node>();
		 Node p = findCommonAncestor(after,before);
		 collectNodesBetween(list,p,after,before);
		 return list;
	}
	
	public static List<Element> collectNodesBetween(Set<String> elementNames, Node after, Node before)
	{
		List<Node> list = collectNodesBetween(after,before);
		List<Element> trimmed = new ArrayList<Element>();
		for (Node n: list)
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{
				if (elementNames.contains(n.getNodeName()) || 
						elementNames.contains(n.getLocalName()))
				{
					trimmed.add((Element) n);		
				}
			}
		}
		return trimmed;
	}
	
	public static Document parse(String aFilename, boolean namespaceAware) throws 
		ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(namespaceAware);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		factory.setFeature( "http://apache.org/xml/features/dom/defer-node-expansion", false );
		DocumentBuilder builder = factory.newDocumentBuilder();
		URI u = new File(aFilename).toURI();
		//There is a bug related to 4 byte UTF8 ... which has to be worked around
		// https://issues.apache.org/jira/browse/XERCESJ-1257
		BufferedInputStream fis = new BufferedInputStream(new FileInputStream(new File(aFilename)));
	    InputStreamReader isr = new java.io.InputStreamReader(fis, "UTF-8");
	    InputSource is = new InputSource(isr);
		Document document = builder.parse(is);
		//Document document = builder.parse(fis);
		
		return document;
	}
	
	public static Document parse(String aFilename) throws ParserConfigurationException, SAXException, IOException
	{
		return parse(aFilename,true);
	}

	public static Document parseString(String inputString, boolean namespaceAware)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(namespaceAware);
		try
		{
			factory.setFeature( "http://apache.org/xml/features/dom/defer-node-expansion", false );
			DocumentBuilder builder = factory.newDocumentBuilder();
			byte[] bytes = inputString.getBytes("UTF-8");
			InputStream input = new ByteArrayInputStream(bytes);
			Document document = builder.parse(input);
			return document;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Document parseStream(InputStream inputStream, boolean namespaceAware)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(namespaceAware);
		try
		{
			factory.setFeature( "http://apache.org/xml/features/dom/defer-node-expansion", false );
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			Document document = builder.parse(inputStream);
			return document;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static Document parseString(String inputString)
	{
		return parseString(inputString, true);
	}
	
	public static Document createDocument(String rootElementName)
	{
		try
		{
			DocumentBuilderFactory builderFactory = 
					DocumentBuilderFactory.newInstance();
			builderFactory.setNamespaceAware(true);
			DocumentBuilder builder = builderFactory.newDocumentBuilder();

			Document doc = builder.newDocument();
			Element root = doc.createElement(rootElementName);
	        doc.appendChild(root);
			return doc;
		} catch (Exception e)
		{
			return null;
		}
	}
	public void parseList(String listfile) throws FileNotFoundException, IOException, 
	ParserConfigurationException, SAXException
	{
		BufferedReader reader = new BufferedReader(new FileReader(listfile));
		List<String> filenames = new ArrayList<String>();
		String filename;
		while ((filename = reader.readLine()) != null)
		{
			filenames.add(filename);
		}
		for (int i = 0; i < filenames.size(); i++)
		{
			String f = filenames.get(i);
			System.err.println(i + ":" + f);
			if (!f.startsWith("#"))
			{
				//System.out.println("File " + (i + 1) + " van " + filenames.size());
				this.parse(f);
			}
		}
	}

	public static String documentToString(Document d)
	{
		try
		{
			// Set up the output transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "no");

			// Print the DOM node

			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(d.getDocumentElement());
			trans.transform(source, result);
			String xmlString = sw.toString();
			xmlString = xmlString.replaceAll(" xmlns=\"\"", ""); // ugly hack, should be avoidable...
			return xmlString;
		}
		catch (TransformerException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void printNode(Node node)
	{
		try
		{
			// Set up the output transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty(OutputKeys.METHOD, "xml");
			// Print the DOM node

			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(node);
			trans.transform(source, result);
			String xmlString = sw.toString();

			stdOutput.println(xmlString);
		}
		catch (TransformerException e)
		{
			e.printStackTrace();
		}
	}
	
	public static String NodeToString(Node node)
	{
		try
		{
			// Set up the output transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty(OutputKeys.METHOD, "xml");
			// Print the DOM node

			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(node);
			trans.transform(source, result);
			String xmlString = sw.toString();
			return xmlString;
			
		}
		catch (TransformerException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static Document cloneDocument(Document d)
	{
		try
		{
			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer tx   = tfactory.newTransformer();
			DOMSource source = new DOMSource(d);
			DOMResult result = new DOMResult();
			tx.transform(source,result);
			return (Document) result.getNode();
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args)
	throws IOException, ParserConfigurationException, SAXException
	{
		if (args.length != 1)
		{
			System.out.println("Gebruikswijze: <programmanaam> <lijstnaam>");
			return;
		}
		XML a = new XML();
		a.parse(args[0]);
	}

	private static void getTextNodesBelow(List<Node> nodes, Node n)
	{
		NodeList children = n.getChildNodes();
		for (int k = 0; k < children.getLength(); k++)
		{
			Node c = children.item(k);

			if (c.getNodeType() == Node.TEXT_NODE)
			{
				nodes.add(c);
			} else if (c.getNodeType() == Node.ELEMENT_NODE)
			{
				getTextNodesBelow(nodes,c);
			}
		}
	}

	public static List<Node> getTextNodesBelow(Node n) 
	{
		// TODO Auto-generated method stub
		List<Node> l = new ArrayList<Node>();
		getTextNodesBelow(l,n);
		return l;
	}

	public static Element findAncestor(Node t, String string) 
	{
		Node p = t;
		for (p=t; p != null; p =p.getParentNode())
		{
			if (p.getNodeType() ==Node.ELEMENT_NODE && p.getNodeName().equals(string))
				return (Element) p;
		}
		return null;
	}
	
	public static Set<Node> getAncestors(Node n)
	{
		Node p = n.getParentNode();
		Set<Node> ancestors = new HashSet<Node>();
		while (p != null)
		{
			ancestors.add(p );
			p = p.getParentNode();
		}
		return ancestors;
	}
	
	public static Node findCommonAncestor(Node n1, Node n2)
	{
		Set<Node> a1 = getAncestors(n1);
		Node p = n2.getParentNode();
		while (p!= null)
		{
			if (a1.contains(p)) return p;
			p = p.getParentNode();
		}
		return null;
	}
	
	public static BufferedReader openBufferedTextFile(String fileName)
	{
		try
		{
			BufferedReader b = 
				new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			return b;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Replace the subtree under e with a single text node
	 * @param e
	 */
	public static void flattenElementContents(Element e) 
	{
		// TODO Auto-generated method stub
		Document d = e.getOwnerDocument();
		Text t = d.createTextNode(e.getTextContent());
		NodeList ch = e.getChildNodes();
		for (int i=0; i < ch.getLength(); i++)
		{
			e.removeChild(ch.item(i));
		}
		e.appendChild(t);
	}
	
	public static void removeInterveningNode(Element e)
	{
		Node p = e.getParentNode();
		NodeList n = e.getChildNodes();
		List<Node> children = new ArrayList<Node>();
		Node next = e.getNextSibling();
		for (int i=0; i < n.getLength(); i++)
		{
			children.add(n.item(i));
		}
		for (Node c: children) e.removeChild(c);
		p.removeChild(e);
		for (Node c: children)
		{
			if (next != null) 
				p.insertBefore(c, next);
			else
				p.appendChild(c);
		}
	}
}

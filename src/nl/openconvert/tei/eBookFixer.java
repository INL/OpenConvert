package nl.openconvert.tei;

import nl.openconvert.filehandling.DirectoryHandling;
import nl.openconvert.filehandling.DoSomethingWithFile;
import nl.openconvert.util.*;

import org.w3c.dom.*;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.ranges.Range;


import java.util.*;

/**
 * Dit werkt niet.
 * Er zitten <p>-tjes in de esjes..
 * @author does
 *
 */
public class eBookFixer implements DoSomethingWithFile
{
	Validator validator = new Validator();
	
	public Document fixDocument(Document d)
	{
		Element root = d.getDocumentElement();
		List<Element> mainTextNodes = XML.getElementsByTagname(root, "text", false);
		Element main = mainTextNodes.get(0);
		List<Element> allDivs = XML.getElementsByTagname(main, "div", true);
		
		for (Element div: allDivs)
		{
			Set<Pair<Node, Node>> rangesToWrapInP = new HashSet<Pair<Node, Node>>();
			Set<Pair<Node, Node>> rangesToWrapInDiv = new HashSet<Pair<Node, Node>>();
			NodeList ch = div.getChildNodes();
			boolean wrapping = false;
			Node firstToWrap=null;
			Node lastToWrap=null;
			for (int i=0; i < ch.getLength(); i++)
			{
				Node c = ch.item(i);
				if (c.getNodeType() == Node.ELEMENT_NODE)
				{
					Element e = (Element) c;
					String tag = e.getNodeName();
					if (tag.equals("div") || tag.equals("p") || tag.equals("head"))
					{
						if (wrapping)
						{
							System.err.println("Stop wrapping at "  + c);
							rangesToWrapInP.add(new Pair<Node, Node>(firstToWrap,lastToWrap));
							firstToWrap = lastToWrap = null;
						}
						wrapping=false;
					} else // no reason to stop wrapping...
					{
						if (!wrapping)
						{
							wrapping = true;
							System.err.println("Start wrapping at "  + c);
							firstToWrap = lastToWrap = c;
						} else
						{
							lastToWrap = c;
						}
					}
				}
				if (c.getNodeType() == Node.TEXT_NODE)
				{
					String text = c.getTextContent();
					if (text.trim().length() == 0) // whitespace
					{
						if (wrapping)
						{
							lastToWrap = c;
						}
					} else
					{
						if (wrapping)
							lastToWrap = c;
						else
						{
							wrapping = true;
							firstToWrap = lastToWrap = c;
						}
					}
				}
			}
			for (Pair<Node, Node> p: rangesToWrapInP)
			{
				this.wrapRange(d, p.first, p.second, "p");
			}
		}
		return d;
	}
	
	private void wrapRange(Document d, Node firstInRange, Node lastInRange, String elementName)
	{
		if (firstInRange == null)
			return;
		DocumentRange dr = (DocumentRange) d;
		Range range = dr.createRange();
		range.setStartBefore(firstInRange);
		range.setEndAfter(lastInRange);
		Element e =  d.createElement(elementName);
		try
		{
			range.surroundContents(e);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		// System.err.println(e.getParentNode() + " " + e.getTextContent());
		range.detach();
	}
	
	public static void main(String[] args)
	{
		eBookFixer f =  new eBookFixer();
		DirectoryHandling.traverseDirectory(f, args[0]);
	}

	@Override
	public void handleFile(String fileName) 
	{
		// TODO Auto-generated method stubfil
		try
		{
			Document d = XML.parse(fileName);
			if (!validator.validate(d))
			{
				System.err.println("try to fix " + fileName);
				fixDocument(d);
				if (validator.validate(d))
				{
					System.err.println("Opgelapt!!!" + fileName);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

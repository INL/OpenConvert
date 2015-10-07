package nl.openconvert.converters;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import nl.openconvert.tei.TEITagClasses;
import nl.openconvert.util.Util;
import nl.openconvert.util.XML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class VerticalTextOutput implements nl.openconvert.filehandling.SimpleInputOutputProcess
{
	boolean tagParts = true;
	boolean useCTAG = false;
	boolean speakerDocs = false;
	private Properties properties;

	XPathFactory xpathFactory = XPathFactory.newInstance();
	XPath xpath = xpathFactory.newXPath();

	String xpathQuery= null;
	
	public void printForSketchEngine(Document d, PrintStream out)
	{
		Element e = d.getDocumentElement();
		out.print("<file");

		out.println(">");
		if (speakerDocs)
		{
			List<Element> sps = XML.getElementsByTagname(e, "sp", false);
			for (Element sp: sps)
			{
				Map<String,String> metadata = new HashMap<String,String>();

				try
				{
					Element speaker = XML.getElementsByTagname(sp, "speaker", false).get(0);
					metadata.put("speaker", getLemmaContent(speaker));
				} catch (Exception ex)
				{

				}
				printForSketchEngine(sp, out, metadata);
			}
		} else
			printForSketchEngine(e, out, null);
		out.println("</file>");
	}

	public String getLemmaContent(Element e)
	{
		if (e == null)
			return null;
		List<Element> words = 	nl.openconvert.tei.TEITagClasses.getWordElements(e);
		if (words.size() == 0)
			return null;
		List<String> lemmata = new ArrayList<String>();
		for (Element w: words)
		{
			lemmata.add(w.getAttribute("lemma"));
		}
		return Util.join(lemmata, "_");
	}

	public void printForSketchEngine(Element e, PrintStream out, Map<String,String> metadata)
	{
		//Map<String,Set<String>> metadataMap = nl.openconvert.tei.Metadata.getMetadata(d);
		out.print("<doc");
		if (metadata != null) for (String k: metadata.keySet())
			out.print(" " + k + "=" + "\"" + metadata.get(k) + "\"");
		out.println(">");
		List<Element> sentences;
		if (this.xpathQuery == null)
		 sentences = nl.openconvert.tei.TEITagClasses.getSentenceElements(e);
		else
		{
			sentences = new ArrayList<Element>();
			List<Element> selectedElements = this.getMatchingElements(e.getOwnerDocument(), this.xpathQuery);
			for (Element e1: selectedElements)
				sentences.addAll(TEITagClasses.getSentenceElements(e1));
		}
		boolean documentHasSentences = false;
		for (Element s: sentences)
		{
			List<Element> words = 	nl.openconvert.tei.TEITagClasses.getWordElements(s);
			if (words.size() == 0)
				continue;
			out.println("<s>");
			List<Element> tokens = 	nl.openconvert.tei.TEITagClasses.getTokenElements(s);
			printTokens(out, tokens);
			out.println("</s>");
			documentHasSentences = true;
		}
		if (!documentHasSentences)
		{
			List<Element> tokens = 	nl.openconvert.tei.TEITagClasses.getTokenElements(e);
			printTokens(out, tokens);
		}
		out.println("</doc>");
	}

	protected void printTokens(PrintStream out, List<Element> tokens)
	{
		for (Element t: tokens)
		{

			String tag = t.getAttribute("type");
			if (useCTAG)
				tag = t.getAttribute("ctag");
			if (t.getNodeName().equals("c"))
			{
				continue;
			}
			String lemma = t.getAttribute("lemma");
			
			String word = t.getTextContent();
			String features = t.getAttribute("features");
			if (features == null || features.length() == 0)
				out.println(word + "\t" + tag + "\t"  + lemma);
			else
				out.println(word + "\t" + tag + "\t"  + features);
		}
	}


	@Override
	public void handleFile(String in, String out) 
	{
		try 
		{
			Document d = XML.parse(in);
			PrintStream pout = new PrintStream(new FileOutputStream(out));
			printForSketchEngine(d, pout);
		} catch (Exception e) 
		{

			e.printStackTrace();
		}
	}

	@Override
	public void setProperties(Properties properties) 
	{
		// TODO Auto-generated method stub
		this.properties = properties;
	}

	public List<Element> getMatchingElements(Document d, String q)
	{
		List<Element> elementList = new ArrayList<Element>();
		try
		{
			XPathExpression e = xpath.compile(q);
			NodeList l = (NodeList) e.evaluate(d,XPathConstants.NODESET);
			for (int i=0; i < l.getLength(); i++)
				elementList.add((Element) l.item(i));
		} catch (XPathExpressionException e1) 
		{
			e1.printStackTrace();
		}
		return elementList;
	}

	public static void main(String[] args)
	{
		nl.openconvert.util.Options options = new nl.openconvert.util.Options(args);
		args = options.commandLine.getArgs();
		VerticalTextOutput v = new VerticalTextOutput();
		v.useCTAG = options.getOptionBoolean("ctag", false);
		if (args.length > 2)
		{
			System.err.println("Using xpath query!: " + args[0]);
			v.xpathQuery = args[0];
			nl.openconvert.filehandling.DirectoryHandling.tagAllFilesInDirectory(v, args[1], 
					args[2]);
		} else
		 nl.openconvert.filehandling.DirectoryHandling.tagAllFilesInDirectory(v, args[0], 
				args[1]);
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}
}

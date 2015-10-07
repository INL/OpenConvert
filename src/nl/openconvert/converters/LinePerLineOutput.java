package nl.openconvert.converters;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import nl.openconvert.filehandling.DoSomethingWithFile;

import nl.openconvert.tei.TEITagClasses;

import nl.openconvert.util.XML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.util.Set;

public class LinePerLineOutput implements nl.openconvert.filehandling.SimpleInputOutputProcess, DoSomethingWithFile
{
	boolean tagParts = true;
	private Properties properties;
	PrintStream stdout = new PrintStream(System.out);
	boolean printLemmata = false;
	int lineLength = 10;
	static boolean detachPunctuation = false;
	
	public LinePerLineOutput(int len) 
	{
		this.lineLength = len;
		// TODO Auto-generated constructor stub
	}

	public void printSentences(Document d, PrintStream out)
	{
		//Map<String,Set<String>> metadataMap = nl.openconvert.tei.Metadata.getMetadata(d);
		
		//List<Element> sentences = nl.openconvert.tei.TEITagClasses.getSentenceElements(d);
		int nLines=0;
		int nSkippedLines=0;
		
		List<Element> tokens = 	
				nl.openconvert.tei.TEITagClasses.getTokenElements(d.getDocumentElement());
		
		int k = 0;
		String prevType = "word";
		boolean first = false;
		while (k < tokens.size())
		{
			int variation = (int) (Math.round(0.5 * (-1 * lineLength + Math.random() * lineLength)));
			int chunkLength = lineLength + variation;
			boolean printed = false;
			for (int i=k; i < k+chunkLength && i < tokens.size(); i++)
			{
				Element t = tokens.get(i);
				
				String token = t.getTextContent();
				String tag = t.getTagName();
				String type = "word";
				
				if (!detachPunctuation && tag.equalsIgnoreCase("pc"))
					type = t.getAttribute("type");
				
				String tokenOut = (first || type.equalsIgnoreCase("post") || prevType.equalsIgnoreCase("pre")? "":" ") + token;
				out.print(tokenOut);
				printed = true;
				prevType = type;
				first = false;
			}
			k += chunkLength;
			if (printed && chunkLength > 0)
			{
				out.print("\n");
			}
		}
		
		//System.err.println("skipped " + nSkippedLines + " of " + nLines);
		
	}
	
	public static boolean sentenceHasEnoughLowercaseCharacters(Element s)
	{
		List<Element> tokens = 	nl.openconvert.tei.TEITagClasses.getTokenElements(s);
		boolean first = true;
		String outLine = "";
		int nLowercase=0;
		int nCharacters=0;
		boolean firstIsUpper=false;
		String prevType = "word";
		for (Element t: tokens)
		{
			String token = t.getTextContent();
			String tag = t.getTagName();
			String type = "word";
			
			if (!detachPunctuation && tag.equalsIgnoreCase("pc"))
				type = t.getAttribute("type");
			
			nCharacters += token.length();
			
			for (int i=0; i < token.length(); i++)
			{
				char c  = token.charAt(i);
				if (first && i==0)
				{
					firstIsUpper = Character.isLetter(c) && Character.isUpperCase(c);
				}
				if (Character.isLetter(c) && Character.isLowerCase(c))
					nLowercase++;
			}
			
			outLine += (first || type.equalsIgnoreCase("post") || prevType.equalsIgnoreCase("pre")? "":" ") + token;
			
			prevType = type;
			first = false;
		}
		
		return ( nLowercase / (double) nCharacters > 0.7);
	}
	
	@Override
	public void handleFile(String in, String out) 
	{
		try 
		{
			Document d = XML.parse(in);
			PrintStream pout = new PrintStream(new FileOutputStream(out));
			printSentences(d, pout);
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
	

	@Override
	public void handleFile(String fileName) 
	{
		try 
		{
			Document d = XML.parse(fileName);
			
			printSentences(d, stdout);
			stdout.flush();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		int len = Integer.parseInt(args[0]);
		if (args.length > 2)
		{
			nl.openconvert.filehandling.DirectoryHandling.tagAllFilesInDirectory(new LinePerLineOutput(len), args[1], 
				args[2]);
		} else
		{
			nl.openconvert.filehandling.DirectoryHandling.traverseDirectory(new LinePerLineOutput(len), args[1]);
		}
	}


	public void close() {
		// TODO Auto-generated method stub
		
	}
}

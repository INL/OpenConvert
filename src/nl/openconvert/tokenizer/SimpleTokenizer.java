package nl.openconvert.tokenizer;
import java.util.regex.*;
import java.util.List;
import java.io.*;

import javax.xml.parsers.ParserConfigurationException;

import nl.openconvert.util.XML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;




public class SimpleTokenizer
{
        Pattern nonWord = Pattern.compile("\\W+");

        static Pattern nonWordPattern = Pattern.compile("\\W+");
        static Pattern punctuationPattern = Pattern.compile("^\\p{P}+$");
        
    	static Pattern prePunctuationPattern = Pattern.compile("(^|\\s)\\p{P}+");
    	static Pattern postPunctuationPattern = Pattern.compile("\\p{P}+($|\\s)");

    	static Pattern leadingBlanks = Pattern.compile("^\\s+");
    	static Pattern trailingBlanks = Pattern.compile("\\s+$");
    	
    	public String prePunctuation="";
    	public String postPunctuation="";
    	public String trimmedToken="";

	
	public void tokenize(String t)
	{
		Matcher m1 = prePunctuationPattern.matcher(t);
		Matcher m2 = postPunctuationPattern.matcher(t);
		int s=0; int e = t.length();

		if (m1.find())
	 		s = m1.end();
		if (m2.find())
			e = m2.start();	

		if (e < s) e=s;
		trimmedToken = t.substring(s,e);
		prePunctuation = t.substring(0,s);
		postPunctuation = t.substring(e,t.length());
	}

	public void printTokens(String fileName, PrintStream out)
	{

		Document d = null;
		try
		{
			d = XML.parse(fileName);
		} catch (Exception e)
		{

			e.printStackTrace();
		}
		List<Element> words = XML.getElementsByTagname(d.getDocumentElement(), "w", false);
		for (Element word: words)
		{
			String w = word.getTextContent();
			String id = word.getAttribute("id");
			if (w.length()>0)
			{
				out.println(w + "\t" + id);
			}
		}
		out.flush();
	}
	
	public void printSentences(String fileName, PrintStream out)
	{

		Document d = null;
		try
		{
			d = XML.parse(fileName);
			printSentences(d,out);
		} catch (Exception e)
		{

			e.printStackTrace();
		}
	}

	public void printSentences(Document d, PrintStream out)
	{
		String[] S = {"w","pc"};
		List<Element> sentences = XML.getElementsByTagname(d.getDocumentElement(), "s", false);
		for (Element s: sentences)
		{
			List<Element> tokens = XML.getElementsByTagname(s, S, false);
			for (Element t: tokens)
			{
				String w  = t.getTextContent();
				String id = t.getAttribute("id");
				if (w.length()>0)
				{
					out.println(w + "\t" + id);
				}
			}
			out.println("");
		}
	}
	public void tokenizeTEI(String fileName, PrintStream out)
	{
		
		Document d = null;
		try 
		{
			d = XML.parse(fileName);
		} catch (Exception e) 
		{
			
			e.printStackTrace();
		} 
		List<Element> words = XML.getElementsByTagname(d.getDocumentElement(), "w", false);
		for (Element word: words)
		{
			String w = word.getTextContent();
			tokenize(w);
			String lastOut="";
			String id = word.getAttribute("id");
			if (prePunctuation.length()>0)
			{
				out.println(prePunctuation + "\t" + id);
				lastOut = prePunctuation;
			}	
			if (trimmedToken.length()>0)
			{
				out.println(trimmedToken + "\t" + id);
				lastOut = trimmedToken;
			}
			if (postPunctuation.length()>0)
			{
				out.println(postPunctuation + "\t" + id);
				lastOut = postPunctuation;
			}
			if (w.endsWith(".") || w.endsWith("?")	 || w.endsWith("!"))
			{
				out.println("\tNONE");
			}
		}			
	}

	public static void main(String[] args)
	{
		SimpleTokenizer t = new SimpleTokenizer();
		for (String a: args)
		{
			t.printSentences(a, System.out);
		}
	}
}


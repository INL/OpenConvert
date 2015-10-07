package nl.openconvert.converters;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.w3c.dom.Document;

import nl.openconvert.filehandling.DirectoryHandling;
import nl.openconvert.sentence.JVKSentenceSplitter;
import nl.openconvert.sentence.TEISentenceSplitter;
import nl.openconvert.tokenizer.PunctuationTagger;
import nl.openconvert.tokenizer.TEITokenizer;
import nl.openconvert.util.XML;

public class TEI2FOLIA extends SimpleXSLTConversion // not quite ... 	TEISentenceSplitter s = new TEISentenceSplitter(new JVKSentenceSplitter());
{
	TEISentenceSplitter s = new TEISentenceSplitter(new JVKSentenceSplitter());
	TEITokenizer t = new TEITokenizer();
	boolean tokenize = true;
	public TEI2FOLIA()
	{
		super("xsl/tei2folia.xsl");
	}
	@Override
	public void setProperties(Properties p)
	{
		String s = p.getProperty("tokenize", tokenize + "");
		System.err.println("s=" + s);
		s = s.trim();
		if (s != null && (s.equalsIgnoreCase("true") ||  s.equalsIgnoreCase("false")))
		{
			//System.err.println("hih?" + s);
			this.tokenize = Boolean.parseBoolean(s);
		}
		System.err.println("tokenize in TEi2FOLIA set to " + tokenize);
	}
	
	@Override
	public void handleFile(String inFilename, String outFilename) 
	{				
		try 
		{
			Document sourceDocument = null;
			
			if (tokenize)
			{
				sourceDocument = t.getTokenizedDocument(inFilename, this.tokenize); 
				new PunctuationTagger().tagPunctuation(sourceDocument);
				s.splitSentences(sourceDocument);
			} else
			{
				sourceDocument = XML.parse(inFilename);
			}
		
			System.err.println("finished tokenizing " + inFilename);
			
			Document teiDocument  = transformer.transformDocument(sourceDocument);
			PrintStream pout = new PrintStream(new FileOutputStream(outFilename));
			pout.print(XML.documentToString(teiDocument));
			pout.close();
		} catch (Exception e) 
		{
			e.printStackTrace
			();
			
		}	
	}
	public static void main(String[] args)
	{
		TEI2FOLIA x = new TEI2FOLIA();
		DirectoryHandling.tagAllFilesInDirectory(x, args[0], args[1]);
		//x.dinges("/mnt/Projecten/Taalbank/Werkfolder_Redactie/Jesse/Projecten/Papiamento/Mosaiko 5 HV Kap 1 vershon 2012 10 28.doc");
	}
}

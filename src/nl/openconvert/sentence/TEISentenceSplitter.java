package nl.openconvert.sentence;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import nl.openconvert.tei.TEITagClasses;
import nl.openconvert.tokenizer.TEITokenizer;
import nl.openconvert.util.Proxy;
import nl.openconvert.util.XML;
import nl.openconvert.filehandling.DirectoryHandling;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;




public class TEISentenceSplitter implements nl.openconvert.filehandling.SimpleInputOutputProcess
{
	SentenceSplitter splitter=null;
	private Properties properties;
	boolean tokenize = true;
	
	public TEISentenceSplitter(SentenceSplitter splitter)
	{
		this.splitter = splitter;
	}
	
	// Add default constructor!
	public TEISentenceSplitter()
	{
		splitter = new JVKSentenceSplitter();
	}
	
	public void splitSentences(Document d)
	{
		TEITokenStream t = new TEITokenStream(d);
		splitter.split(t);
		t.tagSentences();
		if (!allWordsAreInSentences(d))
		{
			System.err.println("Failed to wrap all words!");
		}
	}
	
	

	@Override
	public void handleFile(String in, String out) 
	{
		// TODO Auto-generated method stub
		
		
		Document d = null;
		
		if (this.tokenize)
		{
			TEITokenizer tok = new TEITokenizer();
			d = tok.getTokenizedDocument(in, true);
		} else
		{
			try {
				d = XML.parse(in);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.splitSentences(d);
		
		try 
		{
			PrintStream pout = new PrintStream(new FileOutputStream(out));
			pout.print(XML.documentToString(d));
			pout.close();
		} catch (FileNotFoundException e) 
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
	
	public static boolean allWordsAreInSentences(Document d)
	{
		return allWordsAreInSentences(d.getDocumentElement());
	}
	
	static boolean allWordsAreInSentences(Element e)
	{
		if (e.getNodeName().equals("s"))
			return true;
		boolean ok = true;
	
		List<Element> C = XML.getAllSubelements(e, false);
		for (Element c: C)
		{
			if (TEITagClasses.isTokenElement(c))
			   return false;
			if (!allWordsAreInSentences(c))
				return false;
		}
		return true;
	}

	public static void main(String[] args)
	{
		Proxy.setProxy();
		TEISentenceSplitter s = new TEISentenceSplitter(new JVKSentenceSplitter());
		nl.openconvert.util.Options options = new nl.openconvert.util.Options(args);
        args = options.commandLine.getArgs();
        s.tokenize = options.getOptionBoolean("tokenize", true);
		DirectoryHandling.tagAllFilesInDirectory(s, args[0], args[1]);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
}

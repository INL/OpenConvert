package nl.openconvert.tokenizer;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.w3c.dom.Document;

//import nl.namescape.sentence.JVKSentenceSplitter;
//import nl.namescape.sentence.TEISentenceSplitter;
//import nl.namescape.tagging.ImpactTaggingClient;
import nl.openconvert.tei.TEITagClasses;
import nl.openconvert.util.XML;
import nl.openconvert.filehandling.*;


public class TEIUntokenizer implements SimpleInputOutputProcess
{
	private Properties properties;
	@Override
	public void handleFile(String in, String out) 
	{
	

		try 
		{
			Document d = XML.parse(in);
			TEITagClasses.removeTokenization(d);
			PrintStream pout = new PrintStream(new FileOutputStream(out));
			pout.print(XML.documentToString(d));
			pout.close();
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	@Override
	public void setProperties(Properties properties) 
	{
		// TODO Auto-generated method stub
		this.properties = properties;
	}
	public static void main(String[] args)
	{
		TEIUntokenizer b = new TEIUntokenizer();
		DirectoryHandling.tagAllFilesInDirectory(b, args[0], args[1]);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
}

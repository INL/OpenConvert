package nl.openconvert.converters;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

//import nl.namescape.sentence.JVKSentenceSplitter;
//import nl.namescape.sentence.TEISentenceSplitter;
//import nl.namescape.tokenizer.TEITokenizer;



import nl.openconvert.filehandling.DirectoryHandling;
import nl.openconvert.filehandling.MultiThreadedFileHandler;
import nl.openconvert.filehandling.SimpleInputOutputProcess;
import nl.openconvert.util.Resource;
import nl.openconvert.util.XML;
import nl.openconvert.util.XSLTTransformer;

import org.w3c.dom.*;

public class Doc2TEI implements SimpleInputOutputProcess
{
	Doc2HTML converter = new Doc2HTML();
	XSLTTransformer transformer;
	public Doc2TEI()
	{
		try
		{
			transformer = new XSLTTransformer((new Resource()).openStream("xsl/html2tei.xsl"));
		} catch (Exception e)
		{
			
		}
	}
	
	@Override
	public void handleFile(String docFile, String outFilename) 
	{
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		Document htmlDocument = Doc2HTML.Word2HtmlDocument(docFile);
		//System.err.println(XML.documentToString(htmlDocument));
		Document teiDocument  = transformer.transformDocument(htmlDocument);
		
		
		try 
		{
			PrintStream pout = new PrintStream(new FileOutputStream(outFilename));
			pout.print(XML.documentToString(teiDocument));
			pout.close();
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace
			();
			
		}	
	}

	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args)
	{
		Doc2TEI x = new Doc2TEI();
		
		//DirectoryHandling.tagAllFilesInDirectory(x, args[0], args[1]);
		//MultiThreadedFileHandler m = new MultiThreadedFileHandler(x,nThreads);
		System.err.println("Start conversion from " + args[0] + " to " + args[1]);
		
		DirectoryHandling.traverseDirectory(x, new File(args[0]), new File(args[1]), null);
		//x.dinges("/mnt/Projecten/Taalbank/Werkfolder_Redactie/Jesse/Projecten/Papiamento/Mosaiko 5 HV Kap 1 vershon 2012 10 28.doc");
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}
}

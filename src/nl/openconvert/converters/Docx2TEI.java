package nl.openconvert.converters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

import nl.openconvert.filehandling.DirectoryHandling;
import nl.openconvert.filehandling.SimpleInputOutputProcess;
import nl.openconvert.util.Resource;
import nl.openconvert.util.XML;
import nl.openconvert.util.XSLTTransformer;

import org.w3c.dom.Document;

public class Docx2TEI implements SimpleInputOutputProcess
{
	Docx2HTML converter = new Docx2HTML();
	XSLTTransformer transformer;
	public Docx2TEI()
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
		//System.err.println(docFile);
		try
		{
			File tempFile = File.createTempFile("docx.", ".html.tmp");
			tempFile.deleteOnExit();
			Docx2HTML.createHTML(docFile, tempFile.getCanonicalPath());
			new HTML2TEI().handleFile(tempFile.getCanonicalPath(), outFilename);
			tempFile.delete();

		} catch (Exception e)
		{
			System.err.println("Conversion error on " + docFile);
			e.printStackTrace();
		}

	}
	@Override
	public void setProperties(Properties properties)
	{
		// TODO Auto-generated method stub

	}

	public static void main(String[] args)
	{
		Docx2TEI x = new Docx2TEI();

		//DirectoryHandling.tagAllFilesInDirectory(x, args[0], args[1]);
		//MultiThreadedFileHandler m = new MultiThreadedFileHandler(x,nThreads);
		//System.err.println("Start tagging from " + args[2] + " to " + args[3]);

		DirectoryHandling.traverseDirectory(x, new File(args[0]), new File(args[1]), null);
		//x.dinges("/mnt/Projecten/Taalbank/Werkfolder_Redactie/Jesse/Projecten/Papiamento/Mosaiko 5 HV Kap 1 vershon 2012 10 28.doc");
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}
}

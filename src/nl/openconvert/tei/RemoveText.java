package nl.openconvert.tei;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

import nl.openconvert.converters.Doc2TEI;
import nl.openconvert.filehandling.DirectoryHandling;
import nl.openconvert.filehandling.SimpleInputOutputProcess;
import nl.openconvert.util.XML;

import org.w3c.dom.*;

public class RemoveText implements SimpleInputOutputProcess
{
	public void removeText(Document d)
	{
		Element root = d.getDocumentElement();
		List<Element> textElements = XML.getElementsByTagname(root, "text", false);
		for (Element t: textElements)
		{
			List<Element> subElements = XML.getAllSubelements(t, false);
			for (Element s: subElements)
				t.removeChild(s);
		}
	}

	@Override
	public void handleFile(String inFilename, String outFilename)
	{
		// TODO Auto-generated method stub
		try
		{
			Document d = XML.parse(inFilename);
			removeText(d);
			PrintStream pout = new PrintStream(new FileOutputStream(outFilename));
			pout.print(XML.documentToString(d));
			pout.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void setProperties(Properties properties)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args)
	{
		RemoveText x = new RemoveText();
		
		//DirectoryHandling.tagAllFilesInDirectory(x, args[0], args[1]);
		//MultiThreadedFileHandler m = new MultiThreadedFileHandler(x,nThreads);
		System.err.println("Start conversion from " + args[0] + " to " + args[1]);
		
		DirectoryHandling.traverseDirectory(x, new File(args[0]), new File(args[1]), null);
		//x.dinges("/mnt/Projecten/Taalbank/Werkfolder_Redactie/Jesse/Projecten/Papiamento/Mosaiko 5 HV Kap 1 vershon 2012 10 28.doc");
	}
}

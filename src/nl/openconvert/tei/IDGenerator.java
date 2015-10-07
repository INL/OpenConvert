package nl.openconvert.tei;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import nl.openconvert.filehandling.DoSomethingWithFile;
//import nl.openconvert.sentence.JVKSentenceSplitter;
//import nl.openconvert.sentence.TEISentenceSplitter;
import nl.openconvert.tei.TEITagClasses;
//import nl.openconvert.tokenizer.TEITokenizer;
import nl.openconvert.util.XML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.util.Set;

public class IDGenerator implements nl.openconvert.filehandling.SimpleInputOutputProcess, DoSomethingWithFile
{
	boolean tagParts = true;
	private Properties properties;
	PrintStream stdout = new PrintStream(System.out);
	boolean printLemmata = false;
	boolean printIds = false;
	boolean onlyDecentSentences = false;
	Set<String> tagNames = new HashSet<String>();
	
	
	@Override
	public void handleFile(String in, String out) 
	{
		try 
		{
			Document d = XML.parse(in);
			TEITagClasses.assignIds(d, tagNames);
			PrintStream pout = new PrintStream(new FileOutputStream(out));
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
		this.properties = properties;
	}
	

	@Override
	public void handleFile(String fileName) 
	{
		try 
		{
			Document d = XML.parse(fileName);
			
			
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		nl.openconvert.util.Options options = new nl.openconvert.util.Options(args)
		{
			@Override
			public void defineOptions()
			{
				super.defineOptions();
				options.addOption("n", "nThreads", true, "Number of threads");
				options.addOption("i", "printIds", true, "Print Ids");
			}
		};
		IDGenerator spl = new IDGenerator();
		spl.tagNames.add("s");
        args = options.commandLine.getArgs();
		if (args.length > 1)
		{
			nl.openconvert.filehandling.DirectoryHandling.tagAllFilesInDirectory(spl, args[0], 
				args[1]);
		} else
		{
			nl.openconvert.filehandling.DirectoryHandling.traverseDirectory(spl, args[0]);
		}
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}
}

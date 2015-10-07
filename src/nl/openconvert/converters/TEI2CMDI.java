package nl.openconvert.converters;


import java.util.*;
import java.io.*;

import nl.openconvert.filehandling.DirectoryHandling;
import nl.openconvert.filehandling.SimpleInputOutputProcess;
//import nl.namescape.tei.EPubConverter;
import nl.openconvert.util.*;

import org.w3c.dom.*;

public class TEI2CMDI implements SimpleInputOutputProcess
{
	String stylesheet = "xsl/TEI2CMDI.xsl";
	
	
	public void handleFile(String inFilename, String outFilename) 
	{
		InputStream is = new Resource().openStream(stylesheet);
		XSLTTransformer t = new XSLTTransformer(is);
		
		try
		{
			Document transformedDocument = t.transformDocument(XML.parse(inFilename));
			try 
			{
				PrintStream pout = new PrintStream(new FileOutputStream(outFilename));
				pout.print(XML.documentToString(transformedDocument));
				pout.close();
			} catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}	
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
	
	public static void main(String[] args)
	{
		nl.openconvert.util.Options options = new nl.openconvert.util.Options(args);
        args = options.commandLine.getArgs();
        TEI2CMDI b = new TEI2CMDI();
		
		DirectoryHandling.tagAllFilesInDirectory(b, args[0], args[1]);
	}



	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}
}

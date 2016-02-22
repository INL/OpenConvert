package nl.openconvert.converters;

import java.util.Properties;
import java.io.*;

import nl.openconvert.filehandling.ConversionException;
import nl.openconvert.filehandling.SimpleInputOutputProcess;

public class ExtensionBasedConversion implements SimpleInputOutputProcess
{

	@Override
	public void handleFile(String inFilename, String outFilename)  throws ConversionException
	{
		// TODO Auto-generated method stub
		
		String from = extension(inFilename,".");
		String to =  extension(outFilename, ".");
		
		if (from.equalsIgnoreCase("xml"))
		{
			from = extension(inFilename.replaceAll(".[Xx][Mm][Ll]$", "" ),".");
		}
		
		if (to.equalsIgnoreCase("xml"))
		{
			to = extension(outFilename.replaceAll(".[Xx][Mm][Ll]$", "" ),".");
		}
		
		SimpleInputOutputProcess x = OpenConvert.getConverter(to, from, true);
		
		if (x != null)
		{
			x.handleFile(inFilename, outFilename);
		} else
		{
			System.err.println("No conversion found from "  + from + " to " + to);
		}
	}

	  public String extension(String fullPath, String extensionSeparator ) {
		    int dot = fullPath.lastIndexOf(extensionSeparator);
		    return fullPath.substring(dot + 1);
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

}

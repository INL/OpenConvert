package nl.openconvert.filehandling;

import java.util.Properties;

public interface SimpleInputOutputProcess 
{
	public void handleFile(String inFilename, String outFilename)  throws ConversionException;
	public void setProperties(Properties properties)  throws ConversionException;
	public void close();
}

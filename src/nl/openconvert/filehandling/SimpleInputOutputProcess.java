package nl.openconvert.filehandling;

import java.util.Properties;

public interface SimpleInputOutputProcess 
{
	public void handleFile(String inFilename, String outFilename);
	public void setProperties(Properties properties);
	public void close();
}

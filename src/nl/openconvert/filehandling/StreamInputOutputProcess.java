package nl.openconvert.filehandling;

import java.util.Properties;
import java.io.*;

public interface StreamInputOutputProcess 
{
	public void handleFile(InputStream inFile,  OutputStream outFile);
	public void setProperties(Properties properties);
}

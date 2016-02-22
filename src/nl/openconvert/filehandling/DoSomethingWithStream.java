package nl.openconvert.filehandling;

import java.io.InputStream;

public interface DoSomethingWithStream 
{
	public void handleFile(InputStream stream)  throws ConversionException;
}

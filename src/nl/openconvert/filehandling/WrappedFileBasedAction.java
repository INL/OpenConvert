package nl.openconvert.filehandling;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class WrappedFileBasedAction implements DoSomethingWithStream
{
	DoSomethingWithFile base = null;

	
	public WrappedFileBasedAction(DoSomethingWithFile s)
	{
		this.base = s;
	}
	
	@Override
	public void handleFile(InputStream inStream)  throws ConversionException
	{
		// TODO Auto-generated method stub
		try
		{
			File fin = File.createTempFile("bla", "in");
			
			
			fin.deleteOnExit();
			
			fin.delete();
		
			
			Path pin = fin.toPath();

			
			Files.copy(inStream, pin);
			base.handleFile(fin.getCanonicalPath());
			

			//outStream.flush();
			//outStream.close();
			//System.err.println(fin.getCanonicalPath());
			//System.err.println(fout.getCanonicalPath());
			fin.delete();
		
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void setProperties(Properties properties)
	{
		// TODO Auto-generated method stub
		//base.setProperties(properties);
	}
}

package nl.openconvert.filehandling;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class WrappedFileBasedConverter implements StreamInputOutputProcess
{
    SimpleInputOutputProcess base = null;

	
	public WrappedFileBasedConverter(SimpleInputOutputProcess s)
	{
		this.base = s;
	}
	
	@Override
	public void handleFile(InputStream inStream, OutputStream outStream)
	{
		// TODO Auto-generated method stub
		try
		{
			File fin = File.createTempFile("bla", "in");
			File fout = File.createTempFile("bla", "out");
			
			fin.deleteOnExit();
			fout.deleteOnExit();
			fin.delete();
			fout.delete();
			
			Path pin = fin.toPath();
			Path pout = fout.toPath();
			
			Files.copy(inStream, pin);
			base.handleFile(fin.getCanonicalPath(), fout.getAbsolutePath());
			
			Files.copy(pout, outStream); // ahem?
			outStream.close();
			//outStream.flush();
			//outStream.close();
			//System.err.println(fin.getCanonicalPath());
			//System.err.println(fout.getCanonicalPath());
			fin.delete();
			fout.delete();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setProperties(Properties properties)
	{
		// TODO Auto-generated method stub
		base.setProperties(properties);
	}
}

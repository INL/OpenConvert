package nl.openconvert.filehandling;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class OutputToZip implements SimpleInputOutputProcess
{
	SimpleInputOutputProcess base;
	String archiveName;
	ZipOutputStream zipOutputStream = null;

	public OutputToZip(String archiveName, SimpleInputOutputProcess siop)
	{
		this.base = siop;
		this.archiveName = archiveName;
		try
		{
			zipOutputStream = new ZipOutputStream(new FileOutputStream(archiveName));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void close()
	{
		try
		{
			zipOutputStream.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void handleFile(String inFilename, String outFilename)
	{	
		try
		{
			File tempContent= File.createTempFile("tempOut", ".temp");
			tempContent.deleteOnExit();
			
			// this will NOT work when the base handler is asynchronous...
			// in that case zipping has to wait for the task to finish
			
			base.handleFile(inFilename, tempContent.getCanonicalPath());
			FileInputStream fis = new FileInputStream(tempContent);
			DataInputStream dis = new DataInputStream(fis);
			saveToZip(zipOutputStream, outFilename, dis);
			tempContent.delete();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setProperties(Properties properties)
	{

	}

	public synchronized void saveToZip(ZipOutputStream zippie, String entryName, DataInputStream in) throws IOException
	{
		try
		{
			zippie.putNextEntry(new ZipEntry(entryName));
			writeData(in,zippie);
			zippie.closeEntry();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			in.close();
		}
	}
	
	private static int writeData(DataInputStream in, OutputStream fOut) throws IOException 
	{
		byte[] buffer = new byte[1024 * 1024];
		int bytesRead = 0;
		int totalBytesRead = 0;
		while ((bytesRead = in.read(buffer)) != -1)
		{	
			totalBytesRead += bytesRead;


			fOut.write(buffer, 0, bytesRead);

		}
		return totalBytesRead;
	}
}

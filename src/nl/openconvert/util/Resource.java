package nl.openconvert.util;
import java.io.*;
import java.util.Enumeration;

public class Resource

{

	/** Creates a new instance of Resource **/
	
	public static String resourceFolder = "resources";
	public static String yetAnotherFolder = 
			"N:/Impact/ImpactIR/OCRIRevaluatie/IREval/workspace/ImpactIR/resources";
	
	static String[] foldersToTry = {resourceFolder, yetAnotherFolder};
	
	public Resource()
	{

	}

	public  InputStream openStream(String s)
	{
		try 
		{
			// first try to read file from local file system
			for (String f: foldersToTry)
			{
				File file = new File(f + "/"+ s);
				if (file.exists())
				{
					return new FileInputStream(file);
				}
			}
			// next try for files included in jar
			try
			{
				InputStream is = 
						this.getClass().getResourceAsStream("/"+ s);  
				if (is != null)
				{
					System.err.println("found in jar!!");
					return is;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			} 
			ClassLoader loader = getClass().getClassLoader();
			//Enumeration<java.net.URL> urls = loader.getResources(arg0);
			java.net.URL url = getClass().getClassLoader().getResource(resourceFolder + "/" + s);
			System.err.println("jar url " + url);
			// or URL from web
			if (url == null) url = new java.net.URL(s);
			java.net.URLConnection site = url.openConnection();
			InputStream is = site.getInputStream();
			return is;
		} catch (IOException ioe)
		{
			System.err.println("Could not open " + s);
			return null;
		}
	}
	
	public Reader openFile(String s)
	{
		InputStream is = openStream(s);
		if (is != null)
			return new InputStreamReader(is);
		return null;
	}
	public static Reader openResourceFile(String s)
	{
		return new  Resource().openFile(s);
	}
	
	public static String getStringFromFile(String fileName)
	{
		String r="";
		
		try
		{
			BufferedReader reader = new BufferedReader((new Resource()).openFile(fileName));
			String s;
		
			while ((s = reader.readLine()) != null)
			{
				r += s + "\n";
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return r;
	}
}

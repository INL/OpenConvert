package nl.openconvert.filehandling;

import java.util.*;
import java.net.URI;
import java.nio.file.*;
import java.io.*;

// should use directorystream stuff
public class TestZipFS
{
	public static void main(String[] args)
	{
		Map<String,String> env = new HashMap<String,String>();
		env.put("create", "true");
		//Path zipfile = Paths.get("/codeSamples/zipfs/zipfstest.zip");
		//FileSystem fs = FileSystems.newFileSystem(zipfile, env, null);
		URI uri = URI.create("jar:file:/" + args[0]);
		System.err.println(uri);
		try
		{
			FileSystem zipfs = FileSystems.newFileSystem(uri,env);
			Path pathInZipfile = zipfs.getPath("/testFile.txt");
			OutputStream outStream = Files.newOutputStream(pathInZipfile);
			//System.err.println("apply conversion " + p.getClass() + ":  "  + inputPath +   " -->  " + outputPath);
			//p.handleFile(inStream,outStream) ;
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outStream));
			bw.write("hallo heren, staat hier dan echt niks in???");
			bw.close();
			//outStream.close();
			zipfs.close();
			if (false) for (Path p: zipfs.getRootDirectories())
			{
				System.err.println(p);
				DirectoryStream<Path> stream = Files.newDirectoryStream(p);
				for (Path p1: stream)
				{
					System.err.println(p1);
					if (Files.isDirectory(p1, null))
					{
						DirectoryStream<Path> stream1 = Files.newDirectoryStream(p1);
					} else if (Files.isRegularFile(p1, null))
					{
						System.err.println("regular file:" + p1);
						InputStream inputStream = Files.newInputStream(p1);
						BufferedInputStream b = new BufferedInputStream(inputStream);
						BufferedReader br = new BufferedReader(new InputStreamReader(b));
						String s;
						while ((s = br.readLine()) != null)
						{
							System.out.println(s);
						}
						//File f = p1.toFile();
					}
				}
				//System.err.println(f);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

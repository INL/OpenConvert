package nl.openconvert.filehandling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import nl.openconvert.converters.ALTO2TEI;
import nl.openconvert.converters.DocumentPerLineOutput;

public class PathHandlingNoOutput
{
	public static void traverseDirectory(DoSomethingWithStream  p, Path inputPath) 
	{	
		if (!Files.exists(inputPath))
		{
			System.err.println("bestaat NIET? "  + inputPath);
		} else if (Files.isRegularFile(inputPath) && inputPath.toString().endsWith(".zip"))
		{
			System.err.println("Hah! input zip!!!");
			Map<String,String> env = new HashMap<String,String>();
			URI uri = URI.create("jar:file:" + inputPath.toString().replaceAll("\\\\", "/"));
			System.err.println("URI: "  + uri);
			try
			{
				FileSystem zipfs = FileSystems.newFileSystem(uri,env);
				for (Path r: zipfs.getRootDirectories())	
				{
					traverseDirectory(p, r);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}  else if (Files.isRegularFile(inputPath) )
		{
			try
			{
				InputStream inStream = Files.newInputStream(inputPath);
				//OutputStream outStream = Files.newOutputStream(outputPath);
				//System.err.println("apply conversion " + p.getClass() + ":  "  + inputPath +   " -->  " + outputPath);
				try 
				{
					p.handleFile(inStream) ;
				} catch (Exception e)
				{
					System.err.println("Exception in conversion " + p.getClass());
					e.printStackTrace();
				}
				//BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outStream));
				//bw.write("hallo heren, staat hier dan echt niks in???");
				//bw.close();
				inStream.close();
				//outStream.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		} else if (Files.isRegularFile(inputPath)) // boven behandeld...
		{
			System.err.println("Huh? ");
		} else if (Files.isDirectory(inputPath))
		{
			System.err.println("directory : " + inputPath);
		
			try
			{
				DirectoryStream<Path> dir = Files.newDirectoryStream(inputPath);
				for (Path entry: dir)
				{
					String lastPart = entry.getName(entry.getNameCount()-1).toString();
					
					if (Files.isDirectory(entry))
					{
						// create outpu
						try
						{
						
							traverseDirectory(p, entry);
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					} else
					{
						// make sure parent dir exists...
						traverseDirectory(p, entry);
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args)
	{
		DocumentPerLineOutput a2t = new DocumentPerLineOutput();
		DoSomethingWithStream  s = new WrappedFileBasedAction(a2t);
		Path in = Paths.get(args[0]);
		traverseDirectory(s,in);
	}
}

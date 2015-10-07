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

public class PathHandling
{
	public static void traverseDirectory(StreamInputOutputProcess p, Path inputPath, 
			Path outputPath, FileFilter fileFilter) 
	{	
		System.err.println(inputPath + " -->  " +  outputPath);
		if (!Files.exists(inputPath))
		{
			System.err.println("bestaat NIET? "  + inputPath);
		}
		if (outputPath.toString().endsWith(".zip"))
		{
			System.err.println("Hah! output zip!!!" + outputPath);
			Map<String,String> env = new HashMap<String,String>();
			env.put("create", "true");
			URI uri = URI.create("jar:file:" + outputPath.toString().replaceAll("\\\\", "/"));
			try
			{
				FileSystem zipfs = FileSystems.newFileSystem(uri,env);
				Path pathInZipfile = zipfs.getPath("/");
				Files.createDirectories(pathInZipfile);
				traverseDirectory(p, inputPath, pathInZipfile, fileFilter);
				zipfs.close(); // dit kan niet bij multithreaded toepassing, dan kan je pas achteraf sluiten
			} catch (Exception e)
			{
				e.printStackTrace();
			}
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
					traverseDirectory(p, r, outputPath, fileFilter);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}  else if (Files.isRegularFile(inputPath) && (!Files.exists(outputPath)||Files.isRegularFile(outputPath) ) )
		{
			try
			{
				InputStream inStream = Files.newInputStream(inputPath);
				OutputStream outStream = Files.newOutputStream(outputPath);
				System.err.println("apply conversion " + p.getClass() + ":  "  + inputPath +   " -->  " + outputPath);
				try 
				{
					p.handleFile(inStream,outStream) ;
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
			if (!Files.exists(outputPath))
			{
				try
				{
					System.err.println("creating directories for: " + outputPath);
					Files.createDirectories(outputPath);
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try
			{
				DirectoryStream<Path> dir = Files.newDirectoryStream(inputPath);
				for (Path entry: dir)
				{
					String lastPart = entry.getName(entry.getNameCount()-1).toString();
					Path oPath = outputPath.getFileSystem().getPath(outputPath.toString(), lastPart);
					System.err.println("oPath: " + oPath);
					if (Files.isDirectory(entry))
					{
						// create outpu
						try
						{
							Files.createDirectories(oPath);
							traverseDirectory(p, entry, oPath, fileFilter);
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					} else
					{
						// make sure parent dir exists...
						traverseDirectory(p, entry, oPath, fileFilter);
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
		ALTO2TEI a2t = new ALTO2TEI();
		StreamInputOutputProcess s = new WrappedFileBasedConverter(a2t);
		Path in = Paths.get(args[0]);
		Path out = Paths.get(args[1]);
		traverseDirectory(s,in,out,null);
	}
}

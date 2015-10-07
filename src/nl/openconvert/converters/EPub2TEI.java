package nl.openconvert.converters;


import nl.openconvert.util.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Properties;

import nl.openconvert.util.Options;
import nl.openconvert.util.Proxy;
import nl.openconvert.util.TagSoupParser;
import nl.openconvert.util.XML;
import nl.openconvert.util.XSLTTransformer;
import nl.openconvert.filehandling.*;

import java.util.zip.*;
import org.w3c.dom.*;

public class EPub2TEI implements SimpleInputOutputProcess
{
	boolean cleanupUnzippedFiles = true;
	static
	{
		Proxy.setProxy();
	}

	//String xsltPath =  "/mnt/Projecten/Taalbank/Namescape/Corpus-Gutenberg/Data/Epub/test.xsl" ; // "/mnt/Projecten/Taalbank/Namescape/Tools/SrcIsaac/oxygen/epub2tei.flat.xsl";
	XSLTTransformer transformer = null;
	
	public EPub2TEI()
	{
		Proxy.setProxy();
		InputStream xslStream = new Resource().openStream("xsl/epub2tei.xsl");
		transformer = new XSLTTransformer(xslStream);
	}
	
	public static  void createPath(String fileName)
	{
		String [] parts  = fileName.split(File.separator);
		String path = parts[0];
		for (int i=1; i < parts.length; i++)
		{
			File f = new File(path);
			if (!f.exists())
			{
				f.mkdir();
			}
			path = path + "/" + parts[i];
		}
	}
	
	public static void getZipFiles(String filename, String destinationFolder)
	{
		try
		{
			byte[] buf = new byte[1024];
			ZipInputStream zipInputStream = null;
			ZipEntry zipentry;
			zipInputStream = new ZipInputStream(new FileInputStream(filename));

			zipentry = zipInputStream.getNextEntry();
			while (zipentry != null) 
			{ 
				//for each entry to be extracted
				String entryName = zipentry.getName();
				System.out.println("entryname "+entryName);
				
				int n; FileOutputStream fileOutputStream;
				
				if (zipentry.isDirectory())
				{

				} else
				{
					createPath(destinationFolder + "/" + entryName);
					fileOutputStream = new FileOutputStream(destinationFolder + "/" + entryName);             

					while ((n = zipInputStream.read(buf, 0, 1024)) > -1)
						fileOutputStream.write(buf, 0, n);

					fileOutputStream.close(); 
				}
				zipInputStream.closeEntry();
				
				// bad test on extension: should parse content.opf to get media type for each entry!
				
				if (entryName.toLowerCase().endsWith("html") || entryName.toLowerCase().endsWith("htm"))  // cleanup
				{
					cleanupHTML(destinationFolder + "/" + entryName);
				}
				zipentry = zipInputStream.getNextEntry();

			}//while

			zipInputStream.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void renameNamespaceRecursive(Document doc, Node node,
			String namespace) 
	{

		if (node.getNodeType() == Node.ELEMENT_NODE) 
		{
			//  System.out.println("renaming type: " + node.getClass()	+ ", name: " + node.getNodeName());
			doc.renameNode(node, namespace, node.getNodeName());
		}

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); ++i) 
		{
			renameNamespaceRecursive(doc, list.item(i), namespace);
		}
	}
	private static void cleanupHTML(String entryName) // this should be done better -- jtidy to remove textnodes hanging around, etc??
	{
		// TODO Auto-generated method stub
		Document d = TagSoupParser.parse2DOM(entryName);
		renameNamespaceRecursive(d,d.getDocumentElement(),"http://www.w3.org/1999/xhtml");
		try
		{
			File f = new File(entryName);
			f.delete();
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(f), "UTF8");
			out.write(XML.NodeToString(d.getDocumentElement()));
			out.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void handleFile(String inFilename, String outFileName) 
	{
		// unzip the zip to a temp directory
		String tempDir = nl.openconvert.util.IO.getTempDir();
		try
		{
			File unzipTo = File.createTempFile("unzip.", ".dir");
			
			unzipTo.delete();
			unzipTo.mkdir();
			if (cleanupUnzippedFiles) unzipTo.deleteOnExit();
			try
			{
				getZipFiles(inFilename, unzipTo.getPath());
				//File[] modelFiles = unzipTo.listFiles();
				transformer.setParameter("unzipTo", unzipTo.getPath());
				transformer.transformFile(unzipTo.getPath() + "/META-INF/container.xml" , outFileName);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			if (cleanupUnzippedFiles) deleteRecursively(unzipTo);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void setProperties(Properties properties) 
	{
		// TODO Auto-generated method stub

	}

	private static void deleteRecursively( File file )
	{
		if (!file.exists())
		{
			return;
		}

		if ( file.isDirectory() )
		{
			for ( File child : file.listFiles() )
			{
				deleteRecursively( child );
			}
		}
		if ( !file.delete() )
		{
			throw new RuntimeException(
					"Couldn't empty database. Offending file:" + file );
		}
	}
	
	public static void main(String[] args)
	{
		nl.openconvert.util.Options options = new nl.openconvert.util.Options(args);
        args = options.commandLine.getArgs();
		EPub2TEI b = new EPub2TEI();
		
		DirectoryHandling.tagAllFilesInDirectory(b, args[0], args[1]);
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}

}
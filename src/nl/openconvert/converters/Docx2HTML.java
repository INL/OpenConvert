package nl.openconvert.converters;
/**
 * Kijk ook naar docx4j spul
 * Conversie via open office zoals door oxgarage zou beter kunnen zijn....
 */
import nl.openconvert.filehandling.SimpleInputOutputProcess;
import nl.openconvert.util.XML;

import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
//import org.apache.poi.xwpf.converter.xthml.*;
//import org.apache.poi.xwpf.converter.xthml.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

public class Docx2HTML  implements SimpleInputOutputProcess
{
	public static void main(String[] args) 
	{
		createHTML(args[0], args[1]);
	}

	public static Document Word2HtmlDocument(String docFile)
	{
		File tempFile;
		try
		{
			tempFile = File.createTempFile(docFile, ".html.tmp");
			tempFile.deleteOnExit();
			createHTML(docFile, tempFile.getCanonicalPath());
			Document d = XML.parse( tempFile.getCanonicalPath());
			tempFile.delete();
			return d;
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		} catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	static void createHTML(String inFile, String outFile) 
	{
		try {
			long start = System.currentTimeMillis();

			// 1) Load DOCX into XWPFDocument
			InputStream is = new FileInputStream(new File(inFile));
			XWPFDocument document = new XWPFDocument(is);

			// 2) Prepare Html options
			XHTMLOptions options = XHTMLOptions.create();
			
			// 3) Convert XWPFDocument to HTML
			
			OutputStream out = new FileOutputStream(outFile);
			XHTMLConverter.getInstance().convert(document, out, options);

			// System.err.println("Generate html/HelloWorld.html with "+ (System.currentTimeMillis() - start) + "ms");

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleFile(String inFilename, String outFilename)
	{
		// TODO Auto-generated method stub
			createHTML(inFilename, outFilename);
	}

	@Override
	public void setProperties(Properties properties)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}
}


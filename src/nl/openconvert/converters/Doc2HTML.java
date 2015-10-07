package nl.openconvert.converters;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.openconvert.filehandling.SimpleInputOutputProcess;
import nl.openconvert.util.XML;

import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.converter.WordToHtmlUtils;
//import org.apache.poi.xwpf; // xwpf is voor word 2010 etc.
//import org.apache.poi.
import org.w3c.dom.Document;

import java.util.*;
public class Doc2HTML implements SimpleInputOutputProcess
{	
	public static Document Word2HtmlDocument(String docFile)
	{
		try
		{
			HWPFDocumentCore wordDocument = WordToHtmlUtils.loadDoc(new FileInputStream(docFile));

			WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
					DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument());
			wordToHtmlConverter.processDocument(wordDocument);
			Document htmlDocument = wordToHtmlConverter.getDocument();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DOMSource domSource = new DOMSource(htmlDocument);
			DOMResult domResult = new DOMResult();

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();
			
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(OutputKeys.METHOD, "html");
			serializer.transform(domSource, domResult);
			
			out.close();
			//System.err.println(domResult.getNode());
			return (Document) domResult.getNode();
			
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	

	
	
	public static void main(String[] args)
	{
		Document d = Doc2HTML.Word2HtmlDocument(args[0]);
		System.out.println(XML.documentToString(d));
	}




	@Override
	public void handleFile(String inFilename, String outFilename)
	{
		// TODO Auto-generated method stub
		Document htmlDocument = Doc2HTML.Word2HtmlDocument(inFilename);
		try 
		{
			PrintStream pout = new PrintStream(new FileOutputStream(outFilename));
			pout.print(XML.documentToString(htmlDocument));
			pout.close();
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace
			();
		}	
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

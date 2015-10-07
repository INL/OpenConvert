package nl.openconvert.util;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import nl.openconvert.filehandling.DirectoryHandling;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Wrapper voor de xsltc engine. Deze klasse zet met behulp van de XSLT engine (o.a. Transformer.java) een XML
 * artikel om in HTML.
 */

public class XSLTTransformer implements nl.openconvert.filehandling.SimpleInputOutputProcess
{
	/** our Transformer object */
	private Transformer transformer = null;
	private TransformerFactory tFactory;
	private String xslInUri = null;
	private boolean useSaxon = true;
	private boolean alwaysReload = true;
	private Properties properties;
	InputStream xslReader = null;
	int jobId=0;
	public static String inputEncoding = "UTF-8";
	
	private synchronized void nextJob()
	{
		jobId++;
		setParameter("jobNumber","" + jobId);
	}
	
	public XSLTTransformer(String xslInUri)
	{
		String key = "javax.xml.transform.TransformerFactory";
		// N.B. Aangepast ivm nieuwe Javaversie (1.5 of 1.6), was vroeger:
		// com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl
		String value = "org.apache.xalan.processor.TransformerFactoryImpl";
		if (useSaxon)
		{
			value = "net.sf.saxon.TransformerFactoryImpl";
		}
		Properties props = System.getProperties();
		props.put(key, value);
		System.setProperties(props);
		tFactory = TransformerFactory.newInstance();
		this.xslInUri = xslInUri;
		loadStylesheet();
		if (this.transformer == null)
		{
			System.err.println("EEK!");
			System.exit(1);
		}
	}

	public XSLTTransformer(InputStream xslReader)
	{
		String key = "javax.xml.transform.TransformerFactory";
		// N.B. Aangepast ivm nieuwe Javaversie (1.5 of 1.6), was vroeger:
		// com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl
		String value = "org.apache.xalan.processor.TransformerFactoryImpl";
		if (useSaxon)
		{
			value = "net.sf.saxon.TransformerFactoryImpl";
		}
		Properties props = System.getProperties();
		props.put(key, value);
		System.setProperties(props);
		tFactory = TransformerFactory.newInstance();
		
		this.xslReader = xslReader;
		loadStylesheet();
		if (this.transformer == null)
		{
			System.err.println("EEK!");
			System.exit(1);
		}
	}

	private void loadStylesheet() 
	{
		if (xslInUri != null)
		{
			try 
			{
				this.transformer = tFactory.newTransformer(new StreamSource(this.xslInUri));
			} catch (TransformerConfigurationException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		if (xslReader != null)
		{
			try 
			{
				this.transformer = tFactory.newTransformer(new StreamSource(xslReader));
			} catch (TransformerConfigurationException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setProperties(Properties properties) 
	{
		// TODO Auto-generated method stub
		this.properties = properties;
	}
	
	public XSLTTransformer(Transformer transformer)
	{
		this.transformer = transformer;
	}

	public void setParameter(String name, String value)
	{
		transformer.setParameter(name, value);
	}
	/**
	 * Voert de transformatie uit. De input bestaat uit een String met de XML code.
	 * 
	 * @param(STring  instring
	 *            De input string
	 * @param out
	 *            java.io.Writer object Het resultaat van het transformeren van de XML code.
	 * @param licenseAccepted 
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 * @throws IOException
	 */
	
	public void transformString(String instring, Writer out)
		throws TransformerConfigurationException, TransformerException, IOException
	{
		StreamSource source = new StreamSource(new StringReader(instring));
		StreamResult result = new StreamResult(out);
		
		transformer.transform(source, result);
		out.flush();
	}
	

	public void transformFile(String inFileName, OutputStreamWriter out)
	{
		try 
		{
			BufferedReader br = openBufferedTextFile(inFileName);
			// OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outFileName),"UTF-8");
			StreamSource source = new StreamSource(br);
			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
			br.close();
			// out.close();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public Document transformDocument(Document inDocument)
	{
		try 
		{
			DOMSource source = new DOMSource(inDocument);
			DOMResult result = new DOMResult();
			transformer.transform(source, result);
			org.w3c.dom.Node resultNode =  result.getNode();
			DocumentBuilder builder = null;
			try 
			{
				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (ParserConfigurationException ex) {
				ex.printStackTrace();
				System.exit(-1);
			}


			
			//Document doc = builder.newDocument();
			//doc.appendChild(resultNode);
			return (Document) resultNode;
			//System.err.println("RESULT: " + resultNode);
			//System.err.println(XML.NodeToString(resultNode));
			//return resultNode.getOwnerDocument();
		} catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Dit gaat niet goed als de input niet utf8 is i$oM0K
	 */
	public void transformFile(String inFileName, String outFileName)
	{
		try 
		{
			nextJob();
			setParameter("inputFile", inFileName);
			BufferedReader br = openBufferedTextFile(inFileName);
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outFileName),"UTF-8");
			StreamSource source = new StreamSource(br);
			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
			br.close();
			out.close();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * Releases the transformer object to the free pool, to be reused next time.
	 * 
	 */
	public static BufferedReader openBufferedTextFile(String fileName)
	{
		try
		{
			BufferedReader b = 
				new BufferedReader(new InputStreamReader(new FileInputStream(fileName), inputEncoding));
			return b;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	public void release()
	{
	}


	@Override
	public void handleFile(String inFile, String outFile) 
	{
		// TODO Auto-generated method stub
		transformFile(inFile,outFile);
	}
	
	public static void main(String[] args)
	{
		XSLTTransformer p = new XSLTTransformer(args[0]);
		DirectoryHandling.tagAllFilesInDirectory(p, args[1], args[2]);
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}
}


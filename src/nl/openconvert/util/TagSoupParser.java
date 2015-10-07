package nl.openconvert.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;

import nl.openconvert.filehandling.DirectoryHandling;

import org.apache.xalan.xsltc.trax.SAX2DOM;
import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


public class TagSoupParser implements nl.openconvert.filehandling.SimpleInputOutputProcess
{
	static
	{
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
		System.setProperty("http.maxRedirects", "100");
	}
	
	public static Document parse2DOM(String sURL)
	{
		Parser p = new Parser();
		SAX2DOM sax2dom = null;
		org.w3c.dom.Node doc  = null;

		try 
		{ 
			File f = new File(sURL);
			//URL url = new URL(sURL);
			URL url = null;
			if (f.exists())
			{
				url = f.toURI().toURL();
			} else
				url = new URL(sURL);
			
			
			//= f.toURI().toURL();
			//System.err.println(url);
			p.setFeature(Parser.namespacesFeature, false);
			p.setFeature(Parser.namespacePrefixesFeature, false);
			sax2dom = new SAX2DOM();
			p.setContentHandler(sax2dom);
			
			
			p.parse(new InputSource(new InputStreamReader(getInputStream(url),"UTF-8")));
			doc = sax2dom.getDOM();
			//System.err.println(doc);
		} catch (Exception e) 
		{
			// TODO handle exception
			e.printStackTrace();
		}
		return (Document) doc;
	}

	public static InputStream getInputStream(URL url)
	{
		try
		{
			URLConnection urlConnection = url.openConnection();
			InputStream is = urlConnection.getInputStream();
			return is;
		} catch (Exception e)
		{
			return null;
		}
	}
	public static Document parseFromHTMLString(String htmlText)
	{
		Parser p = new Parser();
		SAX2DOM sax2dom = null;
		org.w3c.dom.Node doc  = null;

		try 
		{ 
			
			p.setFeature(Parser.namespacesFeature, false);
			p.setFeature(Parser.namespacePrefixesFeature, false);
			sax2dom = new SAX2DOM();
			p.setContentHandler(sax2dom);
			p.parse(new InputSource(new StringReader(htmlText)));
			doc = sax2dom.getDOM();
			//System.err.println(doc);
		} catch (Exception e) 
		{
			// TODO handle exception
			e.printStackTrace();
		}
		return (Document) doc;
	}
	
	public static Document parsePlainText(String plainText)
	{
		String[] paragraphs = plainText.split("\\s*\n\\s*\n\\s*");
		String html="<html><body><div>";
		for (String p: paragraphs)
		{
			html += "<p>";
			String[] lines = p.split("\\s*\n\\s*");
			for (String l: lines)
			{
				html +=  l + "<br>\n";
			}
			html += "</p>";
		}
		return parseFromHTMLString(html);
	}
	
	private Properties properties;

	@Override
	public void handleFile(String inFileName, String outFileName) 
	{
		// TODO Auto-generated method stub
		try
		{
			Document d = TagSoupParser.parse2DOM(inFileName);
			Element root = d.getDocumentElement();
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outFileName), "UTF8");
			
			out.write(XML.NodeToString(d.getDocumentElement()));
			out.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void setProperties(Properties properties) 
	{
		// TODO Auto-generated method stub
		this.properties = properties;
	}
	
	
	public static void main(String[] args)
	{
		TagSoupParser p = new TagSoupParser();
		
		DirectoryHandling.tagAllFilesInDirectory(p, args[0], args[1]);
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}
}

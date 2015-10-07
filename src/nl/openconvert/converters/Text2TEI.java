package nl.openconvert.converters;

import java.io.*;
import java.util.Properties;

import org.w3c.dom.Document;

import nl.openconvert.filehandling.DirectoryHandling;
import nl.openconvert.filehandling.SimpleInputOutputProcess;
import nl.openconvert.util.TagSoupParser;
import nl.openconvert.util.XML;

public class Text2TEI implements SimpleInputOutputProcess
{

	@Override
	public void handleFile(String inFilename, String outFilename) 
	{
		// TODO Auto-generated method stub	
		// TODO Auto-generated method stub


		String s = slurpFile(inFilename);
		Document teiDocument = parsePlainText(s);
		try 
		{
			PrintStream pout = new PrintStream(new FileOutputStream(outFilename));
			pout.print(XML.documentToString(teiDocument));
			pout.close();
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace
			();

		}	
	}

	public static String slurpFile(String fileName) 
	{
		String s = "";
		try 
		{
			BufferedReader in = 
					new BufferedReader (new InputStreamReader (new FileInputStream (fileName), "UTF8"));

			String l;
			while ((l = in.readLine()) != null)
			{
				s += l + "\n";
			}
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return s;
	}
	@Override
	public void setProperties(Properties properties) 
	{
		// TODO Auto-generated method stub	
	}

	static String plainText = "Hallo meneer\n \nIk weet het wel\nU Het is mooi weer!!";

	public Document parsePlainText(String plainText)
	{
		Document d = new TagSoupParser().parsePlainText(plainText);
		return new HTML2TEI().convertDocument(d);
	}
	public static void main(String[] args)
	{
		Text2TEI x = new Text2TEI();

		DirectoryHandling.tagAllFilesInDirectory(x, args[0], args[1]);
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}
}

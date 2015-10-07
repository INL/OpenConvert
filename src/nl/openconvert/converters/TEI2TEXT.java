package nl.openconvert.converters;

import java.io.FileOutputStream;
import java.io.PrintStream;

import org.w3c.dom.Document;

import nl.openconvert.filehandling.DirectoryHandling;
import nl.openconvert.tokenizer.PunctuationTagger;
import nl.openconvert.util.XML;

public class TEI2TEXT extends SimpleXSLTConversion
{
	public TEI2TEXT()
	{
		super("xsl/toplaintext.xsl");
	}
	
	@Override
	public void handleFile(String docFile, String outFilename) 
	{
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		
		
		
		try 
		{
			Document htmlDocument = XML.parse(docFile);
			//System.err.println(XML.documentToString(htmlDocument));
			Document teiDocument  = transformer.transformDocument(htmlDocument);
			PrintStream pout = new PrintStream(new FileOutputStream(outFilename));
			pout.print(teiDocument.getDocumentElement().getTextContent());
			pout.close();
		} catch (Exception e) 
		{
			e.printStackTrace
			();
			
		}	
	}
	public static void main(String[] args)
	{
		TEI2TEXT x = new TEI2TEXT();
		DirectoryHandling.tagAllFilesInDirectory(x, args[0], args[1]);
		//x.dinges("/mnt/Projecten/Taalbank/Werkfolder_Redactie/Jesse/Projecten/Papiamento/Mosaiko 5 HV Kap 1 vershon 2012 10 28.doc");
	}
}

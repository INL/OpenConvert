package nl.openconvert.converters;

/**
 * Probleem alto versies en namespaces
 * Kijk ook naar: https://raw.githubusercontent.com/pulibrary/childrens-lit/master/xsl/metsalto2tei.xsl
 */

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PushbackInputStream;
import java.util.Properties;

//import nl.namescape.sentence.JVKSentenceSplitter;
//import nl.namescape.sentence.TEISentenceSplitter;
//import nl.namescape.tokenizer.PunctuationTagger;



import nl.openconvert.filehandling.DirectoryHandling;
import nl.openconvert.filehandling.SimpleInputOutputProcess;
import nl.openconvert.sentence.JVKSentenceSplitter;
import nl.openconvert.sentence.TEISentenceSplitter;
import nl.openconvert.tokenizer.PunctuationTagger;
import nl.openconvert.util.Resource;
import nl.openconvert.util.TagSoupParser;
import nl.openconvert.util.XML;
import nl.openconvert.util.XSLTTransformer;

import org.w3c.dom.Document;

public class ALTO2TEI implements SimpleInputOutputProcess
{
	XSLTTransformer transformer;
	TagSoupParser parser = new TagSoupParser();
	private boolean splitSentences = true;

	public ALTO2TEI()
	{
		try
		{
			transformer = new XSLTTransformer((new Resource()).openStream("xsl/alto2tei.xsl"));
		} catch (Exception e)
		{

		}
	}

	private void removeOtherNamespaces(Document d)
	{

	}

	public Document convertDocument(Document htmlDocument)
	{
		return transformer.transformDocument(htmlDocument);
	}

	private static InputStream checkForUtf8BOMAndDiscardIfAny(InputStream inputStream) throws IOException 
	{
		PushbackInputStream pushbackInputStream = new PushbackInputStream(new BufferedInputStream(inputStream), 3);
		byte[] bom = new byte[3];
		if (pushbackInputStream.read(bom) != -1) 
		{
			if (!(bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)) 
			{
				pushbackInputStream.unread(bom);
			}
		}
		return pushbackInputStream; 
	}

	@Override

	public void handleFile(String docFile, String outFilename) 
	{
		try 
		{
			InputStream is = new FileInputStream(docFile);
			InputStream noBom = checkForUtf8BOMAndDiscardIfAny(is);
			Document htmlDocument = XML.parseStream(noBom, true); 
			//System.err.println(XML.documentToString(htmlDocument));
			Document teiDocument  = transformer.transformDocument(htmlDocument);

			if (splitSentences )
			{
				TEISentenceSplitter tsp = new TEISentenceSplitter(new JVKSentenceSplitter());
				PunctuationTagger pt =  new PunctuationTagger();
				pt.tagPunctuation(teiDocument);
				tsp.splitSentences(teiDocument);
			}
			
			PrintStream pout = new PrintStream(new FileOutputStream(outFilename));
			pout.print(XML.documentToString(teiDocument));
			pout.close();
			noBom.close();
		} catch (Exception e) 
		{
			System.err.println("Error handling " + docFile);
			e.printStackTrace();
			//System.exit(1); // Huh this is stupid...
		}	
	}

	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args)
	{
		ALTO2TEI x = new ALTO2TEI();
		DirectoryHandling.tagAllFilesInDirectory(x, args[0], args[1]);
		//x.dinges("/mnt/Projecten/Taalbank/Werkfolder_Redactie/Jesse/Projecten/Papiamento/Mosaiko 5 HV Kap 1 vershon 2012 10 28.doc");
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub

	}
}

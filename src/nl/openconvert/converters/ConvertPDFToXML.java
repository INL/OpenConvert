package nl.openconvert.converters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import java.io.*;

public class ConvertPDFToXML {
	static StreamResult streamResult;
	static TransformerHandler handler;
	static AttributesImpl atts;

	public static void blaBla(String pdf) throws IOException
	{
		PdfReader reader = new PdfReader(pdf);
		PdfReaderContentParser parser = new PdfReaderContentParser(reader);
		PrintWriter out = new PrintWriter(System.out); //  new PrintWriter(new FileOutputStream(txt));
		TextExtractionStrategy strategy;
		for (int i = 1; i <= reader.getNumberOfPages(); i++) {
			strategy
			= parser.processContent(i, new LocationTextExtractionStrategy());
			out.println(strategy.getResultantText());
		}
		out.flush();
		out.close();
	}

	public static void main(String[] args) throws IOException {
		//PdfReaderContentParser p;
		//blaBla(args[0]);
		//System.exit(1);
		try {
			Document document = new Document();
			document.open();
			PdfReader reader = new PdfReader(args[0]);
			PdfDictionary page = reader.getPageN(1);
			for (int i=1; i <=  reader.getNumberOfPages(); i++)
			{
				PdfDictionary p = reader.getPageN(i);
				if (p != null)
					processPage(p);
			}

			System.exit(0);
			/**
			//PdfIndirectReference objectReferencex = page.getAsIndirectObject(PdfName.CONTENTS);
			//System.err.println(objectReferencex);
			PRStream stream = (PRStream)  page.getAsStream(PdfName.CONTENTS);;
			byte[] bytes = page.get(PdfName.CONTENTS).getBytes();
			System.err.println(bytes);
			//PdfReader  .getPdfObject(objectReferencex);

			System.err.println(stream);
			RandomAccessSource ras = null;
			RandomAccessFileOrArray streamBytes =  new RandomAccessFileOrArray(ras); //  PdfReader.getStreamBytes(stream);
			PRTokeniser tokenizer = new PRTokeniser(streamBytes);



			String test = strbufe.toString();
			streamResult = new StreamResult(args[1]);
			initXML();
			process(test);
			closeXML();
			document.add(new Paragraph(".."));
			document.close();
			 **/
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	protected static PdfObject processPage(PdfDictionary page)
			throws IOException
	{
		PdfObject contents = page.get(PdfName.CONTENTS);
		StringBuffer strbufe = new StringBuffer();
		List<PRIndirectReference> references = new ArrayList<PRIndirectReference>();
		if (contents.isIndirect())
		{
			System.err.println("whew");
			references.add((PRIndirectReference) contents);
		} else if (contents.isArray())
		{
			PdfArray a = (PdfArray) contents;
			for (PdfObject o : a)
			{
				System.err.println(o.getClass());
				if (o.isIndirect())
				{
					references.add((PRIndirectReference) o);
				}
			}
		}
		for (PRIndirectReference o: references)
		{
			strbufe.append("<beginObject>");
			PRStream stream = (PRStream) PdfReader
					.getPdfObject(o);
			byte[] streamBytes = PdfReader.getStreamBytes(stream);
			PRTokeniser tokenizer = new PRTokeniser(new RandomAccessFileOrArray(streamBytes));
			while (tokenizer.nextToken()) 
			{
				// PRTokeniser.TokenType.
				//System.err.println(tokenizer.getTokenType() + ":" + tokenizer.getStringValue());
				if (tokenizer.getTokenType() == PRTokeniser.TokenType.START_ARRAY)
				{
					strbufe.append("\n");
				}
				if (tokenizer.getTokenType() == PRTokeniser.TokenType.STRING) 
				{

					strbufe.append(tokenizer.getStringValue());
				} else if (tokenizer.getTokenType() == PRTokeniser.TokenType.NUMBER)
				{
					double k = Double.parseDouble(tokenizer.getStringValue());
					if (k < -50)
						strbufe.append(" ");
				}
			
			}
			strbufe.append("<endObject>");
		}


		System.err.println(strbufe);
		return contents;
	}

	public static void initXML() throws ParserConfigurationException,
	TransformerConfigurationException, SAXException {
		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory
				.newInstance();

		handler = tf.newTransformerHandler();
		Transformer serializer = handler.getTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		serializer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		handler.setResult(streamResult);
		handler.startDocument();
		atts = new AttributesImpl();
		handler.startElement("", "", "data", atts);
	}

	public static void process(String s) throws SAXException {
		String[] elements = s.split("\\|");
		atts.clear();
		handler.startElement("", "", "Message", atts);
		handler.characters(elements[0].toCharArray(), 0, elements[0].length());
		handler.endElement("", "", "Message");
	}

	public static void closeXML() throws SAXException {
		handler.endElement("", "", "data");
		handler.endDocument();
	}
}
package nl.openconvert.tokenizer;

import nl.openconvert.util.Proxy;
import nl.openconvert.util.XML;

import org.w3c.dom.Document;

public class Pretokenizer
{
	public static void main(String[] args)
	{
		Proxy.setProxy();
		new TEITokenizer().preTokenizeFile(args[0]);
	}
}

package nl.openconvert.tokenizer;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;


import nl.openconvert.tei.TEITagClasses;
import nl.openconvert.util.Proxy;
import nl.openconvert.util.Util;
import nl.openconvert.util.XML;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;




import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory; 
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

public class TEITokenizer extends DefaultHandler
{
	private Writer out;
	
	private boolean inToken = false;
	private boolean insideTokenizedElement = false;
	private String pendingToken="";
	private boolean deferOpenTags = true;
	private boolean deleteIntermediate  =  true;
	private boolean needWhite = false;
	private boolean onlyInTextElement = true;
	Document currentDocument = null;
	private Set<String> inlineTags = new HashSet<String>();
	private Set<String> milestoneTags = new HashSet<String>();
	Stack<Element> openElementStack = new Stack<Element>();
	Stack<Element> pendingElementStack = new Stack<Element>();
	
	SAXParser parser = null;
	
	int wordNumber=0;
	
	public TEITokenizer()
	{
		addInlineTags(TEITagClasses.inlineTagNames);
		addMilestoneTags(TEITagClasses.milestone);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			currentDocument = docBuilder.newDocument();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addInlineTags(String [] tagNames)
	{
		for (String t: tagNames)
			inlineTags.add(t);
	}
	
	public void addMilestoneTags(String [] tagNames)
	{
		for (String t: tagNames)
			milestoneTags.add(t);
	}
	
	private boolean isMilestoneTag(String tagName)
	{
		return (milestoneTags.contains(tagName));
	}
	
	
	public void	characters(char[] ch, int start, int length) 
	{
		StringBuffer b = new StringBuffer("");
		for (int p=start; p < start+length; p++)
			b.append(ch[p]);
		String s = new String(b);
		
		if (!insideTokenizedElement)
		{
			s = nl.openconvert.util.Util.escapeCharacterData(s);
			output(s);
			return;
		}
		
		s = s.replaceAll("\u00a0", " ");
		s = s.replaceAll("\\s+", " ");
		
		// nog niet helemaal goed als ch met whitespace begint of eindigt

		
		boolean leadingWhite = s.startsWith(" ");
		boolean trailingWhite = s.endsWith(" ");
		
		s = s.replace("\\s$","");
		s = s.replace("^\\s", "");
		
		String[] tokens = new String(s).split("\\s+"); 
		
		if (leadingWhite)
			this.flushCurrentWord();
		
		// whitespace node: echo space
		if (tokens.length == 0)
		{
			if (leadingWhite) output(" ");
			return;
		}
		// het eerste token is ofwel vervolg van een pending token ofwel gewoon een los token...
		
		if (inToken && (tokens.length > 1 || trailingWhite)) // continue previous token
		{
				outputCharacterData(tokens[0]);
				endWTag(); // neen...... niet goed als eerste = laatste.....
				inToken = false;
		}  else if (!inToken && (tokens.length > 1 || trailingWhite))
		{
			if (tokens[0].length() > 0)
			{
				startWTag();
				outputCharacterData(tokens[0]);
				endWTag();
				inToken = false;
			}
		}
		
		// de niet-eerste en niet-laatste kunnen er gewoon uit
		// en ook de laatste als je training whitespace hebt
		
		for (int i=1; i < tokens.length-(trailingWhite?0:1); i++)
		{
			if (tokens[i].length() > 0)
			{
				startWTag();
				outputCharacterData(tokens[i]);
				endWTag();
				inToken = false;
			}
		}
		
		if (trailingWhite)
			output(" ");
		
		// de laatste wordt even pending...
		
		if (!trailingWhite && tokens.length > 0)
		{
			String t = tokens[tokens.length -1];
			if (t.length() > 0)
			{
			
				if (tokens.length > 1 || !inToken) 
					startWTag();
				// why not output the 
				pendingToken = t;
				inToken = true;
				outputCharacterData(t);
				pendingToken = "";
				//System.err.println("\nPending... <" + t + "> in <" + s + ">");
			}
		}
	}

	public  void startElement(String uri, String localName, String qName, Attributes attributes) 
	{
		Element e = currentDocument.createElement(qName);
		//System.err.println(localName);
		if (qName.equals("text") || qName.equals("osisText"))
		{
			//System.err.println("OK....");
			insideTokenizedElement=true;
			//System.exit(1);
		}
		for (int i=0; i < attributes.getLength(); i++)
		{
			if (!attributes.getQName(i).equalsIgnoreCase("teiform"))
	   		     e.setAttribute(attributes.getQName(i), attributes.getValue(i));
		}
		
		if (!insideTokenizedElement)
		{
			printStartTag(e);
			return;
		}
		
		if (isMilestoneTag(qName)) // dit werkt dus niet....
		{
			printEmptyTag(e);
			return;
		}
		
		if (TEITagClasses.tagSplitsWords(qName))
		{
			flushCurrentWord();
			needWhite = false;
			output("\n");
			printStartTag(e);
		} else
		{
			if (inToken)
			{
				if (pendingToken.length() > 0)
				{
					startWTag();
					outputCharacterData(pendingToken);
					pendingToken = "";
				}
			}
			// ofwel zet hem alleen op de pending lijst? en dan moet ie gerealiseerd zodra je karakters ziet
			if (deferOpenTags) //  && !milestoneTags.contains(qName))    
			{ 
				pendingElementStack.push(e);
			}
			else
			{
				//System.err.println("milestone: " + qName);
				printStartTag(e);
				openElementStack.push(e);
			}
		}
	}

	// het mag niet zo zijn dat hiervoor nog inline tags open staan; die moeten allemaal weer dicht.
	
	public void startWTag()
	{
		if (needWhite)
		{
			output(" ");
			needWhite = false;
		}
		flushOpenTags();
		output("<w xml:id=\"w." + wordNumber++ + "\">");
		realizePendingTags(); 
	}
	
	// het mag niet 
	public void endWTag()
	{
		flushOpenTags();
		output("</w>");
		needWhite = true;
		if (!this.deferOpenTags) 
			realizePendingTags(); // dit hoeft op zich niet? 
	}
	
	public void output(String s)
	{
		try
		{
			out.write(s);
			//out.flush();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void outputCharacterData(String s)
	{
		realizePendingTags();
		try
		{
			s = s.replaceAll("&", "&amp;");
			s = s.replaceAll("<", "&lt;");
			s = s.replaceAll(">", "&gt;");
			out.write(s);
			//out.flush();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void flushOpenTags()
	{
		while(true)
		{
			try 
			{
				Element e = openElementStack.pop();
				if (e != null) 
				{
					printEndTag(e);
					pendingElementStack.push(e);
				}
			} catch (Exception e) 
			{
				break;
				// TODO: handle exception
			}
		}
	}
	
	public void flushCurrentWord()
	{
		if (this.inToken)
		{
			// flush tag stack
			realizePendingTags();
			output(this.pendingToken);
			flushOpenTags();
			endWTag();
			this.inToken = false;
			this.pendingToken = "";
			// flush tag stack in reverse
		}
	}
	
	public void printStartTag(Element e)
	{
		try 
		{
			out.write("<" + e.getNodeName());
			NamedNodeMap attributes = e.getAttributes();
			for (int i=0; i < attributes.getLength(); i++)
			{
				Node n=attributes.item(i);
				out.write(" " + n.getNodeName() + "=\"" + nl.openconvert.util.Util.escapeForAttribute(n.getNodeValue()) + "\"");
			}
			out.write(">");
		} catch (IOException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void printEmptyTag(Element e)
	{
		try 
		{
			out.write("<" + e.getNodeName());
			NamedNodeMap attributes = e.getAttributes();
			for (int i=0; i < attributes.getLength(); i++)
			{
				Node n=attributes.item(i);
				out.write(" " + n.getNodeName() + "=\"" + n.getNodeValue() + "\"");
			}
			out.write("/>");
		} catch (IOException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void printEndTag(Element e)
	{
		try 
		{
			out.write("</" + e.getNodeName() + ">");
		} catch (IOException e1) 
		{
			e1.printStackTrace();
		}
	}
	
	public void printEndTag(String t)
	{
		try 
		{
			out.write("</" + t + ">");
		} catch (IOException e1) 
		{
			e1.printStackTrace();
		}
	}
	
	public void endElement(String uri, String localName, String qName) // hier moet je nog iets voor de empty elements doen....
	{
		if (!insideTokenizedElement)
		{
			printEndTag(qName);
			return;
		}
		if (isMilestoneTag(qName))
			return;
		if (TEITagClasses.tagSplitsWords(qName)) // hier gaat de volgorde mis..
		{
			flushCurrentWord();
			needWhite = false;
		} else
		{
		   // there should not be any pending elements right now..
		   realizePendingTags();
		   Element e = openElementStack.pop();
		}
		printEndTag(qName);
		if (TEITagClasses.tagSplitsWords(qName))
			output("\n");
	}

	public void tokenizeFile(String fileName, String outputFilename)
	{
		
		try 
		{
			out = new OutputStreamWriter(new FileOutputStream(outputFilename), "UTF8");
			if (parser == null)
			{
				SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setFeature("http://apache.org/xml/features/validation/schema", false);
				factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			    parser = factory.newSAXParser(); // waarom steeds opnieuw?
			}
			parser.parse( new File(fileName), this);
			out.close();
		} catch (Throwable err) 
		{
			err.printStackTrace ();
		}
	}
	
	public String preTokenizeString(String inputString)
	{
		try 
		{
			out = new StringWriter();
			byte[] bytes = inputString.getBytes("UTF-8");
			InputStream input = new ByteArrayInputStream(bytes);
			if (parser == null)
			{
				SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setFeature("http://apache.org/xml/features/validation/schema", false);
				factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			    parser = factory.newSAXParser(); // waarom steeds opnieuw?
			}
			this.insideTokenizedElement = true;
			parser.parse(input, this);
			out.close();
			return out.toString();
		} catch (Throwable err) 
		{
			err.printStackTrace ();
		}
		return null;
	}
	
	public Document tokenizeString(String inputString)
	{
		String pretokenized = preTokenizeString(inputString);
		Document d = XML.parseString(pretokenized);
		new PunctuationTagger().tagPunctuation(d);
		return d;
	}
	
	public String getTempDir()
	{
		 String property = "java.io.tmpdir";
      
	        // Get the temporary directory and print it.
	      String tempDir = System.getProperty(property);
	      System.err.println("OS current temporary directory is " + tempDir);
	      return tempDir;
	}
	
	public Document getTokenizedDocument(String fileName, boolean splitPunctuation)
	{
		File pretokenizedFile = null; 
		try
		{
			long startTok = System.currentTimeMillis();
			pretokenizedFile = File.createTempFile("tokenized", ".xml");
			try
			{
				if (deleteIntermediate) pretokenizedFile.deleteOnExit();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			tokenizeFile(fileName, pretokenizedFile.getCanonicalPath());
			double step1Time = (System.currentTimeMillis() - startTok) / 1000.0;
			System.err.println("pretokenization time: "  + step1Time);
			Document d = null;
			
			try
			{
				d = XML.parse(pretokenizedFile.getCanonicalPath(),true);
			} catch (Exception e)
			{
				System.err.println("error in pretokenized file " + pretokenizedFile.getCanonicalPath() );
				e.printStackTrace();
				//System.exit(1);
				//java.nio.file.Files.copy("aap", "noot");
			}
			if (deleteIntermediate) pretokenizedFile.delete();
			if (splitPunctuation)
		 	  new PunctuationTagger().tagPunctuation(d);
			//System.out.println(ParseUtils.documentToString(d));
			
			long endTok = System.currentTimeMillis();
			double elapsed = (endTok - startTok) / 1000.0;
			System.err.println("tokenization time: "  + elapsed);
			return d;
		} catch (Exception e)
		{
			e.printStackTrace();
			if (deleteIntermediate && pretokenizedFile != null) pretokenizedFile.delete();
			return null;
		}
	}
	
	public void preTokenizeFile(String fileName)
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try 
		{
			out = new OutputStreamWriter(System.out, "UTF8");
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse( new File(fileName), this);
			out.flush();
		} catch (Throwable err) 
		{
			err.printStackTrace ();
		}
	}
	
	/**
	 * This is silly! should output "deepest" tag first....
	 */
	public void realizePendingTags()
	{
		Stack<Element> viceVersa = new Stack<Element>();
		while(true)
		{
			try
			{
				Element e = pendingElementStack.pop();
				if (e != null)
					viceVersa.push(e);
			} catch (Exception e)
			{
				break;
			}
		}
		while(true)
		{
			try
			{
				Element e = viceVersa.pop();
				if (e != null)
				{
					printStartTag(e);
					openElementStack.push(e); // this IS ok? No?
				}
			} catch (Exception e)
			{
				break;
			}
		}
	}

	public static void main(String[] args)
	{
		Proxy.setProxy();
		Document d =  new TEITokenizer().getTokenizedDocument(args[0], true);
		System.out.println(XML.documentToString(d));
		//new TEITokenizer().preTokenizeFile(args[0]); // getTokenizedDocument(args[0],true);
	}
}

package nl.openconvert.converters;

import nl.openconvert.filehandling.ComposedInputOutputProcess;
import nl.openconvert.filehandling.DirectoryHandling;
import nl.openconvert.filehandling.SimpleInputOutputProcess;
import nl.openconvert.util.Options;
import java.util.*;

public class OpenConvert
{
	boolean tokenize = true;

	public static void main(String[] args)
	{
		Options o = new Options(args) 
		{
			public void defineOptions() 
			{
				options.addOption("t", "to", true, "-");
				options.addOption("f", "from", true, "-");
				options.addOption("T", "tokenize" , true, "true");
			}
		};

		args = o.commandLine.getArgs();
		String to = o.getOption("to");
		String from = o.getOption("from");

		SimpleInputOutputProcess x =  null;

		if (("-".equals("from")  || from == null) && ("-".equals("to") || to==null))
		{
			System.err.println("Guessing from extensions...");
			x = new ExtensionBasedConversion();
		} else
			
		x = getConverter(to, from, true);
		//x.setProperties(o.asProperties()); // pas op dit verandert het gedrag nogal...
		
		if (x != null)
			DirectoryHandling.traverseDirectory(x, args[0], args[1],null);
		else
		{
			System.err.println("Could not find conversion from " + from + " to "  + to);
		}
		//x.dinges("/mnt/Projecten/Taalbank/Werkfolder_Redactie/Jesse/Projecten/Papiamento/Mosaiko 5 HV Kap 1 vershon 2012 10 28.doc");
	}

	public static SimpleInputOutputProcess getConverter( String to,
			String from,  boolean tokenize)
	{
		SimpleInputOutputProcess x = null;
		
		if (to.equalsIgnoreCase("txt") ||to.equalsIgnoreCase("text") )
		{
			TEI2TEXT  y = new  TEI2TEXT();
			
			if (from.equalsIgnoreCase("tei"))
				return y;
			
			SimpleInputOutputProcess z = getConverterToTEI(from);

		
			if (z != null)
			{
				List<SimpleInputOutputProcess> l = new ArrayList<SimpleInputOutputProcess>();
				l.add(z);
				l.add(y);
				ComposedInputOutputProcess c  = new ComposedInputOutputProcess(l);
				x = c;
			}
			return x;
		}
		
		if (to.equalsIgnoreCase("html"))
		{
			switch(from.toLowerCase())
			{
			case "doc": x = new Doc2HTML();  break;
			case "docx": x = new Docx2HTML(); break;
			}
			return x;
		}

		if (to.equalsIgnoreCase("TEI"))
		{
			x = getConverterToTEI(from);
			return x;
		};

		if (to.equalsIgnoreCase("folia"))
		{
			TEI2FOLIA  y = new  TEI2FOLIA();
			y.tokenize = !from.equalsIgnoreCase("alto"); // alto is already tokenized
			SimpleInputOutputProcess z = null;
			
			if (from.equalsIgnoreCase("tei"))
				return y;
			
			z = getConverterToTEI(from);

			if (from.equalsIgnoreCase("alto")) // alto is already tokenized
			{
				Properties p = new Properties();
				p.setProperty("tokenize", false + "");
				y.setProperties(p);
			}
			if (z != null)
			{
				List<SimpleInputOutputProcess> l = new ArrayList<SimpleInputOutputProcess>();
				l.add(z);
				l.add(y);
				ComposedInputOutputProcess c  = new ComposedInputOutputProcess(l);
				x = c;
			}
			return x;
		};
		
		return x;
	}

	public static SimpleInputOutputProcess getConverterToTEI(String from)
	{
		SimpleInputOutputProcess x = null;
		switch(from.toLowerCase())
		{
		case "txt": x = new Text2TEI(); break;
		case "doc": case "word97": x = new Doc2TEI(); break;
		case "docx": x = new Docx2TEI(); break;
		case "html": x = new HTML2TEI(); break;
		case "folia":  x = new FOLIA2TEI(); break;
		case "epub": x = new EPub2TEI(); break;
		case "alto": x = new ALTO2TEI(); break;
		}
		return x;
	}
}

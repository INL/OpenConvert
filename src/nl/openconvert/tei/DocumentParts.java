package nl.openconvert.tei;
import nl.openconvert.util.XML;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;


public class DocumentParts 
{
	public static void UntagWordsInEditorialMatter(Document d)
	{
		Element e = d.getDocumentElement();
		List<Element> all = XML.getAllSubelements(e, true);
		int editorial=0;
		int original=0;
		boolean excludeNotes = true;
		for (Element x : all)
		{
			String name = x.getNodeName();
			if (name.equals("milestone"))
			{
				String unit = x.getAttribute("unit");
				if (unit != null && unit.equalsIgnoreCase("be"))
					editorial++;
				if (unit != null && unit.equalsIgnoreCase("ee"))
					editorial--;
				if (unit != null && unit.equalsIgnoreCase("bo"))
					original++;
				if (unit != null && unit.equalsIgnoreCase("eo"))
					original--;
			}
			if (name.equals("w") && (editorial > 0 || original <= 0) )
			{
				d.renameNode(x, "", "seg");
			}
		}
		if (excludeNotes)
		{
			List<Element> notes = XML.getElementsByTagname(e, "note", false);
			for (Element n: notes)
			{
				List<Element> words = nl.openconvert.tei.TEITagClasses.getTokenElements(n);
				for (Element w: words)
					d.renameNode(w, "", "seg");
			}
		}
	}
	
	public static void main(String [] args)
	{
		try 
		{
			Document d = XML.parse(args[0]);
			DocumentParts.UntagWordsInEditorialMatter(d);
			System.out.println(XML.documentToString(d));
		} catch (ParserConfigurationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

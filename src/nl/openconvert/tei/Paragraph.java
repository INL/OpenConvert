package nl.openconvert.tei;
import java.util.List;

import nl.openconvert.util.XML;

import org.w3c.dom.Element;



public class Paragraph 
{
	public String id = null;
	boolean decent=true;
	public int numTokens=-1;
	public Paragraph(Element p)
	{
		this.id = p.getAttribute("id");
		if (this.id==null)
			this.id = p.getAttribute("xml:id");
		String s = p.getTextContent();
		String[] tokens = s.split("\\s+");
		this.numTokens = tokens.length;
	}
	
	/**
	 * Een heuristiekje om de meest verknipte alinea's uit te sluiten.
	 * Voorlopig ook de alinea's met andere dan NE tags eruit gooien omdat de attestatie tool
	 * niet zo van die tagjes houdt en ik dan gewoon de hele inhoud van de p kan vervangen?
	 * @param p
	 * @return
	 */
	
	public static boolean isDecentParagraph(Element p)
	{
		String s = p.getTextContent();
		boolean startsOK = s.matches("^\\s*\\p{P}*\\s*\\p{Lu}.*");
		boolean endsOK = s.matches(".*[.?!]\\p{P}*\\s*$");
		List<Element> subElements = XML.getAllSubelements(p, true);
		boolean subsOK=true;
		for (Element e: subElements)
		{
			String n = e.getNodeName();
			if (! (n.contains("Name") || n.contains("name")))
			{
					subsOK=false;
			}
		}
		return (subsOK && startsOK && endsOK);
	}
}

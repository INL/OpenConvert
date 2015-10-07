package nl.openconvert.tei;
import java.util.*;

import nl.openconvert.util.XML;

import org.w3c.dom.*;


public class Metadata 
{
	public Map<String,Set<String>> metadata = new  HashMap<String,Set<String>>();
	
	public Metadata(Document d)
	{
		this.metadata = getMetadata(d);
	}
	
	public String getValue(String key)
	{
		return getValue(metadata,key);
	}
	
	public static Map<String,Set<String>> getMetadata(Document d)
	{
		
		Map<String,Set<String>> m = new HashMap<String,Set<String>>();
		List<Element> bibls = XML.getElementsByTagnameAndAttribute(d.getDocumentElement(), 
				"listBibl","xml:id", "inlMetadata", false);
		if (bibls.size() == 0)
		{
			bibls = XML.getElementsByTagnameAndAttribute(d.getDocumentElement(), 
					"listBibl","id", "inlMetadata", false);
		}
		for (Element b: bibls)
		{
			//System.err.println(b);
			List<Element> grps = XML.getElementsByTagname(b, "interpGrp", false);
			for (Element grp: grps)
			{
				String fieldName = grp.getAttribute("type");
				//System.err.println("interpGrp type=" + fieldName);
				Set<String> values = m.get(fieldName);
				
				if (values == null)
				{
					values = new HashSet<String>();
					m.put(fieldName, values);
				}
				
				List<Element> intrps = XML.getElementsByTagname(grp, "interp", false);
				
				for (Element i: intrps)
				{
					String value = i.getAttribute("value");
					String content = i.getTextContent();
					
					//System.err.println(fieldName + ":"  + content);
					if (value != null && value.length() > 0) 
						values.add(value);
					if (content != null  && content.length() > 0)
						values.add(content);
				}
			}
		}
		return m;
	}
	
	public String getValue(Map<String,Set<String>> m, String key)
	{
		Set<String> vals = m.get(key);
		String separator = "|";
		if (vals != null)
		{
			String r="";
			for (String s: vals)
			{
				if (r.length() > 0)
					r += separator;
				s = s.replaceAll("\\s+", " ");
				r += s;
			}
			return r;
		}
		return "";
	}
}

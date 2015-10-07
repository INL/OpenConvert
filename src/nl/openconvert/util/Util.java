package nl.openconvert.util;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Util 
{
	private String getDateTime() 
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	static public String join(List<String> list, String conjunction)
	{
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String item : list)
		{
			if (first)
				first = false;
			else
				sb.append(conjunction);
			sb.append(item);
		}
		return sb.toString();
	}
	
	
	public static String escapeForAttribute(String v)
	{
		v = v.replaceAll("&", "&amp;");
		v = v.replaceAll("<", "&lt;");
		v = v.replaceAll(">", "&gt;");
		v = v.replaceAll("'", "&apos;");
		v = v.replaceAll("\"", "&quot;");
		return v;
	}
	
	public static String escapeCharacterData(String v)
	{
		v = v.replaceAll("&", "&amp;");
		v = v.replaceAll("<", "&lt;");
		v = v.replaceAll(">", "&gt;");
		return v;
	}
}

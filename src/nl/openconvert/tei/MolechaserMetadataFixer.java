package nl.openconvert.tei;

import nl.openconvert.filehandling.DirectoryHandling;
import nl.openconvert.filehandling.DoSomethingWithFile;
import nl.openconvert.filehandling.SimpleInputOutputProcess;
import nl.openconvert.util.*;

import org.w3c.dom.*;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.ranges.Range;


import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.util.*;

/**
 * Dit werkt niet.
 * Er zitten <p>-tjes in de esjes..
 * @author does
 *
 */
public class MolechaserMetadataFixer implements SimpleInputOutputProcess
{
	Validator validator = new Validator();
	
	public Document fixDocument(Document d, String fileName)
	{
		Element root = d.getDocumentElement();
		Metadata m = new Metadata(d);
		String idno = m.getValue("idno").trim();
		if (idno == null || idno.equals(""))
		{
			String newIdno = MolechaserMetadataFixer.createIdno(m, d, fileName);
			System.err.println("Attempt to assign idno: " + newIdno);
			idno = newIdno;
			setIdno(d,idno);
		}
		return d;
	}
	
	public static String getPlainTextMD5(Document d, String fileName)
	{
		Element t = XML.getElementByTagname(d.getDocumentElement(), "body");
		String s = t.getTextContent();
		
		try 
		{
			byte[] bytesOfMessage = s.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			StringBuffer hexString = new StringBuffer();
			for (int i=0;i<thedigest.length;i++) 
			{
				hexString.append(Integer.toHexString(0xFF & thedigest[i]));
			}

			return hexString.toString();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return "oompf";
	}
	
	
	public static void main(String[] args)
	{
		MolechaserMetadataFixer f =  new MolechaserMetadataFixer();
		DirectoryHandling.tagAllFilesInDirectory(f, args[0], args[1], true);
	}
	
	@Override
	public void handleFile(String fileName, String out) 
	{
		// TODO Auto-generated method stubfil
		Document d = null;
		try
		{
			 d = XML.parse(fileName);
			 fixDocument(d, fileName);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		try 
		{
			PrintStream pout = new PrintStream(new FileOutputStream(out));
		
			pout.print(XML.documentToString(d));
			pout.close();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}



	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub
		
	}



	public void setIdno(Document d, String idno)
	{
		List<Element> listBibls = 
				XML.getElementsByTagname(d.getDocumentElement(), "listBibl", false);
		for (Element lb: listBibls)
		{
			if (lb.getAttribute("id").matches("[Ii][Nn][Ll][Mm].*"))
			{
				List<Element> bibls = XML.getElementsByTagname(lb, "bibl", false);
				for (Element b: bibls)
				{
					Element myInterpGrp = null;
					List<Element> idnoInterpGrps = XML.getElementsByTagnameAndAttribute(b, 
							"interpGrp", "type", "idno", false);
					if (idnoInterpGrps.size() > 0)
					{
						myInterpGrp = idnoInterpGrps.get(0);
					} else
					{
						myInterpGrp = d.createElement("interpGrp");
						myInterpGrp.setAttribute("type", "idno");
						b.appendChild(myInterpGrp);
					}
					Element interp = d.createElement("interp");
					interp.setAttribute("value", idno);
					myInterpGrp.appendChild(interp);
				}
			}
		}
	}



	public static String createIdno(Metadata m, Document d, String fileName)
	{
		String provenance = m.getValue("corpusProvenance");
		if (provenance == null)
			provenance = "NO_PROVENANCE";
		if (provenance.contains("tandaard"))
		{
			String fn = new File(fileName).getName();
			fn = fn.replaceAll(".xml$","");
			return fn;
		} else
		{
			return provenance + "." + getPlainTextMD5(d, fileName);
		}
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}
}

package nl.openconvert.tei;
import nl.openconvert.filehandling.*;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import nl.openconvert.util.Proxy;
import nl.openconvert.util.XML;
import org.w3c.dom.*;
import java.security.*;


public class MolechaserMetadataChecker implements DoSomethingWithFile
{

	ConcurrentHashMap<String,Set<String>> 
	idmap = new ConcurrentHashMap<String,Set<String>>();
	ConcurrentHashMap<String,Set<String>> 
	dedupMap = new ConcurrentHashMap<String,Set<String>>();
	Set<String> filesWithoutText = new HashSet<String>();
	ConcurrentMap<String,String> multiProperties = new ConcurrentHashMap<String,String>();
	int N = 0;
	boolean checkIdno = true;
	boolean checkDuplicateArticles = true;
	static int minimum_words = 10;

	public void printDuplicateInfo()
	{
		for (String k: dedupMap.keySet())
		{
			Set<String> v = dedupMap.get(k);
			if (v.size() > 1)
			{
				System.out.println("Error: duplicate content! "  +k + "\t" + v);
			}
		}
		for (String k: idmap.keySet())
		{
			Set<String> v = idmap.get(k);
			if (v.size() > 1)
			{
				System.out.println("Error: duplicate id! "  +k + "\t" + v);
			}
		}
		for (String f: filesWithoutText)
		{
			System.out.println("Empty file\t" + f);
		}
	}

	public String checkMinimumLength(Document d, String fileName)
	{
		List<Element> bodies = XML.getElementsByTagname(d.getDocumentElement(), "body", false);
		String txt = "";
		for (Element b: bodies)
		{
		// dit gaat mis bij artikelen met groups erin? Waar komen die vandaan?
			String s = b.getTextContent();
			txt += " " + s;
		}
		if (txt.split("\\s+").length < minimum_words)
		{
			filesWithoutText.add(fileName);
		}
		return txt;
	}
	
	public String getPlainTextMD5(Document d, String fileName)
	{
		Element t = XML.getElementByTagname(d.getDocumentElement(), "body");
		// dit gaat mis bij artikelen met groups erin? Waar komen die vandaan?
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
	
	public String getStandaardArticleKey(Metadata m, Document d, String fileName)
	{
		String key="";
		String[] metaFields = {"witnessYear_from", "witnessMonth_from", "witnessDay_from",
		"titleLevel1"};
		for (String f: Arrays.asList(metaFields))
		{
			key += m.getValue(f) + "/";
		}
		key += getPlainTextMD5(d, fileName);
		return key;
	}
	
	@Override
	public void handleFile(String fileName) 
	{
		// TODO Auto-generated method stub
		//System.err.println(N + ": "+ fileName);
		try
		{
			Document d = XML.parse(fileName,false);
			Metadata m = new Metadata(d);
			checkMetadata(m, d, fileName);
		} catch (Exception e)
		{
			e.printStackTrace();
			//System.exit(1);
		}
	}

	public boolean isYear(String y)
	{
		return y.matches("^[0-9][0-9][0-9][0-9]$");
	}

	public synchronized void checkMetadata(Metadata m, Document d, String fileName)
	{

		String witnessYear_from = m.getValue("witnessYear_from").trim();
		String witnessYear_to = m.getValue("witnessYear_to").trim();
		String sourceId = m.getValue("sourceID").trim();
		String provenance = m.getValue("corpusProvenance");
		checkMinimumLength(d,fileName);
		if (provenance == null || provenance.equals(""))
		{
			System.err.println("No corpusProvenance in file:  " + fileName);
		}
		
		if (!isYear(witnessYear_from))
		{
			System.err.println("invalid year in " + fileName + " : " + witnessYear_from);
		}
		if (!isYear(witnessYear_to))
		{
			System.err.println("invalid year in " + fileName + " : " + witnessYear_to);
		}

		if (checkIdno)
		{
			if (sourceId == null || sourceId.equals(""))
			{
				System.err.println("No idno in file:  " + fileName);
				String newIdno = MolechaserMetadataFixer.createIdno(m, d, fileName);
				System.err.println("Attempt to assign idno: " + newIdno);
				sourceId = newIdno;
			}

			Set<String> filesWithThisId = idmap.get(sourceId);

			if (filesWithThisId == null)
			{
				filesWithThisId = new HashSet<String>();
				idmap.put(sourceId, filesWithThisId);
			} else
			{
				filesWithThisId.add(fileName);
				//System.err.println("Error: duplicate idno "  + idno + " " + filesWithThisId);
			}
			filesWithThisId.add(fileName);
		}

		Set<String> languages = m.metadata.get("languageVariant");
		String l;
		if (languages == null || languages.size() != 1)
		{
			System.err.println("Wrong number of language variants: " + languages + ":  " + fileName);
		} else
		{
			l = languages.iterator().next();
			if (!l.equals("NN") && !l.equals("BN"))
			{
				System.err.println("Wrong language variant: " + l +  ":  " + fileName);
			}
		}

		for (String name: m.metadata.keySet())
		{
			Set<String> values = m.metadata.get(name);
			if (values.size() > 1 && !multiProperties.containsKey(name))
			{
				System.err.println("Multiply defined property " + name +  " in " + fileName +  " :  " + values);
				multiProperties.put(name,name);
			}
		}

		if (checkDuplicateArticles)
		{
			String key = this.getStandaardArticleKey(m,d, fileName);
			Set<String> filesWithThisKey = dedupMap.get(key);

			if (filesWithThisKey == null)
			{
				filesWithThisKey = new HashSet<String>();
				dedupMap.put(key, filesWithThisKey);
			} else
			{
				filesWithThisKey.add(fileName);
				//System.err.println("Error: duplicate content? "  +key + " " + filesWithThisKey);
			}
			filesWithThisKey.add(fileName);
		}

		if (N % 10000 == 0)
		{
			print(m,fileName);
		}
		N++;
	}

	public synchronized void print(Metadata m, String fileName)
	{

		String authorLevel1 = m.getValue("authorLevel1").trim();
		String authorLevel2 = m.getValue("authorLevel2").trim();
		String titleLevel1 = m.getValue("titleLevel1").trim();
		String titleLevel2 = m.getValue("titleLevel2").trim();
		String witnessYear_from = m.getValue("witnessYear_from").trim();
		String witnessYear_to = m.getValue("witnessYear_to").trim();
		String languageVariant = m.getValue("languageVariant").trim();
		String corpusProvenance =  m.getValue("corpusProvenance").trim();
		String author=authorLevel1.equals("")?authorLevel2:authorLevel1;
		String title=authorLevel1.equals("")?titleLevel2:titleLevel1;

		System.err.println(N + "\t" + corpusProvenance+ "\t" +  fileName + "\t" + languageVariant + "\t" 
				+ author + "\t" + title + "\t" + 
				witnessYear_from + "\t"+ witnessYear_to + "\t" + 
				authorLevel1 + "\t" +  authorLevel2 + "\t" 
				+ titleLevel1 + "\t" + titleLevel2);
	}

	public static void main(String[] args)
	{
		Proxy.setProxy();
		MolechaserMetadataChecker d = new MolechaserMetadataChecker();
		MultiThreadedFileHandler m = new MultiThreadedFileHandler(d,11);
		DirectoryHandling.traverseDirectory(m, args[0]);
		m.shutdown();
		d.printDuplicateInfo();
	}
}

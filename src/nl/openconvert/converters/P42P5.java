package nl.openconvert.converters;

import nl.openconvert.filehandling.DirectoryHandling;

public class P42P5 extends SimpleXSLTConversion
{
	public P42P5()
	{
		super("xsl/p4top5.xsl");
	}
	
	public static void main(String[] args)
	{
		P42P5 x = new P42P5();
		DirectoryHandling.tagAllFilesInDirectory(x, args[0], args[1]);
		//x.dinges("/mnt/Projecten/Taalbank/Werkfolder_Redactie/Jesse/Projecten/Papiamento/Mosaiko 5 HV Kap 1 vershon 2012 10 28.doc");
	}
}

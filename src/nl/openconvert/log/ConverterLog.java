package nl.openconvert.log;
import java.io.*;

public class ConverterLog 
{
	PrintStream logStream = System.err;
	boolean verbose = false;

	public static ConverterLog defaultLog = new ConverterLog(System.err);

	public static void setDefaultVerbosity(boolean b)
	{
		defaultLog.verbose = b;
	}


	public ConverterLog()
	{

	}

	public ConverterLog(PrintStream s)
	{
		this.logStream = s;
	}

	public void setVerbose(boolean b)
	{
		verbose = b;
	}

	public void println(Object  s)
	{
		if (verbose)
			logStream.println(s);
	}

	public void print(Object  s)
	{
		if (verbose)
			logStream.print(s);
	}

	public void printf(String format, Object ... args)
	{
		if (verbose)
			logStream.printf(format, args);
	}
	
	public static String getTopClass()
	{
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		return st[st.length-1].getClassName();
	}
}

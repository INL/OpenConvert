package nl.openconvert.util;

public class IO 
{
	public static String getTempDir()
	{
		 String property = "java.io.tmpdir";
      
	        // Get the temporary directory and print it.
	      String tempDir = System.getProperty(property);
	      System.err.println("OS current temporary directory is " + tempDir);
	      return tempDir;
	}
	
}

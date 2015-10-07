package nl.openconvert.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TabSeparatedFile 
{
	BufferedReader b =null;
	public String[] fieldNames;
	public String[] currentRow = null;
	public String separator = "\t";
	Map<String,Integer> fieldNumbers = new HashMap<String,Integer>();
	
	public void setSeparator(String s)
	{
		this.separator = s;
	}
	public TabSeparatedFile(String fileName, String[] fields)
	{
		try {
			b = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.fieldNames = fields;
		for (int i=0; i < fieldNames.length; i++)
			fieldNumbers.put(fieldNames[i],i);
	}
	
	public TabSeparatedFile(BufferedReader b, String[] fields)
	{
		this.b = b;
		this.fieldNames = fields;
		for (int i=0; i < fieldNames.length; i++)
			fieldNumbers.put(fieldNames[i],i);
	}
	

	
	public String[] getLine()
	{	
		String s;
		try {
			if  (( (s = b.readLine()) != null))
			{
				currentRow = s.split(separator);
				return currentRow;
			}
		} catch (IOException e) 
		{
			
			e.printStackTrace();
		}
		currentRow = null;
		return null;
	}
	
	
	public String getField(String s)
	{
		if (currentRow != null)
		{
			try
			{
				return currentRow[fieldNumbers.get(s)];
			} catch (Exception e)
			{
				
			}
		}
		return null;
	}
	
   
}

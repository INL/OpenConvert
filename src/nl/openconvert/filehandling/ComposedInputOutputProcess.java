package nl.openconvert.filehandling;

import java.util.*;
import java.io.*; 


public class ComposedInputOutputProcess implements SimpleInputOutputProcess
{
	List<SimpleInputOutputProcess> steps = new ArrayList<SimpleInputOutputProcess>();
	public ComposedInputOutputProcess(SimpleInputOutputProcess s1, SimpleInputOutputProcess s2)
	{
		steps.add(s1);
		steps.add(s2);
	}
	
	public List<SimpleInputOutputProcess> getSteps()
	{
		return steps;
	}
	public ComposedInputOutputProcess(List<SimpleInputOutputProcess> l)
	{
		steps.addAll(l);
	}
	
	@Override
	public void handleFile(String inFilename, String outFilename) throws ConversionException
	{
		// TODO Auto-generated method stub
		File previousOut = null;
		for (int i =0; i < steps.size(); i++)
		{
			SimpleInputOutputProcess step = steps.get(i);
			String in = i==0?inFilename:previousOut.getPath();
			String out = null;
			File x = null;
			if (i==steps.size()-1)
				out = outFilename;
			else
			{
				try
				{
					x = File.createTempFile("step.", "tmp");
					out = x.getPath();
					x.delete(); // this is ugly and wrong
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			step.handleFile(in, out);
			previousOut = x;
		}
	}

	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}
}

package nl.openconvert.filehandling;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedFileHandler implements DoSomethingWithFile, SimpleInputOutputProcess
{
	ExecutorService pool;
	DoSomethingWithFile baseHandler1;
	SimpleInputOutputProcess baseHandler2;
	
	public class UnaryTask implements Runnable
	{
		String fileName = null;
		
		public UnaryTask(String fileName)
		{
			this.fileName = fileName;
		}
		@Override
		public void run() 
		{
			if (fileName != null && baseHandler1 != null)
				try
				{
					baseHandler1.handleFile(fileName);
				} catch (ConversionException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	
	public class BinaryTask implements Runnable
	{
		String inFile = null;
		String outFile = null;
		public  BinaryTask(String inFile, String outFile)
		{
			this.inFile = inFile;
			this.outFile = outFile;
		}
		@Override
		public void run() 
		{
			if (inFile != null && baseHandler2 != null)
				try
				{
					baseHandler2.handleFile(inFile,outFile);
				} catch (ConversionException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public MultiThreadedFileHandler(DoSomethingWithFile h, int nThreads)
	{
		this.baseHandler1 = h;
		this.pool =  Executors.newFixedThreadPool(nThreads);
	}

	public MultiThreadedFileHandler(SimpleInputOutputProcess h, int nThreads)
	{
		this.baseHandler2 = h;
		this.pool =  Executors.newFixedThreadPool(nThreads);
	}
	
	@Override
	public void handleFile(String fileName) 
	{
		// TODO Auto-generated method stub
		UnaryTask t = new UnaryTask(fileName);
		pool.execute(t);
	}

	@Override
	public void handleFile(String in, String out) 
	{
		// TODO Auto-generated method stub
		BinaryTask t = new BinaryTask(in,out);
		pool.execute(t);
	}
	
	public void shutdown()
	{
		this.pool.shutdown();
		while (!this.pool.isTerminated())
		{
			try 
			{
				Thread.sleep(100);
			} catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	static class ExampleHandler implements DoSomethingWithFile
	{
		long totalSpace = 0;
		synchronized void increment(long x)
		{
			totalSpace += x;
		}
		@Override
		public void handleFile(String fileName) 
		{
			//System.err.println(fileName + " " + Thread.currentThread().getId());
			File f = new File(fileName);
			long l = f.length();
			increment(l);
			double l1 =l / 1000000.0;
			double d = totalSpace / 1000000.0;
			System.err.println(fileName + " " + Thread.currentThread().getId() + "  " + l1 + " / " + d);
		}
	}
	

	public static void main(String args[])
	{
		ExampleHandler e = new ExampleHandler();
		MultiThreadedFileHandler m = new MultiThreadedFileHandler(e,100);
		DirectoryHandling.traverseDirectory(m, "D:/");
		m.pool.shutdown();
	}

	@Override
	public void setProperties(Properties properties)  throws ConversionException
	{
		// TODO Auto-generated method stub
		if (this.baseHandler2 != null)
			this.baseHandler2.setProperties(properties);
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}
}

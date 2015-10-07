package nl.openconvert.util;

import java.util.HashMap;
import java.util.Map;



public class Progress 
{
	static Map<String, State> states = new HashMap<String, State>();
	static Map<String, String> jobs =  new HashMap<String, String>();
	
	public static void setJobId(String threadId, String jobId)
	{
		jobs.put(threadId, jobId);
	}
	
	public static String getJobId(String threadId)
	{
		return jobs.get(threadId);
	}
	
	public static  Map<String, String> getJobs()
	{
		return jobs;
	}
	
	public static class State
	{
		String id;
		public double percentage;
		public String message="";
		public State(String id)
		{
			this.id = id;
		}
	}
	
	public static void setPercentage(String id, double p)
	{
		State s = states.get(id);
		if (s == null)
		{
			states.put(id,  s = new State(id));
			
		}
		s.percentage = p;
	}
	
	public static State getState(String id)
	{
		return states.get(id);
	}

	public static void setMessage(String id, String string) 
	{
		State s = states.get(id);
		if (s == null)
		{
			states.put(id,  s = new State(id));
		}
		s.message = string;
	}
}

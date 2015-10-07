package nl.openconvert.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

public class HTTP 
{
	public static String postRequest(URL url, Map<String,String> requestParameters)
	{
		try 
		{
			// Construct data
			String postData="";

			for (String key: requestParameters.keySet())
			{
				if (postData.length() > 0)
					postData += "&";
				postData += key + "=" + URLEncoder.encode(requestParameters.get(key), "UTF-8");
			}


			// System.err.println("PostData: " + postData);
			URLConnection conn = url.openConnection(); // iets met caching afzetten (zie stackoverflow); persistent connection sneller?
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(postData);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String response="";
			String line;
			while ((line = rd.readLine()) != null) 
			{
				response += line + "\n";
			}
			wr.close();
			rd.close();
			return response;
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
}

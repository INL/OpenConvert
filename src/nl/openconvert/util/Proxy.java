package nl.openconvert.util;
import java.net.Authenticator;
import java.net.PasswordAuthentication;






public class Proxy 
{

	public static class httpAuthenticateProxy extends Authenticator 
	{
		protected PasswordAuthentication getPasswordAuthentication() 
		{
			// username, password
			// sets http authentication
			return new PasswordAuthentication("....", ".....".toCharArray());
		}
	}

	public static void setProxy()
	{
		try
		{
			String localhostname = java.net.InetAddress.getLocalHost().getHostName();
			String canonicalhost  = java.net.InetAddress.getLocalHost().getCanonicalHostName();
			if (!canonicalhost.endsWith("inl.loc"))
			{
				System.err.println("use inl proxy only inside inl.loc: " + canonicalhost);
				return;
			} else
			{
				System.err.println("use inl proxy, we are inside inl.loc: " + canonicalhost);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		System.setProperty("http.proxyHost", "proxy.inl.loc"); 
		System.setProperty("http.proxyPort",  "8080");


		//System.out.println("using proxy: "+ SecureClient.proxyhost + " port " + SecureClient.proxyport);


		// now create http authentication


		// this didn't work 
		// System.setProperty("http.proxyUser", "myuser"); 
		// System.setProperty("http.proxyPassword", "mypassword");


		// this worked in 1.4.1 
		Authenticator.setDefault( new httpAuthenticateProxy() ); 
	}
}

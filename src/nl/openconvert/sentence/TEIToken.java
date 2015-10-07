package nl.openconvert.sentence;


import java.util.HashMap;

import org.w3c.dom.Element;




public class TEIToken extends HashMap<String,String> implements Token
{
	Element e;
	boolean isEOS=false;
	
	public TEIToken()
	{
		
	}
	
	
	
	public TEIToken(Element el)
	{
		e = el;
		this.put("word", el.getTextContent());
		String tag =  el.getAttribute("type");
		if (tag != null)
			this.put("tag", tag);
		this.put("id", el.getAttribute("xml:id"));
	}
	
	@Override
	public String getContent() 
	{
		// TODO Auto-generated method stub
		return e.getTextContent();
	}

	@Override
	public boolean isWord() 
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean getIsEOS() 
	{
		// TODO Auto-generated method stub
		return isEOS;
	}

	@Override
	public void setIsEOS(boolean b) 
	{
		isEOS=true;
		// TODO Auto-generated method stub
	}
}

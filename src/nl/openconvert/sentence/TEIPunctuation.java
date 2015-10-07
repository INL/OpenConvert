package nl.openconvert.sentence;
import org.w3c.dom.Element;





public class TEIPunctuation extends TEIToken
{
	public TEIPunctuation(Element el) 
	{
		super(el);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isWord() 
	{
		// TODO Auto-generated method stub
		return false;
	}
}

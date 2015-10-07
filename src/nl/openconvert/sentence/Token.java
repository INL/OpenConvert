package nl.openconvert.sentence;

public interface Token 
{
	public String getContent();
	public boolean isWord();
	public boolean getIsEOS();
	public void setIsEOS(boolean b);
}

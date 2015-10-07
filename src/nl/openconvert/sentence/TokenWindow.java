package nl.openconvert.sentence;


public interface TokenWindow 
{
	public boolean shift(int by);
	public Token getToken(int rel);
	public Token getToken();
}

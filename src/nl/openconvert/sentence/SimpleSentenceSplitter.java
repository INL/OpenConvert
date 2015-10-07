package nl.openconvert.sentence;



public class SimpleSentenceSplitter implements SentenceSplitter 
{
	public void split(TokenWindow w)
	{
		while (true)
		{
			Token t = w.getToken();
			if (t == null)
				break;
			if (!t.isWord()) 
			{
				String s = t.getContent();
				if (s.contains(".") || s.contains("?") || s.contains("!"))
				{
					t.setIsEOS(true);
				}
			}
			if (!w.shift(1))
				break;
		}
	}
}

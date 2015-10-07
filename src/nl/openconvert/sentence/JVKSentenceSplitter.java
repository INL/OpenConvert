package nl.openconvert.sentence;

import java.util.HashSet;
import java.util.Set;




public class JVKSentenceSplitter implements SentenceSplitter
{
	public static String[] abbreviations = 
	{
			 "no", "nl", "(ed", "alg", "art", "blz", "dhr", "m.n", "Nat", "ned", "pag", "kap", "(red", "artt", "t.a.v", 
			 "soc", "int", "Kon", "org", "atl", "bur", "eur", "acad", "spec", "sted", "coll", "econ", "inst", "kath", 
			 "geref", "voorl", "centr", "Sticht", "buitenl", "binnenl", "technol", "volksgez",
			 "personeelsz", "kwaliteitsz", "prof.dr", "prof.dr.ir", 
			 "c", "v", "a.d", "o.a", "b.d", "v.d", "o.l.v", "o.v.v", "m.m.v", "t.n.v", "Best", 
			 "dr", "ds", "ed", "mr", "ir", "mw", "st", "vz", "zr", 
			 "fac", "adm", "adv", "gem", "afd", "hfd", "zgn", "mgr", "jhr", "min", "dir", "vml", "vgl", "Vgl", "ond", "ing", "kol", "arr", "drs", 
			 "secr", "pred", "comm", "prof", "prov", "vert", "insp", "resp", "uitg", "mevr", 
			 "distr", "hoogl", "medew", "voorm", "voorz", "waarn", "brandw", "hfddir", "onderz", 
			 "commerc", "hfdbest", "stafafd", "stafdir", 
			 "basisond", "hoofdafd", "hoofddir", "onderafd", "onderdir", "penningm", 
			 "groepsdir", "hoofdambt", "hoofdinsp", "stadsvern", "stafmedew", 
			 "bedrijfdir", "verkoopdir", "beleidsmedew", "secr.-penn", "oud-voorz", 
			 "a.b", "a.h.v", "a.h.w", "a.j.c", "a.m.v.b", "a.o.v", "a.s", "a.u.b", "n.a.v", "t.a.v", "m.a.w", "b.p", "m.b.t", 
			 "b.v", "c.a", "c.q", "c.r.m", "c.s", "d.a.v", "d.d", "d.i", "d.m.v", "d.w.z", "e.a", "e.d", "e.o", "e.v", "g.o.v", 
			 "h.b.s", "h.c.m", "h.m", "i.c", "i.e", "i.p.v", "i.q", "i.v.m", "j.l", "k.b", "k.n.m.i", "l.l", "m.i", "n.a.h.v", 
			 "n.b", "n.h.m", "n.l", "n.r.c", "n.s.b", "n.v", "o.a.c", "o.i", "o.l", "o.m", "t.o.v", "p.a.c", "p.c", "p.i", "p.k.b", 
			 "p.p.c", "p.p.d", "p.p.p", "i.p.v", "r.h.v", "r.k", "r.m.w", "r.o", "r.p.c", "r.p.d", "s.b.m", "s.s", "s.v.p", 
			 "t.a.t", "t.b.v", "t.v", "t.w", "t.z.t", "u.s.a", "v.c", "v.n", "v.o.j", "v.s", "v.v.v", "v.v.s.l", "w.c", "w.g", 
			 "w.i.c", "w.o", "w.r.o", "w.s", "w.v.k", "z.g", "z.i", "z.k.h", "z.m", 
	};
	
	public static String[] functionWords = 
	{
			"Aan", "Aangenomen", "Aangezien", "Al", "Alleen", "Als", "Anders", "Ben", "Bij", "Bijster",
			"Binnenkort", "Boven", "Buiten", "DE", "Daar", "Daarbij", "Daardoor", "Daarmee", "Daarna", "Daarop", "Dat",
			"De", "Deze", "Die", "Dinsdag", "Dit", "Donderdag", "Door", "Drie", "Duizend", "EN", "Een", "Eerste", "Elk", "En",
			"Er", "Even", "Evenals", "Eén", "Gemakshalve", "Gisteravond", "Hem", "Het", "Hieraan", "Hierdoor", "Hiermee",
			"Hierna", "Hieronder", "Hieruit", "Hij", "Hoewel", "Hun", "Hup", "IN", "Iedere", "Iemand", "Ik", "In", "Inmiddels",
			"Intussen", "Is", "Je", "Later", "Linker", "Maandag", "Maar", "Met", "Mijn", "Momenteel", "NIET", "Na", "Naar",
			"Naarmate", "Naast", "Nadat", "Namens", "Niet", "Nu", "Of", "Om", "Omdat", "Onder", "Ongeveer", "Ons", "Ooit",
			"Ook", "Op", "Opnieuw", "Over", "Pas", "Ruim", "SAMEN", "Samen", "Sinds", "Straks", "Te", "Tegenover", "Ten", "Ter",
			"Terwijl", "Tevens", "Tijdens", "Toch", "Toen", "Tot", "Tussen", "Twaalf", "Tweede", "Tweeëntwintig", "Uit",
			"VAN", "Van", "Vanavond", "Verder", "Volgens", "Voor", "Wanneer", "Wat", "We", "Wel", "Welke", "Wij", "Woensdag",
			"Ze", "Zelf", "Zestig", "Zij", "Zijn", "Zo", "Zowel"
	};
	
	private Set<String> abbreviationSet = new HashSet<String>();
	private Set<String> functionWordSet = new HashSet<String>();
	
	
	public JVKSentenceSplitter()
	{
		for (String s: abbreviations)
			abbreviationSet.add(s);
		for (String s: functionWords)
			functionWordSet.add(s);
	}
	@Override

	public void split(TokenWindow w)
	{
		while (true)
		{
			Token t = w.getToken();
			if (t == null) break;

			if (!t.isWord()) 
			{
				String s = t.getContent();
				if (s.contains("?") || s.contains("!"))
				{
					t.setIsEOS(true);
				} else if (s.contains("."))
				{
					Token previous = w.getToken(-1); 
					if (previous != null)
					{	
						boolean couldbeInitial =  isInitial(previous);
						boolean abbreviation = isPossibleAbbreviation(previous);

						Token nextWord = getNextWord(w,1);

						if (nextWord == null)
						{
							t.setIsEOS(true);
						} else
						{
							String nextS = nextWord.getContent();
							if (!couldbeInitial && !abbreviation && 
									nextS.matches("^[A-Z].*"))
								t.setIsEOS(true);
							
							// TODO: 't etc;
							// TODO: number Uppercase etc
							// TODO: uppercase initial need not start sentence
							
							if (isUppercaseFunctionWord(nextWord))
								t.setIsEOS(true);
						}
					}
					//printWindow(w);
				}
			}
			if (!w.shift(1))
				break;
		}
	}

	Token getNextWord(TokenWindow w, int k)
	{
		Token t;
		for (int i=k; true; i++)
		{
			t = w.getToken(i);
			if (t==null)
				break;
			if (t.isWord())
				return t;
		}
		return null;
	}
	
	private boolean isUppercaseFunctionWord(Token t)
	{
		String s = t.getContent();
		return functionWordSet.contains(s);
	}
	
	private boolean isInitial(Token t)
	{
		String s = t.getContent();
		return s.matches("^[A-Z][a-z]?$") || s.matches("[A-Z][a-z]?\\.[A-Z][a-z]?");
	}
	
	private boolean isPossibleAbbreviation(Token t) 
	{
		// TODO Auto-generated method stu
		String s = t.getContent();
		return s.length() == 1 ||
				s.equalsIgnoreCase("th") || 
				s.equalsIgnoreCase("ph") || abbreviationSet.contains(s);
		//return false;
	}
	
	/*
	 * Grefenstette heuristic:
	 * terminated by period
	 * followed by: ,;?[a-z][0-9]
	 * Or word with capital and ending in period
	 */
	
	private boolean isLikelyAbbreviation(Token t, Token next)
	{
		if (next.getContent().matches("^[,;a-z].*"))
		{
			return true;
		}
		return false;
	}
	
	void printWindow(TokenWindow w)
	{
		for (int i=-6; i <= 6; i++)
		{
			Token t = w.getToken(i);
			if (t != null)
			{
				System.err.print(t.getContent());
				if (t.getIsEOS())
					System.err.print("|");
				System.err.print(" ");
			}
		}
		System.err.println("");
	}
}

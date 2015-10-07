package nl.openconvert.sentence;

import java.util.Set;


// public class JVKSentenceSplitter implements SentenceSplitter 

/*
 * Rule Set
word-2 word-1 FOCAL word+1 word+2 Assign Example





ANY Not ABBR [.?!] ANY-OR-NONE ANY-OR-NONE FS book.
ANY CLOSE PUNCT [.?!] ANY-OR-NONE ANY-OR-NONE FS ).
ABBR . [.?!] ANY-OR-NONE ANY-OR-NONE FS Tex.!
C.ANY, C.ANY C.SEMICOLON C.CAP_COMMON C.ANY_OR_NONE L.FS // ; The
C.ANY, C.ABBR C.DOT C.NONE C.NONE AFS // Tex.EOF
C.ANY, C.ABBR C.DOT C.CAP_COMMON C.ANY_OR_NONE L.AFS // Tex. The
C.ANY, C.ABBR C.DOT C.CLOSE_PUNCT C.CAP_COMMON L.AFS // kg.) This
C.ANY, C.ABBR C.DOT C.OPEN_PUNCT C.CAP_COMMON L.AFS // kg. (This
C.ANY, C.ABBR C.DOT C.CLOSE_PUNCT C.CAP_COMMON L.AFS // kg.) (This
// C.ANY C.OPEN PUNCT
C.ANY, C.ABBR C.DOT C.PUNCT C.ANY_OR_NONE L.AP // kg.,
C.ANY, C.ABBR C.DOT C.POSSIBLE_SB C.ANY_OR_NONE L.AP // Tex.!
C.ANY, C.ABBR C.DOT C.LOW_COMMON ANY-OR-NONE L.AP // kg. this
C.ANY, C.ABBR C.DOT CLOSE PUNCT LOW COMMON L.AP kg.) this
C.ANY, C.ABBR C.DOT OPEN PUNCT LOW COMMON L.AP // kg. (this
C.ANY, C.ABBR C.DOT CLOSE PUNCT LOW COMMON L.AP // kg.) (this
// C.ANY C.OPEN PUNCT
C.ANY, C.ABBR C.DOT C.ABBR C.DOT L.AP // Sen. Gen.
C.ANY, C.ABBR C.DOT C.NUM C.ANY_OR_NONE // L.AP kg. 5
C.ANY, C.ABBR C.DOT C.PROPER_NAME C.ANY_OR_NONE L.AP // Dr. Smith
 */

public class MikheevSentenceSplitter implements SentenceSplitter
{

	Set<String> commonWords;
	Set<String> commonSentenceInitialWords;
	Set<String> frequentProperNames;
	Set<String> abbreviations;
	
	
	public static enum L
	{
		FS,//  Punctuation that signals end of sentence
		AP, // Period that is part of abbreviation
		AFS // Period that is part of abbreviation and signals end of sentence
	}
	
	public static enum C
	{
		NONE, // No token (end of input)
		ANY, // Any token
		ANY_OR_NONE, // Any token or no token at all
		ABBR, //  Token that was disambiguated as “abbreviation”
		// (Note: . . . Ellipsis is treated as an abbreviation too)
		NOT_ABBR, //  Nonpunctuation token that was disambiguated as “not abbreviation”
		POSSIBLE_SB, //  [.!?]
		DOT, // .
		SEMICOLON, // ;
		CLOSE_PUNCT, // PUNCT Closing quotes, closing brackets
		OPEN_PUNCT, // PUNCT Opening quotes, opening brackets
		OTHER_PUNCT, /// Punctuation token not CLOSE PUNCT or OPEN PUNCT or [.!?;]
		NUM, //  Number
		LOW_COMMON, // Lower-cased common word
		CAP_COMMON, //  Capitalized word that was disambiguated as a common word
		CAP_PROP, //  Capitalized word that was disambiguated as a proper name
		PROPER_NAME, // Proper name
	};
	
	static class Rule
	{
		C[] condition;
		L label;
		
		public Rule(C[] condition, L label)
		{
			this.condition = condition;
			this.label = label;
		}
	}
	
	static C[] c1 = {C.ANY, C.NOT_ABBR, C.POSSIBLE_SB, C.ANY_OR_NONE, C.ANY_OR_NONE};
		static Rule r1 = new Rule(c1, L.FS);
	static C[] c2 = {C.ANY,  C.CLOSE_PUNCT, C.POSSIBLE_SB,  C.ANY_OR_NONE, C.ANY_OR_NONE};
		static Rule r2 = new Rule(c2, L.FS);
	static C[] c3 = {C.ABBR, C.DOT, C.POSSIBLE_SB, C.ANY_OR_NONE, C.ANY_OR_NONE};
		static Rule r3 = new Rule(c3, L.FS);
	static C[] c4 = {C.ANY, C.ANY, C.SEMICOLON, C.CAP_COMMON, C.ANY_OR_NONE};  
		static Rule r4 = new Rule(c4, L.FS);
	static C[] c5 = {C.ANY, C.ABBR, C.DOT, C.NONE, C.NONE}; // Tex.<EOF>
		static Rule r5 = new Rule(c5, L.AFS);
	static C[] c6 = {C.ANY, C.ABBR, C.DOT, C.CAP_COMMON, C.ANY_OR_NONE}; // Tex. The
		static Rule r6 = new Rule(c6,L.AFS);
	static C[] c7 = {C.ANY, C.ABBR, C.DOT, C.CLOSE_PUNCT, C.CAP_COMMON};  // kg.) This
		static Rule r7 = new Rule(c7,L.AFS);
	static C[] c8 =  {C.ANY, C.ABBR, C.DOT, C.OPEN_PUNCT, C.CAP_COMMON};
		static Rule r8 = new Rule(c8,L.AFS);// kg. (This
	static C[] c9 = {C.ANY, C.ABBR, C.DOT, C.CLOSE_PUNCT, C.CAP_COMMON};
		static Rule r9 = new Rule(c9, L.AFS); // kg.) (This
	// C.ANY C.OPEN PUNCT
	static C[] c10 =  {C.ANY, C.ABBR, C.DOT, C.OTHER_PUNCT, C.ANY_OR_NONE};
		static Rule r10 = new Rule(c10, L.AP); // kg.,
	static C[] c11 = {C.ANY, C.ABBR, C.DOT, C.POSSIBLE_SB, C.ANY_OR_NONE}; 
		static Rule r11 = new Rule(c11, L.AP); // Tex.!
	static C[] c12 =  {C.ANY, C.ABBR, C.DOT, C.LOW_COMMON, C.ANY_OR_NONE};
		static Rule r12 = new Rule(c12, L.AP); // kg. this
	static C[] c13 =  {C.ANY, C.ABBR, C.DOT, C.CLOSE_PUNCT, C.LOW_COMMON} ;
		static Rule r13 = new Rule(c13, L.AP);//  kg.) this
	static C[] c14 =  {C.ANY, C.ABBR, C.DOT, C.OPEN_PUNCT, C.LOW_COMMON} ;
		static Rule r14 = new Rule(c14, L.AP); // kg. (this
	static C[] c15 =  {C.ANY, C.ABBR, C.DOT, C.CLOSE_PUNCT, C.LOW_COMMON} ;
		static Rule r15 = new Rule(c15, L.AP); // kg.) (this
	// C.ANY C.OPEN PUNCT
	
	static C[] c16 =  {C.ANY, C.ABBR, C.DOT, C.ABBR, C.DOT} ;
		static Rule r16 = new Rule(c16, L.AP); // Sen. Gen.
	static C[] c17 =  {C.ANY, C.ABBR, C.DOT, C.NUM, C.ANY_OR_NONE} ;
		static Rule r17 = new Rule(c17, L.AP); // kg. 5; (?) disable for Dutch?
	static C[] c18 =  {C.ANY, C.ABBR, C.DOT, C.PROPER_NAME, C.ANY_OR_NONE} ;
		static Rule r18 = new Rule(c18, L.AP); // Dr. Smith
	
	
	static Rule[] rules = {r1,r2,r3,r4,r5,r6,r7,r9,r10,r11,r12,r13,r14,r15,r16,r17,r18};
	
	@Override
	public void split(TokenWindow w) 
	{
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * The simplest strategy for deciding whether a word that is followed by a period
is an abbreviation or a regular word is to apply well-known heuristics based on the
observation that single-word abbreviations are short and normally do not include
vowels (Mr., Dr., kg.). Thus a word without vowels can be guessed to be an abbreviation
unless it is written in all capital letters and can stand for an acronym or a proper name
(e.g., BBC). A span of single letters separated by periods forms an abbreviation too
(e.g., Y.M.C.A.). A single letter followed by a period is also a very likely abbreviation.
There is also an additional heuristic that classifies as abbreviations short words (with
length less than five characters) that are followed by a period and then by a comma, a
lower-cased word, or a number. All other words are considered to be nonabbreviations.
	 */
	
	static boolean looksLikeAbbreviation(String s) // ! s does not have the dot
	{
		if (s.contains("."))
			return true;
		if (s.matches("[A-Z][a-z]?") || s.matches("[A-Z][a-z]?\\.[A-Z][a-z]?"))
			return true;
		
		return false;
	}

}

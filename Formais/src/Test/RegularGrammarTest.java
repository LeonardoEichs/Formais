package Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;

import RegularLanguages.FiniteAutomata;
import RegularLanguages.RegularGrammar;
import RegularLanguages.RegularLanguage;

public class RegularGrammarTest {
	private static String[] validRG;
	private static String[] invalidRG;
	private static int lengthValid = 5;
	private static int lengthInvalid = 18;

	/**
	 * Possibilities of grammars that are valid
	 */
	@Before
	public void setUpValidGrammars() {
		validRG = new String[lengthValid];
		validRG[0] = "A -> aA | a";
		validRG[1] = "S -> aA\n"
				+ "A -> aA | bB\n"
				+ "B -> bB | cC\n"
				+ "C -> cC | c";
		validRG[2] = "S'''' -> aA | & \n"
				+ "A -> bB | bE\n"
				+ "B -> cC\n"
				+ "E -> cC\n"
				+ "C -> dD\n"
				+ "D -> eD | e";
		validRG[3] = "S -> aA | bB | cC | dD | eE\n"
				+ "A -> aA | a\n"
				+ "B -> bB | b\n"
				+ "C -> cC | c\n"
				+ "D -> dD | d\n"
				+ "E- >e E|e";
		validRG[4] = "S -> aA | bA | a | b | &\n"
				+ "A -> aA | bA | a | b";
	}
	
	@Before
	public void setUpInvalidGrammars() {
		invalidRG = new String[lengthInvalid];
		invalidRG[0] = " -> aA | a";
		invalidRG[1] = "AA -> aA | a\n";
		invalidRG[2] = "";
		invalidRG[3] = "a -> aA | a";
		invalidRG[4] = "A -> ";
		invalidRG[5] = "A -> aAa | a";
		invalidRG[6] = "S -> 00 | 0S";
		invalidRG[7] = "S -> S0 | 0";
		invalidRG[8] = "S -> S0 | 0 | *";
		invalidRG[9] = "S -> 0 | | 0S";
		invalidRG[10] = "S -> 0S | A | 1";
		invalidRG[11] = "S -> 0 | | 0S";
		invalidRG[12] = "S -> 0&";
		invalidRG[13] = "S -> &A";
		invalidRG[14] = "S -> aS | a | &";
		invalidRG[15] = "S -> & | a | aS";
		invalidRG[16] = "S -> 0A | &\n"
				+ "A -> aS";
		invalidRG[17] = "S -> aA \n" + 
				"A -> aS\n" + 
				"S -> &";
	}

	@Test
	public void testValidGrammar() {
		RegularLanguage rg[] = new RegularLanguage[lengthValid]; 
		int i = 0;
		for (String grammar : validRG) {
			rg[i++] = RegularGrammar.isValidRG(grammar);
		}
		for (RegularLanguage lr : rg) {
			assertNotNull(lr);
		}
	}

	@Test
	public void testInvalidGrammar() {
		RegularLanguage rg[] = new RegularLanguage[lengthInvalid];
		int i = 0;
		for (String grammar: invalidRG) {
			rg[i++] = RegularGrammar.isValidRG(grammar);
		}
		for (RegularLanguage l : rg) {
			assertNull(l);
		}
	}
	
}

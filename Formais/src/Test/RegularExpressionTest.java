package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import RegularLanguages.RegularExpression;
import RegularLanguages.RegularLanguage;

class RegularExpressionTest {
	private static String[] validRE;
	private static String[] invalidRE;
	private static int lengthValid = 16;
	private static int lengthInvalid = 15;
	

	@BeforeEach
	void setUpValidRE(){
		validRE= new String[lengthValid];
		validRE[0] = "ab";
		validRE[1] = "(ab)";
		validRE[2] = "(ab)*";
		validRE[3] = "a???";
		validRE[4] = "a***";
		validRE[5] = "a+++";
		validRE[6] = "(((a)))";
		validRE[7] = "(a | ab | c*)*";
		validRE[8] = "(a | ab | c*)*??";
		validRE[9] = "(a | (ab | cd)+)+";
		validRE[10] = "0 (01 | (02) * | (03)++) | (a?01?)";
		validRE[11] = "a | &";
		validRE[12] = "&*";
		validRE[13] = "(())*";
		validRE[14] = "(a*b)*";
		validRE[15] = "(a | & | a*b?c+)*";
	}
	
	@BeforeEach
	void setUpInvalidRE(){
		invalidRE= new String[lengthInvalid];
		invalidRE[0] = "(ab";
		invalidRE[1] = "ab)";
		invalidRE[2] = "-";
		invalidRE[3] = "((ab)";
		invalidRE[4] = "(ab))";
		invalidRE[5] = "a | b | +c";
		invalidRE[6] = "a | b | *c";
		invalidRE[7] = "a | b | ?c";
		invalidRE[8] = "a | b | - | c";
		invalidRE[9] = "(a(b(c(d)*)+)*";
		invalidRE[10] = "a | b | +";
		invalidRE[11] = "a | b | $ | -";
		invalidRE[12] = "(()";
		invalidRE[13] = "(|a?(?)";
		invalidRE[14] = "(.b.)";
	}	

	@Test
	void testValidRE() {
		RegularLanguage rl[] = new RegularLanguage[lengthValid]; 
		int i = 0;
		for (String re : validRE) {
			rl[i++] = RegularExpression.isValidRE(re);
		}
		for (RegularLanguage lr : rl) {
			assertNotNull(lr);
		}		
	}

	
	@Test
	void testInvalidRE() {
		RegularLanguage rl[] = new RegularLanguage[lengthInvalid];
		int i = 0;
		for (String re: invalidRE) {
			rl[i++] = RegularExpression.isValidRE(re);
		}
		int j = 0;
		for (RegularLanguage l : rl) {
			System.out.println(j);
			j++;
			assertNull(l);
		}
	}	

}
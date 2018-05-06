package Test;

import RegularLanguages.RegularGrammar;

public class Main {
	public static void main(String[] arg) {
		System.out.println("Hello");
		RegularGrammar rg = new RegularGrammar("S->aA");
		System.out.println(rg.getType());
		System.out.println(RegularGrammar.lexicalValidation("S -> aA | bB \n"
				+ "A -> a \n"
				+ "B -> b \n"));
	}
}

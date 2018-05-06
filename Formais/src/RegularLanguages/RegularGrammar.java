package RegularLanguages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RegularGrammar extends RegularLanguage{
	
	private HashSet<Character> vn;
	private HashSet<Character> vt;
	private HashMap<Character, HashSet<String>> productions;
	private char s;

	public RegularGrammar(String input) {
		super(input, InputType.RG);
		vn = new HashSet<Character>();
		vt = new HashSet<Character>();
		productions = new HashMap<Character, HashSet<String>>();
	}

	public static RegularLanguage isValidRG(String inp) {
		RegularGrammar rg = new RegularGrammar(inp);
		if(!lexicalValidation(inp)) {
			return null;
		}
		String[] productions = getProductions(inp);
		validateProductions(productions, rg);
		if(rg.vn.isEmpty()) {
			return null;
		}
		
		return rg;
	}
	
	private static String[] getProductions(String inp) {
		String[] prod = inp.split("\\r\\n|\\r|\\n");
		int i = 0;
		for (String s : prod) {
			prod[i++] = s.replaceAll("\\s+", "");
		}
		return prod;
	}

	private static boolean lexicalValidation(String inp) {
		String formatted = inp.replaceAll("\\s+", "");
		if(!formatted.matches("^[a-zA-Z0-9\\->|&]+"))
			return false;
		return true;
	}

	private static void validateProductions(String[] productions, RegularGrammar rg) {
		// TODO Auto-generated method stub
		
	}

	public Set<Character> getVn() {
		return vn;
	}
	
	public Set<Character> getVt() {
		return vt;
	}
	
	public char getInitial() {
		return s;
	}
	
	public Set<String> getProductions(char vn) {
		Set<String> prod = productions.get(vn);
		if(prod == null) {
			prod = new HashSet<String>();
		}
		return prod;
	}

	@Override
	public String getDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RegularGrammar getRG() {
		return this;
	}

	@Override
	public RegularExpression getRE() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FiniteAutomata getFA() {
		// TODO Auto-generated method stub
		return null;
	}
	

}

package RegularLanguages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class RegularGrammar extends RegularLanguage{
	
	private HashSet<Character> vn;
	private HashSet<Character> vt;
	private HashMap<Character, HashSet<String>> productions;
	private char s;
	private static Scanner prodScan;

	public RegularGrammar(String input) {
		super(input, InputType.RG);
		s = ' ';
		vn = new HashSet<Character>();
		vt = new HashSet<Character>();
		productions = new HashMap<Character, HashSet<String>>();
	}
	
	private static boolean lexicalValidation(String inp) {
		String formatted = inp.replaceAll("\\s+", ""); // replace whitespace
		if(!formatted.matches("^[a-zA-Z0-9\\->|&]+"))
			return false;
		return true;
	}
	
	public static RegularLanguage isValidRG(String inp) {
		if(!lexicalValidation(inp)) {
			return null;
		}
		RegularGrammar rg = new RegularGrammar(inp);
		String[] productions = inp.split("\\r\\n|\\r|\\n");
		int i = 0;
		for (String s : productions) {
			productions[i++] = s.replaceAll("\\s+", "");
		}

		validateProductions(productions, rg);
		if(rg.vn.isEmpty()) {
			return null;
		}
		
		return rg;
	}

	private static RegularGrammar validateProductions(String[] productions, RegularGrammar rg) {
		String production = "";
		char vn;
		String[] vnProd;
		HashSet<String> prodList = new HashSet<String>();
		for(int i = 0; i < productions.length; i++) {
			production = productions[i];
			vnProd = production.split("->");
			if(vnProd.length == 2) {
				if(vnProd[0].length() > 1)
					return null;
				vn = vnProd[0].charAt(0);
				System.out.println(vn);
				prodList = rg.productions.get(vn);
				if(prodList == null)
					prodList = new HashSet<String>();
				if(!(Character.isLetter(vn)) || (Character.isLowerCase(vn))) {
					rg.vn = new HashSet<Character>();
					return null;
				}
				rg.vn.add(vn);
				if(rg.s == ' ')
					rg.s = vn;
				System.out.println(production);
				if(!verifyProduction(vn, vnProd[1], prodList, rg)) {
					rg.vn.clear();
					return null;
				}
				
			}
		}
		return rg;
		
	}

	private static boolean verifyProduction(char vn, String productions, HashSet<String> prodList, RegularGrammar rg) {
		// Iterate every production for every vN
		String prod = productions.substring(productions.indexOf("->")+2);
		int prodLength = 0;
		char first, second;
		prodScan = new Scanner(prod);
		prodScan.useDelimiter("[|]");
		if (prod.length() < 1) {
			prodScan.close();
			return false;
		}
		
		while (prodScan.hasNext()) {
			prod = prodScan.next();
			prodLength = prod.length();
			if (prodLength < 1 || prodLength > 2) { // |prod| = 0 || |prod| = 2
				prodScan.close();
				return false;
			} else { // |prod| = 1 or 2
				first = prod.charAt(0);
				if (Character.isUpperCase(first)){
					prodScan.close();
					return false;
				}
				if (Character.isDigit(first)
						|| Character.isLetter(first)) { // if first symbol is terminal
					rg.vt.add(first); //
				}
				
				if (prodLength == 2) { // |prod| = 2
					second = prod.charAt(1);
					if (Character.isUpperCase(first)) { // if first symbol is vN
						prodScan.close();
						return false;
					} else if (Character.isLowerCase(second)
							|| Character.isDigit(second)) { // if both are lower case or digit
						prodScan.close();
						return false;
					} else if (first == '&' || second == '&') {
						prodScan.close();
						return false;
					}
					if (second == rg.s && rg.vt.contains('&')) {
						prodScan.close();
						return false;
					}
					rg.vn.add(second);
					prodList.add(prod);
					rg.productions.put(vn, prodList);
				} else { // |prod| = 1
					if (Character.isUpperCase(first)) { // if S -> A
						prodScan.close();
						return false;
					}
					if (first == '&') {
						rg.vt.add(first);
						if (vn != rg.s) {
							prodScan.close();
							return false;
						} else if (rg.productions.values().stream().anyMatch(
								list -> list.stream().anyMatch(
										pr -> pr.length() > 1 && pr.charAt(1) == vn
						))) {  // if S -> & and X -> aS
							prodScan.close();
							return false;
						}
					}
					prodList.add(prod);
					rg.productions.put(vn, prodList);
				}
			}
		}
		prodScan.close();
		return true;
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
		String grammar = "";
		String aux = "";
		HashSet<String> prodList;
		
		for (Character vN : this.productions.keySet()) {
			prodList = this.productions.get(vN);
			
			for (String prod : prodList) {
				aux += prod + " | ";
			}
			aux = aux.substring(0, aux.length()-2);
			if (vN.equals(this.s)) {
				grammar = vN + " -> " + aux + "\n" + grammar;
			} else {
				grammar += vN + " -> " + aux + "\n";
			}
			aux = "";
		}
		return grammar;
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

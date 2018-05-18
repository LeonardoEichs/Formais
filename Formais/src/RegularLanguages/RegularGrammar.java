package RegularLanguages;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class RegularGrammar extends RegularLanguage{
	
	private HashSet<String> vn;
	private HashSet<Character> vt;
	private HashMap<String, HashSet<String>> productions;
	private String s;

	public RegularGrammar(String input) {
		super(input, InputType.RG);
		s = "";
		vn = new HashSet<String>();
		vt = new HashSet<Character>();
		productions = new HashMap<String, HashSet<String>>();
	}
	
	private static boolean lexicalValidation(String inp) {
		String formatted = inp.replaceAll("\\s+", "");
		if(!formatted.matches("^[a-zA-Z0-9\\->|&']+"))
			return false;
		return true;
	}
	
	public static RegularGrammar isValidRG(String inp) {
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
		String vn;
		String[] vnProd;
		HashSet<String> prodList = new HashSet<String>();
		for(int i = 0; i < productions.length; i++) {
			production = productions[i];
			vnProd = production.split("->");
			if(vnProd.length == 2) {
				if(vnProd[0].length() > 1) {
					if(!(vnProd[0].matches(".[\\']+")))
						return null;
				}
				vn = vnProd[0];
				prodList = rg.productions.get(vn);
				if(prodList == null)
					prodList = new HashSet<String>();
				if(!(Character.isLetter(vn.charAt(0))) || (Character.isLowerCase(vn.charAt(0)))) {
					rg.vn = new HashSet<String>();
					return null;
				}
				rg.vn.add(vn);
				if(rg.s == "")
					rg.s = vn;
				if(!verifyProduction(vn, vnProd[1], prodList, rg)) {
					rg.vn.clear();
					return null;
				}
			}
		}
		for(String c : rg.vn) { // Vn com produções vazias
			if(rg.getProductions(c).isEmpty()) {
				rg.vn.clear();
				return null;
			}
		}
		return rg;
		
	}

	private static boolean verifyProduction(String vn2, String production, HashSet<String> prodList, RegularGrammar rg) {
		String[] prods = production.split("\\|");
		String prod;
		String second_to_end;
		if (production.length() < 1) {
			return false;
		}
		int i = 0;
		for(String p : prods) {
			prod = prods[i++];
			if(prod.length() > 2) {
				if(!(prod.matches("^[a-z][A-Z][\\']+$"))) {
					return false;
				}	
			}
			Character first = prod.charAt(0);
			if(Character.isUpperCase(first)) {
				return false;
			}
			if(Character.isDigit(first) || Character.isLowerCase(first)) {
				rg.vt.add(first);
			}
			if(prod.length() > 2) {

				Character second = prod.charAt(1);
				second_to_end = prod.substring(1);
				if(Character.isUpperCase(first)) {
					return false;
				}
				else if(Character.isLowerCase(second) || Character.isDigit(second) || first == '&' || second == '&') {
					return false;
				}
				if(second_to_end == rg.s && rg.vt.contains('&')) {
					return false;
				}
				rg.vn.add(second_to_end);
				prodList.add(prod);
				rg.productions.put(vn2, prodList);
			}
			else {
				if(Character.isUpperCase(first)) {
					return false;
				}
				if(first == '&') {
					rg.vt.add(first);
					if(vn2 != rg.s) {
						return false;
					}
					else if(rg.productions.values().stream().anyMatch(list -> list.stream().anyMatch(pr -> pr.length() > 1 && pr == vn2))) {
						return false;
					}
				}
				prodList.add(prod);
				rg.productions.put(vn2, prodList);
			}
		}
		return true;
	}

	public HashSet<String> getVn() {
		return vn;
	}
	
	public Set<Character> getVt() {
		return vt;
	}
	
	public String getInitial() {
		return s;
	}
	
	public Set<String> getProductions(String c) {
		Set<String> prod = productions.get(c);
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
		
		for (String vN : this.productions.keySet()) {
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
		return null;
	}

	@Override
	public FiniteAutomata getFA() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static String mapToInput(HashMap<String, HashSet<String>> new_prod, String string) {
		
		String aux = "";
		
		aux = string + " ->";
		boolean first = true;
		for(String s : new_prod.get(string)) {
			if(first) {
				aux = aux + " " + s;
				first = false;
			}
			else {
				aux = aux + " | " + s;
			}
		}
		aux = aux + " \n";

		for (Map.Entry<String, HashSet<String>> entry : new_prod.entrySet()) {
			String key = entry.getKey();
		    if(key == string)
		    	continue;
		    HashSet<String> value = entry.getValue();
		    
		    aux = aux + key.toString() + " ->";
		    
			first = true;
		    for(String s : value) {
				if(first) {
					aux = aux + " " + s;
					first = false;
				}
				else {
					aux = aux + " | " + s;
				}
			}
			aux = aux + " \n";
		}
		
		aux = aux.trim();		
		return aux;
		
	}
	
	private static HashMap<String, HashSet<String>> deepCloneHashMap(HashMap<String, HashSet<String>> productions2) {
		HashMap<String, HashSet<String>> deepClone = new HashMap<String, HashSet<String>>();
		   
		for (Map.Entry<String, HashSet<String>> entry : productions2.entrySet()){
	        deepClone.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
	    }
		
		return deepClone;

	}
	
	private static RegularGrammar renameStates(RegularGrammar rg, String initialNew) {		
		HashMap<String, String> vn_r = new HashMap<String, String>();

		HashMap<String, HashSet<String>> new_prod = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> copy = deepCloneHashMap(rg.productions);
		Pattern textPattern = Pattern.compile("^[a-z][A-Z][\\']*$");
		
		for(String p : rg.vn) {
			for(String p2 : rg.vn) {
				for(String s : rg.productions.get(p2)) {
					if(s.length() == 1)
						continue;
					if(textPattern.matcher(s).matches()) {
						copy.get(p2).remove(s);
					}
				}
			}
		}

		char alphabet = initialNew.charAt(0);
		int endAlphabet;
		if(initialNew.length() > 1) {
			String substring = initialNew.substring(1);
			endAlphabet = substring.length();
		}
		else {
			endAlphabet = 0;
		}
		
		String new_symbol;
		vn_r.put(rg.s, initialNew);
		alphabet++;
		for(String p : rg.vn) {
			if(p != rg.s) {
				if(endAlphabet == 0) {
					vn_r.put(p, Character.toString(alphabet));
				}
				else {
					new_symbol = Character.toString(alphabet);
					for(int i = 0; i< endAlphabet; i++) {
						new_symbol = new_symbol + "'";
					}
					vn_r.put(p, new_symbol);
				}
				if(alphabet == 'Z') {
					alphabet = 'A';
					endAlphabet++;
				}
				else {
					alphabet++;
				}
			}
		}

		for(String p : rg.vn) {
			for(String p2 : rg.vn) {
				for(String s : rg.productions.get(p2)) {
					if(s.length() == 1)
						continue;
					if(s.substring(1).equals(p)) {
						copy.get(p2).add(s.replace(p, vn_r.get(p)));
					}
				}
			}
		}
		for(String p : vn_r.keySet()) {
			new_prod.put(vn_r.get(p), copy.remove(p));
		}
		RegularGrammar new_rg = isValidRG(mapToInput(new_prod, vn_r.get(rg.s)));
		
		return new_rg;
	}
	
	private static String returnLastNonTerminal(RegularGrammar rg, String initialNew) {		
		HashMap<String, String> vn_r = new HashMap<String, String>();

		char alphabet = initialNew.charAt(0);
		int endAlphabet;
		if(initialNew.length() > 1) {
			String substring = initialNew.substring(1);
			endAlphabet = substring.length();
		}
		else {
			endAlphabet = 0;
		}
				
		String new_symbol;
		vn_r.put(rg.s, initialNew);
		alphabet++;
		for(String p : rg.vn) {
			if(p != rg.s) {
				if(endAlphabet == 0) {
					vn_r.put(p, Character.toString(alphabet));
				}
				else {
					new_symbol = Character.toString(alphabet);
					for(int i = 0; i< endAlphabet; i++) {
						new_symbol = new_symbol + "'";
					}
					vn_r.put(p, new_symbol);
				}
				if(alphabet == 'Z') {
					alphabet = 'A';
					endAlphabet++;
				}
				else {
					alphabet++;
				}
			}
		}
		
		new_symbol = Character.toString(alphabet);
		for(int i = 0; i< endAlphabet; i++) {
			new_symbol = new_symbol + "'";
		}
		return new_symbol;
	}
	
	public RegularGrammar union(RegularGrammar rg2) {
		
		RegularGrammar new_rg = renameStates(this, "B");
		
		String lastSymbol = returnLastNonTerminal(this, "B");
		
		RegularGrammar new_rg2 = renameStates(rg2, lastSymbol);
		
		HashSet<String> prodList = new HashSet<String>();
		for(String s : new_rg.productions.get(new_rg.s))
			prodList.add(s);
		for(String s : new_rg2.productions.get(new_rg2.s))
			prodList.add(s);
		HashMap<String, HashSet<String>> prodrUnion = new HashMap<String, HashSet<String>>();
		prodrUnion.put("A", prodList);
		
		if(new_rg.productions.get(new_rg.s).toString().contains("&"))
			new_rg.productions.get(new_rg.s).remove("&");
		if(new_rg2.productions.get(new_rg2.s).toString().contains("&"))
			new_rg2.productions.get(new_rg2.s).remove("&");
		
		prodrUnion.putAll(new_rg.productions);
		prodrUnion.putAll(new_rg2.productions);

		RegularGrammar rg = isValidRG(mapToInput(prodrUnion, "A"));
		return rg;
	}
	
	public RegularGrammar concatenation(RegularGrammar rg2) {
		
		boolean empty = false;
		char alphabet = 'A';
		if(this.productions.get(this.s).toString().contains("&")) {
			alphabet = 'B';
			empty = true;
		}

		RegularGrammar new_rg = renameStates(this, Character.toString(alphabet));
		
		String lastSymbol = returnLastNonTerminal(this, Character.toString(alphabet));
		
		RegularGrammar new_rg2 = renameStates(rg2, lastSymbol);
				
		if(new_rg.productions.get(new_rg.s).toString().contains("&"))
			new_rg.productions.get(new_rg.s).remove("&");
		if(new_rg2.productions.get(new_rg2.s).toString().contains("&"))
			new_rg2.productions.get(new_rg2.s).remove("&");
		
		HashSet<String> prodList;
		for (String p : new_rg.productions.keySet()) {
			prodList = new HashSet<String>(); 
			for(String s : new_rg.productions.get(p)) {
				if(s.length() == 1) {
					s = s + new_rg2.s;
				}
				prodList.add(s);
			}
			new_rg.productions.replace(p, prodList);
		}

		new_rg.productions.putAll(new_rg2.productions);
		
		if(empty) {
			prodList = new HashSet<String>();
			prodList.add("&");
			for(String s : new_rg.productions.get(new_rg.s)) {
				prodList.add(s);
			}
			new_rg.productions.put("A", prodList);
		}
		
		RegularGrammar rg = isValidRG(mapToInput(new_rg.productions, "A"));
		
		return rg;
		
	}
	
	public RegularGrammar closurePlus() {
		
		RegularGrammar new_rg = renameStates(this, "A");
		
		HashSet<String> prodList = new HashSet<String>();
		
		HashMap<String, HashSet<String>> clone = deepCloneHashMap(new_rg.productions);
		
		boolean empty = false;
		if(clone.get(new_rg.s).toString().contains("&")) {
			empty = true;
			clone.get(new_rg.s).remove("&");
		}
		
		for (String p : clone.keySet()) {
			prodList = new HashSet<String>();
			for(String s : clone.get(p)) {
				String s2;
				if(s.length() == 1) {
					s2 = s + new_rg.s;
					prodList.add(s2);
				}
				prodList.add(s);
			}
			clone.replace(p, prodList);
		}

		if(empty) {
			prodList = new HashSet<String>();
			prodList.add("&");
			for(String s : clone.get(new_rg.s)) {
				prodList.add(s);
			}
			clone.put(new_rg.s, prodList);
		}
				
		RegularGrammar rg = isValidRG(mapToInput(clone, new_rg.s));
		
		
		return rg;
	}
	
	public RegularGrammar closureKleene() {
		
		RegularGrammar rg = this.closurePlus();
		RegularGrammar new_rg = renameStates(rg, "B");
		
		HashSet<String> prodList = new HashSet<String>();
		prodList.add("&");
		for(String s : new_rg.productions.get(new_rg.s)) {
			prodList.add(s);
		}
		new_rg.productions.put("A", prodList);
		
		RegularGrammar new_rg2 = isValidRG(mapToInput(new_rg.productions, "A"));
		
		return new_rg2;
	}



}

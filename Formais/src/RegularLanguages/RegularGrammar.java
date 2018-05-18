package RegularLanguages;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class RegularGrammar extends RegularLanguage{
	
	private HashSet<Character> vn;
	private HashSet<Character> vt;
	private HashMap<Character, HashSet<String>> productions;
	private char s;

	public RegularGrammar(String input) {
		super(input, InputType.RG);
		s = ' ';
		vn = new HashSet<Character>();
		vt = new HashSet<Character>();
		productions = new HashMap<Character, HashSet<String>>();
	}
	
	private static boolean lexicalValidation(String inp) {
		String formatted = inp.replaceAll("\\s+", "");
		if(!formatted.matches("^[a-zA-Z0-9\\->|&]+"))
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
				if(!verifyProduction(vn, vnProd[1], prodList, rg)) {
					rg.vn.clear();
					return null;
				}
			}
		}
		for(char c : rg.vn) { // Vn com produções vazias
			if(rg.getProductions(c).isEmpty()) {
				rg.vn.clear();
				return null;
			}
		}
		return rg;
		
	}

	private static boolean verifyProduction(char vn, String production, HashSet<String> prodList, RegularGrammar rg) {
		String[] prods = production.split("\\|");
		String prod;
		if (production.length() < 1) {
			return false;
		}
		int i = 0;
		for(String p : prods) {
			prod = prods[i++];
			if(prod.length() > 2) {
				return false;
			}
			else {
				Character first = prod.charAt(0);
				if(Character.isUpperCase(first)) {
					return false;
				}
				if(Character.isDigit(first) || Character.isLowerCase(first)) {
					rg.vt.add(first);
				}
				if(prod.length() == 2) {
					Character second = prod.charAt(1);
					if(Character.isUpperCase(first)) {
						return false;
					}
					else if(Character.isLowerCase(second) || Character.isDigit(second) || first == '&' || second == '&') {
						return false;
					}
					if(second == rg.s && rg.vt.contains('&')) {
						return false;
					}
					rg.vn.add(second);
					prodList.add(prod);
					rg.productions.put(vn, prodList);
				}
				else {
					if(Character.isUpperCase(first)) {
						return false;
					}
					if(first == '&') {
						rg.vt.add(first);
						if(vn != rg.s) {
							return false;
						}
						else if(rg.productions.values().stream().anyMatch(list -> list.stream().anyMatch(pr -> pr.length() > 1 && pr.charAt(1) == vn))) {
							return false;
						}
					}
					prodList.add(prod);
					rg.productions.put(vn, prodList);
				}
			}
		}
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
		return null;
	}

	@Override
	public FiniteAutomata getFA() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static String mapToInput(HashMap<Character, HashSet<String>> inp, char initial) {
		
		String aux = "";
		
		aux = initial + " ->";
		boolean first = true;
		for(String s : inp.get(initial)) {
			if(first) {
				aux = aux + " " + s;
				first = false;
			}
			else {
				aux = aux + " | " + s;
			}
		}
		aux = aux + " \n";

		for (Map.Entry<Character, HashSet<String>> entry : inp.entrySet()) {
		    Character key = entry.getKey();
		    if(key == initial)
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
	
	private static HashMap<Character, HashSet<String>> deepCloneHashMap(HashMap<Character, HashSet<String>> hashmap) {
		HashMap<Character, HashSet<String>> deepClone = new HashMap<Character, HashSet<String>>();
		   
		for (Map.Entry<Character, HashSet<String>> entry : hashmap.entrySet()){
	        deepClone.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
	    }
		
		return deepClone;

	}
	
	private static RegularGrammar renameStates(RegularGrammar rg, char initialNew) {
		char initial_r = 0;
		
		HashMap<Character, Character> vn_r = new HashMap<Character, Character>();

		HashMap<Character, HashSet<String>> new_prod = new HashMap<Character, HashSet<String>>();
		HashMap<Character, HashSet<String>> copy = deepCloneHashMap(rg.productions);


		char alphabet = initialNew;
		

		for (char p : rg.vn) {
			if(p == rg.s)
				initial_r = alphabet;
			for(char p2 : rg.vn) {				
				for(String s : copy.get(p2)) {
					if(s.indexOf(p) >= 0) {
						rg.productions.get(p2).remove(s);
						rg.productions.get(p2).add(s.replace(p, alphabet));
					}
				}
			}
			vn_r.put(p, alphabet);
			alphabet++;
		}
		for(char p : vn_r.keySet()) {
			new_prod.put(vn_r.get(p), rg.productions.remove(p));
		}
		
		RegularGrammar new_rg = isValidRG(mapToInput(new_prod, initial_r));
		
		return new_rg;
	}
	
	public RegularGrammar union(RegularGrammar rg2) {
		
		RegularGrammar new_this = renameStates(this, 'B');
		System.out.println(new_this.productions);
		
		/*
		HashSet<String> prodList = new HashSet<String>();

		char initial_r1 = 0;
		char initial_r2 = 0;
		
		HashMap<Character, Character> vn_r1 = new HashMap<Character, Character>();
		HashMap<Character, Character> vn_r2 = new HashMap<Character, Character>();

		HashMap<Character, HashSet<String>> prodr1 = new HashMap<Character, HashSet<String>>();
	    
		for (Map.Entry<Character, HashSet<String>> entry : this.productions.entrySet()){
			prodr1.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
	    }
		
		HashMap<Character, HashSet<String>> prodr1_2 = new HashMap<Character, HashSet<String>>();

		HashMap<Character, HashSet<String>> prodr2 = new HashMap<Character, HashSet<String>>();
	    
		for (Map.Entry<Character, HashSet<String>> entry : rg2.productions.entrySet()){
	    	prodr2.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
		}
		
		HashMap<Character, HashSet<String>> prodr2_2 = new HashMap<Character, HashSet<String>>();
		
		HashMap<Character, HashSet<String>> copy = new HashMap<Character, HashSet<String>>();
	    for (Map.Entry<Character, HashSet<String>> entry : this.productions.entrySet()){
	        copy.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
	    }

		HashMap<Character, HashSet<String>> copy2 = new HashMap<Character, HashSet<String>>();
	    for (Map.Entry<Character, HashSet<String>> entry : rg2.productions.entrySet()){
	        copy2.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
	    }
		
		HashMap<Character, HashSet<String>> prodrUnion = new HashMap<Character, HashSet<String>>();
		
		char alphabet = 'B';

		for (char p : this.vn) {
			if(p == this.s)
				initial_r1 = alphabet;
			for(char p2 : this.vn) {
				for(String s : copy.get(p2)) {
					if(s.indexOf(p) >= 0) {
						prodr1.get(p2).remove(s);
						prodr1.get(p2).add(s.replace(p, alphabet));

					}
				}
			}
			vn_r1.put(p, alphabet);
			alphabet++;
		}
		for(char p : vn_r1.keySet()) {
			prodr1_2.put(vn_r1.get(p), prodr1.remove(p));
		}
		
		for (char p : rg2.vn) {
			if(p == rg2.s)
				initial_r2 = alphabet;
			for(char p2 : rg2.vn) {
				for(String s : copy2.get(p2)) {
					if(s.indexOf(p) >= 0) {
						prodr2.get(p2).remove(s);
						prodr2.get(p2).add(s.replace(p, alphabet));

					}
				}
			}
			vn_r2.put(p, alphabet);
			alphabet++;
		}
		for(char p : vn_r2.keySet()) {
			prodr2_2.put(vn_r2.get(p), prodr2.remove(p));
		}
		
		prodList = new HashSet<String>();
		for(String s : prodr1_2.get(initial_r1))
			prodList.add(s);
		for(String s : prodr2_2.get(initial_r2))
			prodList.add(s);
		prodrUnion.put('A', prodList);
		
		if(prodr1_2.get(initial_r1).toString().contains("&"))
			prodr1_2.get(initial_r1).remove("&");
		if(prodr2_2.get(initial_r2).toString().contains("&"))
			prodr2_2.get(initial_r2).remove("&");
		
		prodrUnion.putAll(prodr1_2);
		prodrUnion.putAll(prodr2_2);

		RegularGrammar rg = isValidRG(mapToInput(prodrUnion, 'A'));
		*/
		//return rg;
		return null;
	}
	
	public RegularGrammar concatenation(RegularGrammar rg2) {
		
		HashSet<String> prodList = new HashSet<String>();

		char initial_r1 = 0;
		char initial_r2 = 0;
		
		HashMap<Character, Character> vn_r1 = new HashMap<Character, Character>();
		HashMap<Character, Character> vn_r2 = new HashMap<Character, Character>();

		HashMap<Character, HashSet<String>> prodr1 = new HashMap<Character, HashSet<String>>();
	    
		for (Map.Entry<Character, HashSet<String>> entry : this.productions.entrySet()){
			prodr1.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
	    }
		
		HashMap<Character, HashSet<String>> prodr1_2 = new HashMap<Character, HashSet<String>>();

		HashMap<Character, HashSet<String>> prodr2 = new HashMap<Character, HashSet<String>>();
	    
		for (Map.Entry<Character, HashSet<String>> entry : rg2.productions.entrySet()){
	    	prodr2.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
		}
		
		HashMap<Character, HashSet<String>> prodr2_2 = new HashMap<Character, HashSet<String>>();
		
		HashMap<Character, HashSet<String>> copy = new HashMap<Character, HashSet<String>>();
	    for (Map.Entry<Character, HashSet<String>> entry : this.productions.entrySet()){
	        copy.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
	    }

		HashMap<Character, HashSet<String>> copy2 = new HashMap<Character, HashSet<String>>();
	    for (Map.Entry<Character, HashSet<String>> entry : rg2.productions.entrySet()){
	        copy2.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
	    }

		
		boolean empty = false;
		char alphabet = 'A';
		if(prodr1.get(this.s).toString().contains("&")) {
			alphabet = 'B';
			empty = true;
		}

		for (char p : this.vn) {
			if(p == this.s)
				initial_r1 = alphabet;
			for(char p2 : this.vn) {
				for(String s : copy.get(p2)) {
					if(s.indexOf(p) >= 0) {
						prodr1.get(p2).remove(s);
						prodr1.get(p2).add(s.replace(p, alphabet));
					}
				}
			}
			vn_r1.put(p, alphabet);
			alphabet++;
		}
		for(char p : vn_r1.keySet()) {
			prodr1_2.put(vn_r1.get(p), prodr1.remove(p));
		}		
		
		for (char p : rg2.vn) {
			if(p == rg2.s)
				initial_r2 = alphabet;
			for(char p2 : rg2.vn) {
				for(String s : copy2.get(p2)) {
					if(s.indexOf(p) >= 0) {
						prodr2.get(p2).remove(s);
						prodr2.get(p2).add(s.replace(p, alphabet));

					}
				}
			}
			vn_r2.put(p, alphabet);
			alphabet++;
		}
		for(char p : vn_r2.keySet()) {
			prodr2_2.put(vn_r2.get(p), prodr2.remove(p));
		}
				
		if(prodr1_2.get(initial_r1).toString().contains("&"))
			prodr1_2.get(initial_r1).remove("&");
		if(prodr2_2.get(initial_r2).toString().contains("&"))
			prodr2_2.get(initial_r2).remove("&");
		
		for (char p : prodr1_2.keySet()) {
			prodList = new HashSet<String>(); 
			for(String s : prodr1_2.get(p)) {
				if(s.length() == 1) {
					s = s + initial_r2;
				}
				prodList.add(s);
			}
			prodr1_2.replace(p, prodList);
		}

		prodr1_2.putAll(prodr2_2);
		
		if(empty) {
			prodList = new HashSet<String>();
			prodList.add("&");
			for(String s : prodr1_2.get(initial_r1)) {
				prodList.add(s);
			}
			prodr1_2.put('A', prodList);
		}
		
		RegularGrammar rg = isValidRG(mapToInput(prodr1_2, 'A'));
		
		return rg;
		
	}
	
	public RegularGrammar closurePlus() {
		
		char initial_r1 = 0;

		HashMap<Character, Character> vn_r1 = new HashMap<Character, Character>();

		HashMap<Character, HashSet<String>> prodr1 = new HashMap<Character, HashSet<String>>();
	    
		for (Map.Entry<Character, HashSet<String>> entry : this.productions.entrySet()){
			prodr1.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
	    }
		
		HashMap<Character, HashSet<String>> prodr1_2 = new HashMap<Character, HashSet<String>>();
		
		HashMap<Character, HashSet<String>> copy = new HashMap<Character, HashSet<String>>();
	    for (Map.Entry<Character, HashSet<String>> entry : this.productions.entrySet()){
	        copy.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
	    }
		

		char alphabet = 'A';

		for (char p : this.vn) {
			if(p == this.s)
				initial_r1 = alphabet;
			for(char p2 : this.vn) {
				for(String s : copy.get(p2)) {
					if(s.indexOf(p) >= 0) {
						prodr1.get(p2).remove(s);
						prodr1.get(p2).add(s.replace(p, alphabet));

					}
				}
			}
			vn_r1.put(p, alphabet);
			alphabet++;
		}

		for(char p : vn_r1.keySet()) {
			prodr1_2.put(vn_r1.get(p), prodr1.remove(p));
		}

		System.out.println(prodr1_2);

		
		HashSet<String> prodList = new HashSet<String>();
		
		HashMap<Character, HashSet<String>> clone = new HashMap<Character, HashSet<String>>();
	    for (Map.Entry<Character, HashSet<String>> entry : this.productions.entrySet()){
	    	clone.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
	    }
		
		boolean empty = false;
		if(clone.get(this.s).toString().contains("&")) {
			empty = true;
			clone.get(this.s).remove("&");
		}
		
		for (char p : clone.keySet()) {
			prodList = new HashSet<String>();
			for(String s : clone.get(p)) {
				String s2;
				if(s.length() == 1) {
					s2 = s + this.s;
					prodList.add(s2);
				}
				prodList.add(s);
			}
			clone.replace(p, prodList);
		}

		if(empty) {
			prodList = new HashSet<String>();
			prodList.add("&");
			for(String s : clone.get(this.s)) {
				prodList.add(s);
			}
			clone.put(this.s, prodList);
		}
		
		
		System.out.println(clone);


		
		RegularGrammar rg = isValidRG(mapToInput(clone, this.s));
		
		
		return rg;
	}
	
	public RegularGrammar closureKleene() {
		
		HashMap<Character, HashSet<String>> cloneClosurePlus = new HashMap<Character, HashSet<String>>();
	    for (Map.Entry<Character, HashSet<String>> entry : this.closurePlus().productions.entrySet()){
	    	cloneClosurePlus.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
	    }
	    
	    System.out.println(cloneClosurePlus);
	    System.out.println(this.s);

	    
		char initial_r1 = 0;
		
		HashMap<Character, Character> vn_r1 = new HashMap<Character, Character>();

		HashMap<Character, HashSet<String>> prodr1 = new HashMap<Character, HashSet<String>>();
	    
		for (Map.Entry<Character, HashSet<String>> entry : cloneClosurePlus.entrySet()){
			prodr1.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
	    }
		
		HashMap<Character, HashSet<String>> prodr1_2 = new HashMap<Character, HashSet<String>>();
		
		HashMap<Character, HashSet<String>> copy = new HashMap<Character, HashSet<String>>();
	    for (Map.Entry<Character, HashSet<String>> entry : cloneClosurePlus.entrySet()){
	        copy.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
	    }
		

		char alphabet = 'B';

		for (char p : this.vn) {
			if(p == this.s)
				initial_r1 = alphabet;
			for(char p2 : this.vn) {
				for(String s : copy.get(p2)) {
					if(s.indexOf(p) >= 0) {
						prodr1.get(p2).remove(s);
						prodr1.get(p2).add(s.replace(p, alphabet));

					}
				}
			}
			vn_r1.put(p, alphabet);
			alphabet++;
		}

		for(char p : vn_r1.keySet()) {
			prodr1_2.put(vn_r1.get(p), prodr1.remove(p));
		}
		
		System.out.println(prodr1_2);

	    
		HashSet<String> prodList = new HashSet<String>();
		prodList.add("&");
		for(String s : prodr1_2.get(vn_r1.get(this.s))) {
			prodList.add(s);
		}
		prodr1_2.put('A', prodList);
		
		System.out.println(prodr1_2);

		
		//RegularGrammar rg = isValidRG(mapToInput(clone, this.s));
		
		//return rg;
	    return null;
	}



}

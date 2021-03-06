package RegularLanguages;

import java.awt.AlphaComposite;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.regex.Pattern;

import RegularLanguages.RegularLanguage.InputType;

public class RegularGrammar extends RegularLanguage{
	
	private HashSet<String> vn; // Non-terminal
	private HashSet<Character> vt; // Terminal
	private HashMap<String, HashSet<String>> productions;
	private String s; // Initial
	private boolean acceptsEmpty;

	public RegularGrammar(String input) {
		super(input, InputType.RG);
		s = "";
		acceptsEmpty = false;
		vn = new HashSet<String>();
		vt = new HashSet<Character>();
		productions = new HashMap<String, HashSet<String>>();
	}
	
	// Lexical validation of symbols
	private static boolean lexicalValidation(String inp) {
		String formatted = inp.replaceAll("\\s+", "");
		if(!formatted.matches("^[a-zA-Z0-9\\->|&']+"))
			return false;
		return true;
	}
	
	// Check if Regular Grammar is valid
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

	// Validate productions
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
					// If the symbols after the Non-Terminal is not ' it should fail
					if(!(vnProd[0].matches(".[\\']+")))
						return null;
				}
				vn = vnProd[0];
				if(vn.length() == 0) {
					rg.vn = new HashSet<String>();
					return null;
				}
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

	// Verify if production obey rules
	private static boolean verifyProduction(String vn2, String production, HashSet<String> prodList, RegularGrammar rg) {
		String[] prods = production.split("\\|");
		String prod;
		String second_to_end;
		if (production.length() < 1) {
			return false;
		}
		if(vn2.equals(rg.s)) {
			for(String pr : prods) {
				if(pr.length() == 0) {
					return false;
				}
				if(pr.charAt(0) == '&')
					rg.acceptsEmpty = true;
			}
		}
		int i = 0;
		for(String p : prods) {
			prod = prods[i++];
			if(prod.length() == 0) {
				return false;
			}
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
			if(prod.length() >= 2) {
				Character second = prod.charAt(1);
				second_to_end = prod.substring(1);
				if(Character.isUpperCase(first)) {
					return false;
				}
				else if(Character.isLowerCase(second) || Character.isDigit(second) || first == '&' || second == '&') {
					return false;
				}
				if(rg.s.contains(second_to_end) && rg.acceptsEmpty) {
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
					//else if(rg.productions.values().stream().anyMatch(list -> list.stream().anyMatch(pr -> pr.length() > 1 && pr == vn2))) {
					//	return false;
					//}
				}
				prodList.add(prod);
				rg.productions.put(vn2, prodList);
			}
		}
		return true;
	}

	// Return non-terminals
	public HashSet<String> getVn() {
		return vn;
	}
	
	// Return terminals
	public Set<Character> getVt() {
		return vt;
	}
	
	// Return initial
	public String getInitial() {
		return s;
	}
	
	// Return productions
	public HashMap<String, HashSet<String>> getProductions() {
		return productions;
	}
	
	// Return specific productions
	public Set<String> getProductions(String c) {
		Set<String> prod = productions.get(c);
		if(prod == null) {
			prod = new HashSet<String>();
		}
		return prod;
	}

	// Put Regular Grammar in printable format
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

	// Return Regular Grammar
	@Override
	public RegularGrammar getRG() {
		return this;
	}

	// Return Regular Expression
	@Override
	public RegularExpression getRE() {
		return null;
	}

	// Return if is deterministic
	private boolean isDeterministic() {
		int n = 0;
		for(String s : this.vn) {
			for(Character s2 : this.vt) {
				for(String prod : this.getProductions(s)) {
					if(prod.length() == 1)
						if(prod.equals(s2))
							n++;
					else {
						if(prod.charAt(0) == s2)
							n++;
					}
				}
				if(n >= 2)
					return true;
				else
					n = 0;
			}
		}
		return false;
	}
		
	// Transform Finite Automata to Regular Grammar
	public static RegularGrammar faToRG(FiniteAutomata fa) {
		boolean finalState = true;
		ArrayList<State> finals = new ArrayList<State>();
		for(State state : fa.getStates()) {
			finalState = true;
			for(Character c : fa.getAlphabet()) {
				for(State p : fa.getTransitions().get(state).get(c)) {
					if(p.getName() != "$")
						finalState = false;
				}
			}
			if(finalState && state.isFinal)				
				finals.add(state);
			
		}
		
		HashMap<String, HashSet<String>> new_productions = new HashMap<String, HashSet<String>>();
		HashSet<String> prod_value = new HashSet<String>();
		String new_state_name = "";
		boolean notInFinal = true;
		for(State state : fa.getStates()) {
			notInFinal = true;
			prod_value = new HashSet<String>();
			for(Character c : fa.getAlphabet()) {
				notInFinal = true;
				for(State p : fa.getTransitions().get(state).get(c)) {
					new_state_name = "";
					notInFinal = true;
					for(State s : finals) {
						if(s.getName().toString().equals(p.getName().toString()))
							notInFinal=false;
					}
					if((notInFinal) && p.getName() != "$"){
						new_state_name = c + p.getName();
						prod_value.add(new_state_name);
						if(p.isFinal) {
							new_state_name = Character.toString(c);
							prod_value.add(new_state_name);
						}

					}
					else if(p.getName() != "$"){
						new_state_name = Character.toString(c);
						prod_value.add(new_state_name);
					}
				}
			}
			notInFinal = true;
			for(State s : finals) {
				if(s.getName().toString().equals(state.getName().toString()))
					notInFinal=false;
			}
			if(notInFinal) {
				new_productions.put(state.getName(), prod_value);
			}
		}
		RegularGrammar new_rg = isValidRG(mapToInput(new_productions, fa.getInitial().getName()));		
		
		return new_rg;
	}
	
	// Return Finite Automata
	@Override
	public FiniteAutomata getFA() {
		FiniteAutomata fa = FiniteAutomata.rgToFA(this);
		return fa;
	}
	
	// Transform HashMap to an Input form to build  Regular Grammar
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
	
	// Create a deep clone of a hash map
	private static HashMap<String, HashSet<String>> deepCloneHashMap(HashMap<String, HashSet<String>> productions2) {
		HashMap<String, HashSet<String>> deepClone = new HashMap<String, HashSet<String>>();
		   
		for (Map.Entry<String, HashSet<String>> entry : productions2.entrySet()){
	        deepClone.put(entry.getKey(),
	           new HashSet<String>(entry.getValue()));
	    }
		
		return deepClone;

	}
	
	// Rename the states
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
	
	// Get the last letter from renamed states
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
	
	// Union of Regular Grammars
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
	
	// Concatenation of Regular Grammars
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
	
	// Closure Plus of Regular Grammar
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
	
	// Closure Star of Regular Grammar
	public RegularGrammar closureStar() {
		
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

	// Transform to Finite Automata and Reverse
	@Override
	public FiniteAutomata reverse() {
		return this.getFA().reverse();
	}

	// Return the intersection
	@Override
	public FiniteAutomata intersection(RegularLanguage rl) {
		if(rl.getType() == InputType.FA) {
			FiniteAutomata fa = (FiniteAutomata) rl;
			return this.getFA().intersection(fa);
		}
		else if(rl.getType() == InputType.RG) {
			RegularGrammar rg = (RegularGrammar) rl;
			FiniteAutomata fa = rg.getFA();
			return this.getFA().intersection(fa);
		}
		else if(rl.getType() == InputType.RE) {
			RegularExpression re = (RegularExpression) rl;
			FiniteAutomata fa = re.getFA();
			return this.getFA().intersection(fa);

		}
		return null;
	}

	// Return the difference
	@Override
	public FiniteAutomata difference(RegularLanguage rl) {
		if(rl.getType() == InputType.FA) {
			FiniteAutomata fa = (FiniteAutomata) rl;
			return this.getFA().difference(fa);
		}
		else if(rl.getType() == InputType.RG) {
			RegularGrammar rg = (RegularGrammar) rl;
			FiniteAutomata fa = rg.getFA();
			return this.getFA().difference(fa);
		}
		else if(rl.getType() == InputType.RE) {
			RegularExpression re = (RegularExpression) rl;
			FiniteAutomata fa = re.getFA();
			return this.getFA().difference(fa);

		}
		
		return null;
	}



}

package RegularLanguages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class FiniteAutomata extends RegularLanguage{

	private SortedSet<State> states;
	private HashMap<State, HashMap<Character, ArrayList<State>>> transitions;
	private State initialState;
	private State errorState;
	private SortedSet<Character> alphabet;
	private SortedSet<String> enumN;
	private boolean deterministic;
	
	// Constructor
	public FiniteAutomata(SortedSet<Character> _alphabet) {
		super(InputType.FA);
		alphabet = _alphabet;
		transitions = new HashMap<State, HashMap<Character, ArrayList<State>>>();
		errorState = new State("$", false, -1);
		states = new TreeSet<State>();
		deterministic = true;
	}
	
	// Constructor
	public FiniteAutomata(SortedSet<Character> _alphabet, SortedSet<State> _states, 
			HashMap<State, HashMap<Character, ArrayList<State>>> _transitions, State _initialState) {
		super(InputType.FA);
		alphabet = _alphabet;
		transitions = _transitions;
		errorState = new State("$", false, -1);
		states = _states;
		initialState = _initialState;
		deterministic = checkDeterminism();
	}

	@Override
	public String getDefinition() {
		String def = new String();
		def += " ";
		
		Iterator<Character> itSymbol = alphabet.iterator();
		while(itSymbol.hasNext()) {
			def += " |" + itSymbol.next(); 
		}
		def += "\n";
		
		Iterator<State> itStates = states.iterator();
		while(itStates.hasNext()) {
			itSymbol = alphabet.iterator();
			State st = itStates.next();
			def+= st.name + "|";
			HashMap<Character, ArrayList<State>> stateTransition = transitions.get(st);
			while(itSymbol.hasNext()) {
				char c = itSymbol.next();
				ArrayList<State> tList = stateTransition.get(c);
				for(int i = 0; i < tList.size(); i++) {
					def += " " + tList.get(i).name; 
				}
				def += "|";
			}
			if(st.isFinal) {
				def += "*";
			}
			def += "\n";
		}
		
		return def;
	}

	// Return equivalent in Regular Grammar
	@Override
	public RegularGrammar getRG() {
		RegularGrammar rg = RegularGrammar.faToRG(this);
		return rg;
	}

	// NOT REQUIRED
	@Override
	public RegularExpression getRE() {
		return null;
	}

	// Return Finite Automata
	@Override
	public FiniteAutomata getFA() {
		return this;
	}
	
	// Add a Initial State
	public void addInitialState(State ini) {
		initialState = ini;
		addState(ini);
	}
	
	// Return Initial State
	public State getInitial() {
		return initialState;
	}
	
	// Return the Transitions
	public HashMap<State, HashMap<Character, ArrayList<State>>> getTransitions(){
		return transitions;
	}
	
	// Return alphabet
	public SortedSet<Character> getAlphabet() {
		return alphabet;
	}
	
	// Return States
	public SortedSet<State> getStates(){
		return states;
	}
	
	// Return if is Deterministic
	public boolean isDeterministic() {
		return deterministic;
	}
	
	// Add State
	public void addState(State state) {
		states.add(state);
		HashMap<Character, ArrayList<State>> _transitions = new HashMap<Character, ArrayList<State>>();
		ArrayList<State> t;
		
		Iterator<Character> it = alphabet.iterator();
		while(it.hasNext()) {
			char c = it.next();
			t = new ArrayList<State>();
			t.add(errorState);
			_transitions.put(c, t);
		}
		transitions.put(state, _transitions);
	}
	
	// Add Transition
	public boolean addTransition(State out, char symbol, State in) {
		if (!(states.contains(out) || states.contains(in))) {
			return false;
		}

		HashMap<Character, ArrayList<State>> transition = transitions.get(out);
		ArrayList<State> t = transition.get(symbol);
		if(t.get(0).name == "$") {
			t.remove(0);
		}

		t.add(in);
		if (t.size() > 1) {
			deterministic = false;
		}
		t.sort(null);
		
		transition.put(symbol, t);
		return true;
	}
	
	// Check if sentence belongs to Finite Automata
	public boolean checkSentence(String str) {
		if(deterministic) {
			str = str.replaceAll("&+", "&");
			State current = initialState;
			if(str.equals("&") || str.equals("")) {
				return initialState.isFinal;
			}
			if(!str.matches("[a-z0-9\\&]+")) {
				return false;
			}
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (!alphabet.contains(c)) {
					return false;
				}
				HashMap<Character, ArrayList<State>> stateTransitions = transitions.get(current);
				current = stateTransitions.get(c).get(0);
				if (current == errorState) {
					return false;
				}
			}
			return current.isFinal;
		} else {
			FADeterminize det = new FADeterminize();
			FiniteAutomata tmp = det.determinizeAutomata(this);
			return tmp.checkSentence(str);
		}
	}
	
	// Return enumeration
	public SortedSet<String> getEnumeration(int n){
		enumN = new TreeSet<String>();
		possibleStrings(n, "");
		return enumN;
	}
	
	public void possibleStrings(int maxLength, String curr) {
		if(curr.length() == maxLength) {
			if(checkSentence(curr)) {
				enumN.add(curr);
			}
			return;
		}
		Iterator<Character> symbols = alphabet.iterator();
		while(symbols.hasNext()) {
			char c = symbols.next();
			possibleStrings(maxLength, curr+c);
		}
	}

	// Convert Regular Grammar to Finite Automata
	public static FiniteAutomata rgToFA(RegularGrammar regularGrammar) {
		HashMap<String, State> stringState = new HashMap<String, State>();
		SortedSet<Character> _alphabet = new TreeSet<Character>();
		for(char c : regularGrammar.getVt()) {
			_alphabet.add(c);
		}		
		FiniteAutomata fa = new FiniteAutomata(_alphabet);
		int i = 0;
		boolean _isFinal;
		String sInitial = regularGrammar.getInitial();
		_isFinal = false;
		stringState.put(sInitial, new State(sInitial, _isFinal, i++));
		fa.addInitialState(stringState.get(sInitial));
		for(String s : regularGrammar.getVn()) {
			_isFinal = false;
			if(s.toString().equals(regularGrammar.getInitial().toString()))
				continue;
			else {
				stringState.put(s, new State(s, _isFinal, i++));
				fa.addState(stringState.get(s));
			}
		}
		
		State Final = new State("Z'''''", true, i);
		fa.addState(Final);
		
		for(String s : regularGrammar.getProductions().keySet()) {
			for(String s2 : regularGrammar.getProductions(s)) {
				if(s2.length() > 1)
					fa.addTransition(stringState.get(s), s2.charAt(0), stringState.get(s2.substring(1)));
				else
					fa.addTransition(stringState.get(s), s2.charAt(0), Final);
			}
		}

		return fa;
		
	}
	
	// Return reversed automata
	public FiniteAutomata reverse() {
		FiniteAutomata new_fa = new FiniteAutomata(this.alphabet);
		HashMap<String, State> stringState = new HashMap<String, State>();
		
		boolean initialFinal = false;
		if(this.getInitial().isFinal)
			initialFinal = true;
		int i = 0;
		State new_initial = new State("S''''", initialFinal, i++);
		stringState.put("S''''", new_initial);
		new_fa.addInitialState(new_initial);	
				
		for(State s: this.getStates()) {
			if(this.getInitial().getName().toString().equals(s.getName().toString()))
				stringState.put(s.getName().toString(), new State(s.getName().toString(), true, i++));
			else
				stringState.put(s.getName().toString(), new State(s.getName().toString(), !s.isFinal, i++));
			new_fa.addState(stringState.get(s.getName().toString()));
		}

			
		for(State state : this.getStates()) {
			for(char c : this.getAlphabet()) {
				for(State p : this.getTransitions().get(state).get(c)) {
					if(!p.name.toString().contains("$")) {
						new_fa.addTransition(stringState.get(p.getName()), c, stringState.get(state.getName()));
					}
					if(p.isFinal)
						new_fa.addTransition(stringState.get("S''''"), c, stringState.get(state.getName()));
				}
			}
		}
		
		return new_fa;
	}
	
	// Check if is deterministic
	public boolean checkDeterminism() {
		Iterator<State> itSt = states.iterator();
		while(itSt.hasNext()) {
			State current = itSt.next();
			HashMap<Character, ArrayList<State>> stateTrans = transitions.get(current);
			Iterator<Character> it = alphabet.iterator();
			while(it.hasNext()) {
				char c = it.next();
				ArrayList<State> temp = stateTrans.get(c);
				if(temp.size() > 1) {
					return false;
				}
			}
		}
		return true;
	}
	
	// Return complement of finite automata
	public FiniteAutomata complement() {
		FiniteAutomata new_fa = new FiniteAutomata(this.getAlphabet());
		if(!checkDeterminism()) {
			FADeterminize determinize = new FADeterminize();
			return determinize.determinizeAutomata(this).complement();
		}
		HashMap<String, State> stringState = new HashMap<String, State>();

		State qErro = new State("E''''", true, 999);
		stringState.put(this.initialState.getName(), qErro);
		new_fa.addState(qErro);
		for(char c : this.alphabet) {
			new_fa.addTransition(qErro, c, qErro);
		}
		
		
		int i = 1;
		State new_initial = new State(this.initialState.getName(), !this.initialState.isFinal, i++);
		stringState.put(this.initialState.getName(), new_initial);
		new_fa.addInitialState(new_initial);	
				
		for(State s: this.getStates()) {
			if(s != this.getInitial()) {
				stringState.put(s.getName().toString(), new State(s.getName().toString(), !s.isFinal, i++));
				new_fa.addState(stringState.get(s.getName().toString()));
			}
		}

			
		for(State state : this.getStates()) {
			for(char c : this.getAlphabet()) {
				for(State p : this.getTransitions().get(state).get(c)) {
					if(!p.name.toString().contains("$")) {
						new_fa.addTransition(stringState.get(state.getName()), c, stringState.get(p.getName()));
					}
					else {
						new_fa.addTransition(stringState.get(state.getName()), c, qErro);
					}
				}
			}
		}

		return new_fa;
	}
	
	// Return the intersections of two finite automatas
	public FiniteAutomata intersection(FiniteAutomata fa) {
		FiniteAutomata r_this = this.complement();
		FiniteAutomata r_fa = fa.complement();
		FiniteAutomata union = r_this.union(r_fa);
		FiniteAutomata new_fa = union.complement();
		return new_fa;
	}
	
	// Return the difference between two finite automatas
	public FiniteAutomata difference(FiniteAutomata fa) {
		FiniteAutomata r_fa = fa.complement();
		FiniteAutomata new_fa = this.intersection(r_fa);
		return new_fa;
	}
	
	// Return the union of two automatas
	public FiniteAutomata union(FiniteAutomata fa) {
		SortedSet<Character> newAlphabet = new TreeSet<Character>();
		for(char c : this.getAlphabet()) {
			newAlphabet.add(c);
		}
		for(char c : fa.getAlphabet()) {
			newAlphabet.add(c);
		}
		FiniteAutomata new_fa = new FiniteAutomata(newAlphabet);

		HashMap<String, State> stringState = new HashMap<String, State>();
		int i = 1;

		char alphabet = 'A';
		int endAlphabet = 0;		
		String new_symbol;
		
		String pState;
		for(State s : this.getStates()) {
			pState = s.getName().toString();
			if(endAlphabet == 0) {
				stringState.put(pState, new State(Character.toString(alphabet), s.isFinal, i++));
			}
			else {
				new_symbol = Character.toString(alphabet);
				for(int e = 0; e< endAlphabet; e++) {
					new_symbol = new_symbol + "'";
				}
				stringState.put(pState, new State(new_symbol, s.isFinal, i++));
			}
			if(alphabet == 'Z') {
				alphabet = 'A';
				endAlphabet++;
			}
			else {
				alphabet++;
			}
		}
		
		for(State s: this.getStates()) {
			new_fa.addState(stringState.get(s.getName().toString()));
		}
				
		for(State state : this.getStates()) {
			for(char c : this.getAlphabet()) {
				for(State p : this.getTransitions().get(state).get(c)) {
					if(!p.name.toString().contains("$")) {
						new_fa.addTransition(stringState.get(state.getName()), c, stringState.get(p.getName()));
					}
				}
			}
		}
		
		State new_initial;
		if(this.getInitial().isFinal && fa.getInitial().isFinal) {
			new_initial = new State("S''''", true, 0);
		}
		else {
			new_initial = new State("S''''", false, 0);
		}
		stringState.put("S''''", new_initial);
		new_fa.addInitialState(new_initial);
				
		for(char c : this.getAlphabet()) {
			for(State p : this.getTransitions().get(this.initialState).get(c)) {
				if(!p.name.toString().contains("$")) {
					new_fa.addTransition(new_initial, c, stringState.get(p.getName()));
				}
			}
		}
		
		stringState = new HashMap<String, State>();
		for(State s : fa.getStates()) {
			pState = s.getName().toString();
			if(endAlphabet == 0) {
				stringState.put(pState, new State(Character.toString(alphabet), s.isFinal, i++));
			}
			else {
				new_symbol = Character.toString(alphabet);
				for(int e = 0; e< endAlphabet; e++) {
					new_symbol = new_symbol + "'";
				}
				stringState.put(pState, new State(new_symbol, s.isFinal, i++));
			}
			if(alphabet == 'Z') {
				alphabet = 'A';
				endAlphabet++;
			}
			else {
				alphabet++;
			}
		}
		
		for(State s: fa.getStates()) {
			new_fa.addState(stringState.get(s.getName().toString()));
		}
				
		for(State state : fa.getStates()) {
			for(char c : fa.getAlphabet()) {
				for(State p : fa.getTransitions().get(state).get(c)) {
					if(!p.name.toString().contains("$")) {
						new_fa.addTransition(stringState.get(state.getName()), c, stringState.get(p.getName()));
					}
				}
			}
		}
		
		for(char c : fa.getAlphabet()) {
			for(State p : fa.getTransitions().get(fa.initialState).get(c)) {
				if(!p.name.toString().contains("$")) {
					new_fa.addTransition(new_initial, c, stringState.get(p.getName()));
				}
			}
		}

		return new_fa;
	}

	// Return intersection between two regular languages
	@Override
	public FiniteAutomata intersection(RegularLanguage rl) {
		if(rl.getType() == InputType.FA) {
			FiniteAutomata fa = (FiniteAutomata) rl;
			return this.intersection(fa);
		}
		else if(rl.getType() == InputType.RG) {
			RegularGrammar rg = (RegularGrammar) rl;
			FiniteAutomata fa = rg.getFA();
			return this.intersection(fa);
		}
		else if(rl.getType() == InputType.RE) {
			RegularExpression re = (RegularExpression) rl;
			FiniteAutomata fa = re.getFA();
			return this.intersection(fa);

		}
		
		return null;
	}

	// Return difference between two regular languages
	@Override
	public FiniteAutomata difference(RegularLanguage rl) {
		if(rl.getType() == InputType.FA) {
			FiniteAutomata fa = (FiniteAutomata) rl;
			return this.difference(fa);
		}
		else if(rl.getType() == InputType.RG) {
			RegularGrammar rg = (RegularGrammar) rl;
			FiniteAutomata fa = rg.getFA();
			return this.difference(fa);
		}
		else if(rl.getType() == InputType.RE) {
			RegularExpression re = (RegularExpression) rl;
			FiniteAutomata fa = re.getFA();
			return this.difference(fa);

		}
		
		return null;
	}
	
}


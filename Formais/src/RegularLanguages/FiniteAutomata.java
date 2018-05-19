package RegularLanguages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class FiniteAutomata extends RegularLanguage{

	private SortedSet<State> states;
	private HashMap<State, HashMap<Character, State>> transitions;
	private State initialState;
	private State errorState;
	private SortedSet<Character> alphabet;
	private SortedSet<String> enumN;
	
	public FiniteAutomata(SortedSet<Character> _alphabet) {
		super(InputType.FA);
		alphabet = _alphabet;
		transitions = new HashMap<State, HashMap<Character, State>>();
		errorState = new State("$", false, -1);
		states = new TreeSet<State>();
		
	}
	
	public FiniteAutomata(SortedSet<Character> _alphabet, SortedSet<State> _states, HashMap<State, 
			HashMap<Character, State>> _transitions, State _initialState) {
		super(InputType.FA);
		alphabet = _alphabet;
		transitions = _transitions;
		errorState = new State("$", false, -1);
		states = _states;
		initialState = _initialState;
		
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
			HashMap<Character, State> stateTransition = transitions.get(st);
			while(itSymbol.hasNext()) {
				def += stateTransition.get(itSymbol.next()).name + "|"; 
			}
			if(st.isFinal) {
				def += "*";
			}
			def += "\n";
		}
		
		return def;
	}

	@Override
	public RegularGrammar getRG() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RegularExpression getRE() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FiniteAutomata getFA() {
		// TODO Auto-generated method stub
		return this;
	}
	
	public void addInitialState(State ini) {
		initialState = ini;
		addState(ini);
	}
	
	public State getInitial() {
		return initialState;
	}
	
	public HashMap<State, HashMap<Character, State>> getTransitions(){
		return transitions;
	}
	
	public SortedSet<Character> getAlphabet() {
		return alphabet;
	}
	
	public SortedSet<State> getStates(){
		return states;
	}
	
	public void addState(State state) {
		states.add(state);
		HashMap<Character, State> _transitions = new HashMap<Character, State>();
		Iterator<Character> it = alphabet.iterator();
		while(it.hasNext()) {
			char c = it.next();
			_transitions.put(c, errorState);
		}
		transitions.put(state, _transitions);
	}
	
	public boolean addTransition(State out, char symbol, State in) {
		if (!(states.contains(out) || states.contains(in))) {
			return false;
		}
		HashMap<Character, State> transition = transitions.get(out);
		transition.put(symbol, in);
		return true;
	}
	
	public boolean checkSentence(String str) {
		if(!str.matches("[a-z0-9\\&]+")) {
			return false;
		}
		State current = initialState;
		if(str.equals("&")) {
			return initialState.isFinal;
		}
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (!alphabet.contains(c)) {
				return false;
			}
			HashMap<Character, State> stateTransitions = transitions.get(current);
			current = stateTransitions.get(c);
			if (current == errorState) {
				return false;
			}
		}
		return current.isFinal;
	}
	
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
}


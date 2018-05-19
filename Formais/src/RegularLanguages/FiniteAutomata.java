package RegularLanguages;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	public FiniteAutomata(SortedSet<Character> _alphabet) {
		super(InputType.FA);
		alphabet = _alphabet;
		transitions = new HashMap<State, HashMap<Character, ArrayList<State>>>();
		errorState = new State("$", false, -1);
		states = new TreeSet<State>();
		
	}
	
	public FiniteAutomata(SortedSet<Character> _alphabet, SortedSet<State> _states, 
			HashMap<State, HashMap<Character, ArrayList<State>>> _transitions, State _initialState) {
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
	
	public HashMap<State, HashMap<Character, ArrayList<State>>> getTransitions(){
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
		
		transition.put(symbol, t);
		/*transitions.remove(out);
		transitions.put(out, transition);*/
		return true;
	}
	
	public boolean checkSentence(String str) {
		/*if(!str.matches("[a-z0-9\\&]+")) {
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
		return current.isFinal;*/
		return true;
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


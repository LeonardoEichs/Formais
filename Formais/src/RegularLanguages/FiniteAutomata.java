package RegularLanguages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

public class FiniteAutomata extends RegularLanguage{

	private SortedSet<State> states;
	private HashMap<State, HashMap<Character, State>> transitions;
	private State initialState;
	private State errorState;
	private SortedSet<Character> alphabet; 
	
	public FiniteAutomata(InputType type, SortedSet<Character> _alphabet) {
		super(type);
		alphabet = _alphabet;
		transitions = new HashMap<State, HashMap<Character, State>>();
		errorState = new State("$", false);
		states = new TreeSet<State>();
		
	}

	@Override
	public String getDefinition() {
		String def = new String();
		def += " ";
		
		Iterator<Character> itSymbol = alphabet.iterator();
		while(itSymbol.hasNext()) {
			def += "|" + itSymbol.next(); 
		}
		def += "\n";
		
		Iterator<State> itStates = states.iterator();
		Iterator<HashMap<Character, State>> itTransition;
		while(itStates.hasNext()) {
			itSymbol = alphabet.iterator();
			State st = itStates.next();
			def+= st.name + "|";
			HashMap<Character, State> stateTransition = transitions.get(st);
			while(itSymbol.hasNext()) {
				def += stateTransition.get(itSymbol.next()).name + "|"; 
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
		//if (initialState == null) {
		//	initialState = ini;
		//} else {
			//states.add(initialState);
			initialState = ini;
			addState(ini);
		//}
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
}


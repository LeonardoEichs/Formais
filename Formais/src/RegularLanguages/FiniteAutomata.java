package RegularLanguages;

import java.util.Iterator;
import java.util.SortedSet;

public class FiniteAutomata extends RegularLanguage{

	private SortedSet<State> states;
	private State initialState;
	private SortedSet<Character> alphabet; 
	
	public FiniteAutomata(String input, InputType type, SortedSet<Character> _alphabet) {
		super(input, type);
		alphabet = _alphabet;
	}
	
	public FiniteAutomata(InputType type, SortedSet<Character> _alphabet) {
		super(type);
		alphabet = _alphabet;
	}

	@Override
	public String getDefinition() {
		String def = new String();
		def += " ";
		
		Iterator<Character> it = alphabet.iterator();
		while(it.hasNext()) {
			def += " | " + it.next(); 
		}
		def += "\n";
		def+= initialState.name + " |";
		Iterator<String> it2 = initialState.transitions.iterator();
		while(it2.hasNext()) {
			def += " | " + it2.next(); 
		}
		
		def += "\n";
		
		Iterator<State> it3 = states.iterator();
		while(it3.hasNext()) {
			State st = it3.next();
			def+= st.name + " |";
			it2 = st.transitions.iterator();
			while(it2.hasNext()) {
				def += " | " + it2.next(); 
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
		if (initialState == null) {
			initialState = ini;
		} else {
			states.add(initialState);
			initialState = ini;
		}
	}
	
	public void addState(State state) {
		states.add(state);
	}

}

class State {
	String name;
	boolean isFinal;
	SortedSet<String> transitions;
	
	public State(String _name, boolean _isFinal, SortedSet<String> _transitions) {
		name = _name;
		isFinal = _isFinal;
		transitions = _transitions;
	}
}

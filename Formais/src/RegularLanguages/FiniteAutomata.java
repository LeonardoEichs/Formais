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
	
	public FiniteAutomata(SortedSet<Character> _alphabet) {
		super(InputType.FA);
		alphabet = _alphabet;
		transitions = new HashMap<State, HashMap<Character, ArrayList<State>>>();
		errorState = new State("$", false, -1);
		states = new TreeSet<State>();
		deterministic = true;
	}
	
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

	@Override
	public RegularGrammar getRG() {
		RegularGrammar rg = RegularGrammar.faToRG(this);
		return rg;
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
	
	public boolean isDeterministic() {
		return deterministic;
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
		if (t.size() > 1) {
			deterministic = false;
		}
		t.sort(null);
		
		transition.put(symbol, t);
		/*transitions.remove(out);
		transitions.put(out, transition);*/
		return true;
	}
	
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

	public static FiniteAutomata rgToFA(RegularGrammar regularGrammar) {
		HashMap<String, State> stringState = new HashMap<String, State>();
		SortedSet<Character> _alphabet = new TreeSet<Character>();
		for(char c : regularGrammar.getVt()) {
			_alphabet.add(c);
		}
		
		FiniteAutomata fa = new FiniteAutomata(_alphabet);
		int i = 0;
		boolean _isFinal;
		for(String s : regularGrammar.getVn()) {
			_isFinal = false;
			//for(String s2: regularGrammar.getProductions(s)) {
			//	if(s2.length() == 1)
			//		_isFinal = true;
			//}
			stringState.put(s, new State(s, _isFinal, i++));
			if(s.toString().equals(regularGrammar.getInitial().toString()))
				fa.addInitialState(stringState.get(s));
			else {
				fa.addState(stringState.get(s));
			}
		}
		
		State Final = new State("Final", true, i);
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
	
	public FiniteAutomata reverse() {
		FiniteAutomata new_fa = new FiniteAutomata(this.alphabet);
		HashMap<String, State> stringState = new HashMap<String, State>();
		
		boolean initialFinal = false;
		if(this.getInitial().isFinal)
			initialFinal = true;
		int i = 0;
		State new_initial = new State("Initial", initialFinal, i++);
		stringState.put("Initial", new_initial);
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
						new_fa.addTransition(stringState.get("Initial"), c, stringState.get(state.getName()));
				}
			}
		}
		
		System.out.println(new_fa.getDefinition());
		return new_fa;
	}
		
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
}


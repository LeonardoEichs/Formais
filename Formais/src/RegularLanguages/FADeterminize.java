package RegularLanguages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;

public class FADeterminize {
	
	public FADeterminize() {
		
	}
	
	public FiniteAutomata determinizeAutomata(FiniteAutomata fa) {
		HashMap<State, HashMap<Character, ArrayList<State>>> transitions = fa.getTransitions();
		SortedSet<Character> alphabet = fa.getAlphabet();
		
		FiniteAutomata newFA = new FiniteAutomata(alphabet);
		ArrayList<State> newStates = new ArrayList<State>();
		
		ArrayList<CompositeState> table = new ArrayList<CompositeState>();
		ArrayList<State> initialGroup = new ArrayList<State>();
		State initial = fa.getInitial();
		HashMap<Character, ArrayList<State>> stateTransition = transitions.get(initial);
		State current = new State("q0", initial.isFinal, 0);
		initialGroup.add(initial);
		table.add(new CompositeState(current, initialGroup));
		newFA.addInitialState(current);
		
		Iterator<Character> it = alphabet.iterator();
		int count = 1;
		while(it.hasNext()) {
			char c = it.next();
			ArrayList<State> symbolTransitions = stateTransition.get(c);
			boolean isFinal = false;
			for(int i = 0; i < symbolTransitions.size(); i++) {
				if (symbolTransitions.get(i).isFinal) {
					isFinal = true;
				}
			}
			State newState = null;
			for (int k = 0; k < table.size(); k++) {
				newState = table.get(k).get(symbolTransitions);
				if(newState != null) {
					break;
				}
			}
			if(newState == null) {
				newState = new State("q"+count, isFinal, count);
				table.add(new CompositeState(newState, symbolTransitions));
				newStates.add(newState);
				newFA.addState(newState);
				count++;
			}
			newFA.addTransition(current, c, newState);
		}
		
		for(int i = 0; i < newStates.size(); i++) {
			current = newStates.get(i);
			int p = 0;
			CompositeState temp = table.get(p);
			while(temp.state.name != current.name) {
				p++;
				temp = table.get(p);
			}
			
			it = alphabet.iterator();
			while(it.hasNext()) {
				char c = it.next();
				ArrayList<State> symbolTransitions = new ArrayList<State>();
				for(p = 0; p < temp.composition.size(); p++) {
					stateTransition = transitions.get(temp.composition.get(p));
					if(!(stateTransition.get(c).get(0).name == "$")) {
						ArrayList<State> tempTrans = stateTransition.get(c);
						for(int h = 0; h < tempTrans.size(); h++) {
							if(!checkGroup(tempTrans.get(h), symbolTransitions)) {
								symbolTransitions.add(tempTrans.get(h));
								symbolTransitions.sort(null);
							}
						}
					}
				}
				if (symbolTransitions.size() != 0) {
					boolean isFinal = false;
					for(int j = 0; j < symbolTransitions.size(); j++) {
						if (symbolTransitions.get(j).isFinal) {
							isFinal = true;
						}
					}
					State newState = null;
					for (int k = 0; k < table.size(); k++) {
						newState = table.get(k).get(symbolTransitions);
						if(newState != null) {
							break;
						}
					}
					if(newState == null) {
						newState = new State("q"+count, isFinal, count);
						table.add(new CompositeState(newState, symbolTransitions));
						newStates.add(newState);
						newFA.addState(newState);
						count++;
					}
					newFA.addTransition(current, c, newState);
				}
			}
		}
		return newFA;
	}
	
	public boolean checkGroup(State state, ArrayList<State> group) {
		for(int i = 0; i < group.size(); i++) {
			if(state.name.equals(group.get(i).name)) {
				return true;
			}
		}
		return false;
	}
}

class CompositeState {
	State state;
	ArrayList<State> composition;
	
	public CompositeState(State st, ArrayList<State> list) {
		state = st;
		composition = list;
	}
	
	public State get(ArrayList<State> list) {
		if(list.size() != composition.size()) {
			return null;
		}
		for(int i = 0; i < composition.size(); i++) {
			if(list.get(i) != composition.get(i)) {
				return null;
			}
		}
		return this.state;
	}
}

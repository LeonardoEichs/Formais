package RegularLanguages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


public class FAMinimizer {
	
	public FAMinimizer() {
		
	}
	
	public FiniteAutomata minimize(FiniteAutomata fa) {
		HashMap<State, HashMap<Character, ArrayList<State>>> transitions = fa.getTransitions();
		SortedSet<Character> alphabet = fa.getAlphabet();
		SortedSet<State> states = fa.getStates();
		State initial = fa.getInitial();
		
		SortedSet<State> newStates = checkDeadStates(alphabet, states, transitions);
		transitions = removeTransitions(alphabet, newStates, transitions);
		newStates = checkReacheble(alphabet, transitions, initial);
		transitions = removeTransitions(alphabet, newStates, transitions);
		
		HashMap<State, ArrayList<State>> classes = checkEquivalence(alphabet, newStates, transitions, initial);
		FiniteAutomata minAF = buildAutomata(alphabet, classes, transitions, initial);
		
		return minAF;
	}
	
	public FiniteAutomata buildAutomata(SortedSet<Character> alphabet, HashMap<State, ArrayList<State>> classes, HashMap<State, HashMap<Character, ArrayList<State>>> transitions, State initial) {
		FiniteAutomata fa = new FiniteAutomata(alphabet);
		SortedSet<State> states = new TreeSet<State>();
		Set<State> keys = classes.keySet();
		Iterator<State> it = keys.iterator();
		while(it.hasNext()) {
			State current = it.next();
			if(current.name == "$") {
				continue;
			}
			states.add(current);
			fa.addState(current);
		}
		it = states.iterator();
		while(it.hasNext()) {
			State current = it.next();
			Iterator<Character> itS = alphabet.iterator();
			while(itS.hasNext()) {
				char c = itS.next();
				State currentTransition = transitions.get(current).get(c).get(0);
				Iterator<State> itC = states.iterator();
				while(itC.hasNext()) {
					State temp = itC.next();
					ArrayList<State> currentGroup = classes.get(temp);
					if(checkGroup(currentTransition, currentGroup)) {
						fa.addTransition(current, c, temp);
						break;
					}
					
				}
				
			}
			
		}
		
		return fa;
	}
	
	public HashMap<State, ArrayList<State>> checkEquivalence(SortedSet<Character> alphabet, SortedSet<State> states, HashMap<State, HashMap<Character, ArrayList<State>>> transitions, State initial) {
		
		ArrayList<State> f = new ArrayList<State>();
		ArrayList<State> k = new ArrayList<State>();
		
		HashMap<State, ArrayList<State>> classes = new HashMap<State, ArrayList<State>>();
		Iterator<State> it = states.iterator();
		while(it.hasNext()) {
			State current = it.next();
			if(current.isFinal) {
				f.add(current);
			} else {
				k.add(current);
			}
		}
		
		///// TODO tratar indefinições 
		State err = new State("$", false, -1);
		k.add(err);
		HashMap<Character, ArrayList<State>> errTransition = new HashMap<Character, ArrayList<State>>();
		Iterator<Character> itS = alphabet.iterator();
		while(itS.hasNext()) {
			ArrayList<State> t = new ArrayList<State>();
			t.add(err);
			errTransition.put(itS.next(), t);
		}
		transitions.put(err, errTransition);
		if (!f.isEmpty()) {
			State fq0 = f.get(0);
			classes.put(fq0, f);
		}
		if (!k.isEmpty()) {
			State kq0 = k.get(0);
			classes.put(kq0, k);
		}
		boolean ctrl = true;
		while(ctrl) {
			ctrl = false;
			Set<State> keys = classes.keySet();
			HashMap<State, ArrayList<State>> newClasses = new HashMap<State, ArrayList<State>>();
			it = keys.iterator();
			while(it.hasNext()) {
				State base = it.next();
				ArrayList<State> currentGroup = classes.get(base);
				ArrayList<State> newGroup = new ArrayList<State>();
				HashMap<State, ArrayList<State>> newGroups = new HashMap<State, ArrayList<State>>();
				newGroup.add(base);
				//newClasses.put(base, newGroup);
				newGroups.put(base, newGroup);
				for(int j = 0; j < currentGroup.size(); j++) {
					State current = currentGroup.get(j);
					if(base == current) {
						continue;
					}
					if(isEquivalent(base, current, transitions, classes)) {
						newGroup.add(current);
						newGroups.remove(base);
						newGroups.put(base, newGroup);
					} else {
						boolean found = false;
						Set<State> newKeys = newGroups.keySet();
						Iterator<State> it2 = newKeys.iterator();
						while(it2.hasNext()) {
							State otherBase = it2.next();
							if (base == otherBase) {
								continue;
							}
							if(isEquivalent(otherBase, current, transitions, classes)) {
								ArrayList<State> temp = newGroups.get(otherBase);
								temp.add(current);
								newGroups.remove(otherBase);
								newGroups.put(otherBase,temp);
								found = true;
								ctrl = true;
								break;
							}
						}
						if(!found) {
							ArrayList<State> temp = new ArrayList<State>();
							temp.add(current);
							newGroups.put(current, temp);
							ctrl = true;
						}
					}
				}
				newClasses.putAll(newGroups);
			}
			classes = newClasses;
		}
		return classes;
	}
	
	public boolean isEquivalent(State base, State current, HashMap<State, HashMap<Character, ArrayList<State>>> transitions, HashMap<State, ArrayList<State>> classes) {
		Set<Character> alphabet = transitions.get(base).keySet();
		HashMap<Character, ArrayList<State>> bTransitions = transitions.get(base);
		HashMap<Character, ArrayList<State>> cTransitions = transitions.get(current);
		Iterator<Character> it = alphabet.iterator();
		ArrayList<ArrayList<State>> classList = new ArrayList<ArrayList<State>>();
		classList.addAll(classes.values());
		while(it.hasNext()) {
			char c = it.next();
			State bState = bTransitions.get(c).get(0);
			State cState = cTransitions.get(c).get(0);
			for(int i = 0; i < classList.size(); i++) {
				ArrayList<State> temp = classList.get(i);
				if (checkGroup(bState, temp) && checkGroup(cState, temp)) {
					break;
				} else if((checkGroup(bState, temp) && !checkGroup(cState, temp)) || (!checkGroup(bState, temp) && checkGroup(cState, temp))) {
					return false;
				}
			}
			
		}
		return true;
	}
	
	public boolean checkGroup(State state, ArrayList<State> group) {
		for(int i = 0; i < group.size(); i++) {
			if(state.name.equals(group.get(i).name)) {
				return true;
			}
		}
		return false;
	}
	
	public SortedSet<State> checkReacheble(SortedSet<Character> alphabet, HashMap<State, HashMap<Character, ArrayList<State>>> transitions, State initial) {
		SortedSet<State> rchStates = new TreeSet<State>();
		State current = initial;
		rchStates.add(current);
		
		HashMap<Character, ArrayList<State>> currentTransitions = transitions.get(current);
		Iterator<Character> symbols = alphabet.iterator();
		List<State> pendentStates = new ArrayList<State>();
		while(symbols.hasNext()) {
			char c = symbols.next();
			State in = currentTransitions.get(c).get(0);
			if(in.name == "$") {
				continue;
			}
			if(!rchStates.contains(in)) {
				rchStates.add(in);
				pendentStates.add(in);
			}
		}
		
		for(int i = 0; i < pendentStates.size(); i++) {
			current = pendentStates.get(i);
			currentTransitions = transitions.get(current);
			symbols = alphabet.iterator();
			while(symbols.hasNext()) {
				char c = symbols.next();
				State in = currentTransitions.get(c).get(0);
				if(in.name == "$") {
					continue;
				}
				if(!rchStates.contains(in)) {
					rchStates.add(in);
					pendentStates.add(in);
				}
			}
		}
		return rchStates;
	}
	
	public SortedSet<State> checkDeadStates(SortedSet<Character> alphabet, SortedSet<State> states, HashMap<State, HashMap<Character, ArrayList<State>>> transitions ){
		SortedSet<State> alive = new TreeSet<State>();
		State current;
		
		Iterator<Character> symbols = alphabet.iterator();
		List<State> pendentStates = new ArrayList<State>();
		HashMap<Character, ArrayList<State>> currentTransitions;
		
		Iterator<State> it = states.iterator();
		while(it.hasNext()) {
			current = it.next();
			if(current.isFinal) {
				alive.add(current);
			} else {
				pendentStates.add(current);
			}
		}
		boolean ctrl = true;
		while(ctrl) {
			ctrl = false;
			for(int i = 0; i < pendentStates.size(); i++) {
				current = pendentStates.get(i);
				currentTransitions = transitions.get(current);
				symbols = alphabet.iterator();
				while(symbols.hasNext()) {
					char c = symbols.next();
					State in = currentTransitions.get(c).get(0);
					if (alive.contains(in)) {
						alive.add(current);
						pendentStates.remove(i);
						ctrl = true;
						break;
					}
				}
			}
		}
		return alive;
	}
	
	public HashMap<State, HashMap<Character, ArrayList<State>>> removeTransitions(SortedSet<Character> alphabet, SortedSet<State> states, HashMap<State,
		HashMap<Character, ArrayList<State>>> transitions) {	
		HashMap<State, HashMap<Character, ArrayList<State>>> newTransitions = new HashMap<State, HashMap<Character, ArrayList<State>>>();
		Iterator<State> it = states.iterator();
		while(it.hasNext()) {
			State current = it.next();
			HashMap<Character, ArrayList<State>> stateOldTransitions = transitions.get(current);
			HashMap<Character, ArrayList<State>> newStateTransitions = new HashMap<Character, ArrayList<State>>();
			Iterator<Character> symbols = alphabet.iterator();
			while(symbols.hasNext()) {
				char c = symbols.next();
				State nxtState = stateOldTransitions.get(c).get(0);
				ArrayList<State> t = new ArrayList<State>();
				if(states.contains(nxtState)){
					t.add(nxtState);
					newStateTransitions.put(c, t);
				} else {
					t.add(new State("$", false, -1));
					newStateTransitions.put(c, t);				
				}
			}
			newTransitions.put(current, newStateTransitions);
		}
		return newTransitions;
	}
}

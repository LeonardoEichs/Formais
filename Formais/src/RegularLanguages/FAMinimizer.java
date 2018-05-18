package RegularLanguages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import RegularLanguages.RegularLanguage.InputType;

public class FAMinimizer {
	
	public FAMinimizer() {
		
	}
	
	public FiniteAutomata minimize(FiniteAutomata fa) {
		HashMap<State, HashMap<Character, State>> transitions = fa.getTransitions();
		SortedSet<Character> alphabet = fa.getAlphabet();
		SortedSet<State> states = fa.getStates();
		State initial = fa.getInitial();
		
		SortedSet<State> newStates = checkDeadStates(alphabet, states, transitions);
		transitions = removeTransitions(alphabet, newStates, transitions);
		newStates = checkReacheble(alphabet, transitions, initial);
		transitions = removeTransitions(alphabet, newStates, transitions);
		
		FiniteAutomata minAF = checkEquivalence(alphabet, newStates, transitions, initial);
		
		
		return minAF;
	}
	
	public FiniteAutomata checkEquivalence(SortedSet<Character> alphabet, SortedSet<State> states, HashMap<State, HashMap<Character, State>> transitions, State initial) {
		
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
		
		State err = new State("$", false, -1);
		k.add(err);
		HashMap<Character, State> errTransition = transitions.get(initial);
		Set<Character> chars = errTransition.keySet();
		Iterator<Character> itS = chars.iterator();
		while(itS.hasNext()) {
			errTransition.put(itS.next(), err);
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
			ctrl = true;
			Set<State> keys = classes.keySet();
			HashMap<State, ArrayList<State>> newClasses = new HashMap<State, ArrayList<State>>();
			it = keys.iterator();
			while(it.hasNext()) {
				State base = it.next();
				ArrayList<State> currentGroup = classes.get(base);
				ArrayList<State> newGroup = new ArrayList<State>();
				newGroup.add(base);
				newClasses.put(base, newGroup);
				for(int j = 0; j < currentGroup.size(); j++) {
					State current = currentGroup.get(j);
					if(isEquivalent(base, current, transitions, classes)) {
						newGroup.add(current);
						newClasses.put(base, newGroup);
					} else {
						boolean found = false;
						Set<State> newKeys = newClasses.keySet();
						Iterator<State> it2 = newKeys.iterator();
						while(it2.hasNext()) {
							State otherBase = it2.next();
							if (base == otherBase) {
								continue;
							}
							if(isEquivalent(otherBase, current, transitions, classes)) {
								ArrayList<State> temp = newClasses.get(otherBase);
								temp.add(current);
								newClasses.put(otherBase,temp);
								found = true;
								break;
							}
						}
						if(!found) {
							ArrayList<State> temp = new ArrayList<State>();
							temp.add(current);
							newClasses.put(current, temp);
						}
					}
				}
			}
			
			//if(newClasses.equals(classes)) {
				ctrl = false;
			//}
			classes = newClasses;
		}
		Set<State> keys = classes.keySet();
		it = keys.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().name);
		}
		
		return null;
	}
	
	public boolean isEquivalent(State base, State current, HashMap<State, HashMap<Character, State>> transitions, HashMap<State, ArrayList<State>> classes) {
		Set<Character> alphabet = transitions.get(base).keySet();
		HashMap<Character, State> bTransitions = transitions.get(base);
		HashMap<Character, State> cTransitions = transitions.get(current);
		Iterator<Character> it = alphabet.iterator();
		ArrayList<ArrayList<State>> classList = new ArrayList<ArrayList<State>>();
		classList.addAll(classes.values());
		while(it.hasNext()) {
			char c = it.next();
			State bState = bTransitions.get(c);
			State cState = cTransitions.get(c);
			for(int i = 0; i < classList.size(); i++) {
				ArrayList<State> temp = classList.get(i);
				if (temp.contains(bState) && temp.contains(cState)) {
					break;
				} else if((temp.contains(bState) && !temp.contains(cState)) || (!temp.contains(bState) && temp.contains(cState))) {
					return false;
				}
			}
			
		}
		return true;
	}
	
	public SortedSet<State> checkReacheble(SortedSet<Character> alphabet, HashMap<State, HashMap<Character, State>> transitions, State initial) {
		SortedSet<State> rchStates = new TreeSet<State>();
		State current = initial;
		rchStates.add(current);
		
		HashMap<Character, State> currentTransitions = transitions.get(current);
		Iterator<Character> symbols = alphabet.iterator();
		List<State> pendentStates = new ArrayList<State>();
		while(symbols.hasNext()) {
			char c = symbols.next();
			State in = currentTransitions.get(c);
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
				State in = currentTransitions.get(c);
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
	
	public SortedSet<State> checkDeadStates(SortedSet<Character> alphabet, SortedSet<State> states, HashMap<State, HashMap<Character, State>> transitions ){
		SortedSet<State> alive = new TreeSet<State>();
		State current;
		
		Iterator<Character> symbols = alphabet.iterator();
		List<State> pendentStates = new ArrayList<State>();
		HashMap<Character, State> currentTransitions;
		
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
					State in = currentTransitions.get(c);
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
	
	public HashMap<State, HashMap<Character, State>> removeTransitions(SortedSet<Character> alphabet, SortedSet<State> states, HashMap<State,
		HashMap<Character, State>> transitions) {	
		HashMap<State, HashMap<Character, State>> newTransitions = new HashMap<State, HashMap<Character, State>>();
		Iterator<State> it = states.iterator();
		while(it.hasNext()) {
			State current = it.next();
			HashMap<Character, State> stateOldTransitions = transitions.get(current);
			HashMap<Character, State> newStateTransitions = new HashMap<Character, State>();
			Iterator<Character> symbols = alphabet.iterator();
			while(symbols.hasNext()) {
				char c = symbols.next();
				State nxtState = stateOldTransitions.get(c);
				if(states.contains(nxtState)){
					newStateTransitions.put(c, nxtState);
				} else {
					newStateTransitions.put(c, new State("$", false, -1));				
				}
			}
			newTransitions.put(current, newStateTransitions);
		}
		return newTransitions;
	}
}

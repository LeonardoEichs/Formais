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
	/**
	 * Minimiza um dado automato
	 * @param fa Automato a ser minimizado
	 * @return Automato finito
	 */
	public FiniteAutomata minimize(FiniteAutomata fa) {
		//determiniza automato caso necessário
		if(!fa.isDeterministic()) {
			FADeterminize det = new FADeterminize();
			fa = det.determinizeAutomata(fa);
		}
		
		HashMap<State, HashMap<Character, ArrayList<State>>> transitions = fa.getTransitions();
		SortedSet<Character> alphabet = fa.getAlphabet();
		SortedSet<State> states = fa.getStates();
		State initial = fa.getInitial();
		
		//Remove estados mortos e suas transições
		SortedSet<State> newStates = checkDeadStates(alphabet, states, transitions);
		transitions = removeTransitions(alphabet, newStates, transitions);
		
		//Remove estados inalcançáveis e suas transições
		newStates = checkReacheble(alphabet, transitions, initial);
		transitions = removeTransitions(alphabet, newStates, transitions);
		
		//Verifica estados equivalentes
		HashMap<State, ArrayList<State>> classes = checkEquivalence(alphabet, newStates, transitions, initial);
		FiniteAutomata minAF = buildAutomata(alphabet, classes, transitions, initial);
		
		return minAF;
	}
	/**
	 * Constroi o autômato mínimo, adicionando um estado para cada classe de equivalência
	 * @param alphabet Lista de símbolos
	 * @param classes Classes de equivalênicia
	 * @param transitions Lista de transições
	 * @param initial Estado inicial
	 * @return Automato mínimo
	 */
	public FiniteAutomata buildAutomata(SortedSet<Character> alphabet, HashMap<State, ArrayList<State>> classes, HashMap<State, HashMap<Character, ArrayList<State>>> transitions, State initial) {
		FiniteAutomata fa = new FiniteAutomata(alphabet);
		SortedSet<State> states = new TreeSet<State>();
		Set<State> keys = classes.keySet();
		Iterator<State> it = keys.iterator();
		fa.addInitialState(initial);
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
	
	/**
	 * Remove estados equivalentes entre si
	 * @param alphabet Lista de símbolos
	 * @param states Lista de estados
	 * @param transitions Lista de transições
	 * @param initial Estado inicial
	 * @return Lista de estados sem equivalência
	 */
	public HashMap<State, ArrayList<State>> checkEquivalence(SortedSet<Character> alphabet, SortedSet<State> states, HashMap<State, HashMap<Character, ArrayList<State>>> transitions, State initial) {
		//Cada classe de equivalência é representada por um estado
		HashMap<State, ArrayList<State>> classes = new HashMap<State, ArrayList<State>>();
		//Duas classes de equivalência iniciais, finais e não finais
		ArrayList<State> f = new ArrayList<State>();
		ArrayList<State> k = new ArrayList<State>();
		Iterator<State> it = states.iterator();
		while(it.hasNext()) {
			State current = it.next();
			if(current.isFinal) {
				f.add(current);
			} else {
				k.add(current);
			}
		}
		//Adiciona estado erro à classe de não finais
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
		//Enquanto houver uma nova classe de equivalência
		while(ctrl) {
			ctrl = false;
			Set<State> keys = classes.keySet();
			HashMap<State, ArrayList<State>> newClasses = new HashMap<State, ArrayList<State>>();
			it = keys.iterator();
			//Para cada classe
			while(it.hasNext()) {
				State base = it.next();
				ArrayList<State> currentGroup = classes.get(base);
				ArrayList<State> newGroup = new ArrayList<State>();
				HashMap<State, ArrayList<State>> newGroups = new HashMap<State, ArrayList<State>>();
				newGroup.add(base);
				//newClasses.put(base, newGroup);
				newGroups.put(base, newGroup);
				//Para cada estado do grupo atual
				for(int j = 0; j < currentGroup.size(); j++) {
					State current = currentGroup.get(j);
					if(base == current) {
						continue;
					}
					//Se é equivalente ao estado base, adiciona ao mesmo grupo
					if(isEquivalent(base, current, transitions, classes)) {
						newGroup.add(current);
						newGroups.remove(base);
						newGroups.put(base, newGroup);
					} else {
						boolean found = false;
						Set<State> newKeys = newGroups.keySet();
						Iterator<State> it2 = newKeys.iterator();
						//Verifica se é equivalente a algum dos novos grupos
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
						//Senão, cria um grupo novo
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
	
	/**
	 * Verifica equivalencia nas trasições de dois estados
	 * @param base Estado que representa uma classe de equivalencia
	 * @param current Estado a verificar
	 * @param transitions Lista de transições
	 * @param classes Classes de equivalencia
	 * @return Se os estados são equivalentes
	 */
	public boolean isEquivalent(State base, State current, HashMap<State, HashMap<Character, ArrayList<State>>> transitions, HashMap<State, ArrayList<State>> classes) {
		Set<Character> alphabet = transitions.get(base).keySet();
		//Obtem transições da base 
		HashMap<Character, ArrayList<State>> bTransitions = transitions.get(base);
		//Obtem transições do estado a ser verificado
		HashMap<Character, ArrayList<State>> cTransitions = transitions.get(current);
		ArrayList<ArrayList<State>> classList = new ArrayList<ArrayList<State>>();
		classList.addAll(classes.values());
		Iterator<Character> it = alphabet.iterator();
		while(it.hasNext()) {
			char c = it.next();
			State bState = bTransitions.get(c).get(0);
			State cState = cTransitions.get(c).get(0);
			//verifica se cada uma das transições do estado pertence ao mesmo grupo das transições da base
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
	
	/**
	 * Remove os estados inalcançaveis
	 * @param alphabet Lista de símbolos
	 * @param initial Estado inicial
	 * @param transitions Lista de transições
	 * @return Lista de estados alcançaveis
	 */
	public SortedSet<State> checkReacheble(SortedSet<Character> alphabet, HashMap<State, HashMap<Character, ArrayList<State>>> transitions, State initial) {
		SortedSet<State> rchStates = new TreeSet<State>();
		State current = initial;
		//Adiciona q0
		rchStates.add(current);
		
		HashMap<Character, ArrayList<State>> currentTransitions = transitions.get(current);
		Iterator<Character> symbols = alphabet.iterator();
		List<State> pendentStates = new ArrayList<State>();
		//Adiciona estados alcançaveis a partir de q0
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
		
		//Para cada novo estado alcançavel
		for(int i = 0; i < pendentStates.size(); i++) {
			current = pendentStates.get(i);
			currentTransitions = transitions.get(current);
			symbols = alphabet.iterator();
			//Para cada símbolo
			while(symbols.hasNext()) {
				char c = symbols.next();
				State in = currentTransitions.get(c).get(0);
				if(in.name == "$") {
					continue;
				}
				//Adiciona estados alcançáveis
				if(!rchStates.contains(in)) {
					rchStates.add(in);
					pendentStates.add(in);
				}
			}
		}
		return rchStates;
	}
	
	/**
	 * Remove os estados mortos
	 * @param alphabet Lista de símbolos
	 * @param states Lista de estados
	 * @param transitions Lista de transições
	 * @return Lista de estados vivos
	 */
	public SortedSet<State> checkDeadStates(SortedSet<Character> alphabet, SortedSet<State> states, HashMap<State, HashMap<Character, ArrayList<State>>> transitions ){
		SortedSet<State> alive = new TreeSet<State>();
		State current;
		
		Iterator<Character> symbols = alphabet.iterator();
		List<State> pendentStates = new ArrayList<State>();
		HashMap<Character, ArrayList<State>> currentTransitions;
		
		//Adiciona estados finais à lista de vivos
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
		//enquanto encontrar um novo estado vivo
		while(ctrl) {
			ctrl = false;
			//para cada estado ainda não listado como vivo
			for(int i = 0; i < pendentStates.size(); i++) {
				current = pendentStates.get(i);
				currentTransitions = transitions.get(current);
				symbols = alphabet.iterator();
				while(symbols.hasNext()) {
					char c = symbols.next();
					//se o estado leva pra um vivo, adiciona ele na lista de vivos
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
	
	/**
	 * Remove as transições para estados inexistentes
	 * @param alphabet Lista de simbolos do alfabeto
	 * @param states Lista de estados
	 * @param transitions Lista de transições
	 * @return Lista de transições atualizada
	 */
	public HashMap<State, HashMap<Character, ArrayList<State>>> removeTransitions(SortedSet<Character> alphabet, SortedSet<State> states, HashMap<State,
		HashMap<Character, ArrayList<State>>> transitions) {	
		HashMap<State, HashMap<Character, ArrayList<State>>> newTransitions = new HashMap<State, HashMap<Character, ArrayList<State>>>();
		Iterator<State> it = states.iterator();
		//para cada estado
		while(it.hasNext()) {
			State current = it.next();
			HashMap<Character, ArrayList<State>> stateOldTransitions = transitions.get(current);
			HashMap<Character, ArrayList<State>> newStateTransitions = new HashMap<Character, ArrayList<State>>();
			Iterator<Character> symbols = alphabet.iterator();
			//para cada simbolo do alfabeto
			while(symbols.hasNext()) {
				char c = symbols.next();
				State nxtState = stateOldTransitions.get(c).get(0);
				ArrayList<State> t = new ArrayList<State>();
				//se o estado destino não existe mais, transição para erro
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

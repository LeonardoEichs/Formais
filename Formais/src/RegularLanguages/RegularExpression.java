package RegularLanguages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;


public class RegularExpression extends RegularLanguage{

	private String re;
	private String formattedRE;
	private String completeRE;
	
	public RegularExpression(String input) {
		super(input, InputType.RE);
		this.re = input;
		this.formattedRE = formatRE(input);
		this.completeRE = this.setCompleteExpression(formattedRE);
	}

	public static RegularLanguage isValidRE(String inp) {
		RegularExpression rl = new RegularExpression(inp);
		if(validateInput(inp)) {
			return rl;
		} else {
			return null;
		}
		
	}

	/**
	 * Obtém a String que define a ER
	 * @return String que contém a ER
	 */
	@Override
	public String getDefinition() {
		return re;
	}

	@Override
	public RegularGrammar getRG() {
		// toRG;
		return null;
	}

	@Override
	public RegularExpression getRE() {
		return this;
	}

	/**
	 * Executa o algoritmo De Simone para obter
	 * o Automato Finito da ER.
	 * @return Automato Finito da ER
	 */
	@Override
	public FiniteAutomata getFA() {
		return deSimone();
	}
	
	@Override
	public FiniteAutomata reverse() {
		return this.getFA().reverse();
	}
	
	/**
	 * Obtém a ER com as concatenações explicitas.
	 * @return String com a ER
	 */
	public String getCompleteExpression() {
		return completeRE;
	}
	
	/**
	 * Explicita as concatenações em um expressão regular
	 * @param  in expressão regular a ser completada
	 * @return expressão regular completa
	 */
	public String setCompleteExpression(String in) {
		
		String re = "";
		
		re += in.charAt(0);
		for (int i = 1; i < in.length(); i++) {
			char c = in.charAt(i);
			if (Character.isLetterOrDigit(c) || c == '(') {
				if (!(in.charAt(i-1) == '(' || in.charAt(i-1) == '.' || in.charAt(i-1) == '|')) {
					re += '.';
				}
			}
			re += c;
		}
		return re;
	}
	
	/**
	 * Valida uma determinada entrada,
	 * verificando se esta é uma expressão regular correta
	 * @param  inp String a ser avaliada
	 * @return se a expressão é válida
	 */
	public static boolean validateInput(String inp) {
		inp = formatRE(inp);
		
		if(!inp.matches("^[a-z0-9\\(\\)\\?\\+\\*\\|\\.\\&]*")) {
			return false;
		}
		
		int pcount = 0;
		for (int i = 0; i < inp.length(); i++) {
			char c = inp.charAt(i);
			if (c == '(') {
				pcount++;
			} else if (c == ')') {
				pcount--;
				if (pcount < 0) {
					return false;
				}
			}
			if (i == 0) {
				continue;			
			}
			if (c == '*' || c == '+' || c == '.' || c == '?') {
				if (inp.charAt(i-1) == '|' || inp.charAt(i-1) == '.') {
					return false;
				}
			}
		}
		
		
		return pcount == 0;
	}
	
	/**
	 * Determina a prioridade de um operador da expressão
	 * @param  op operador
	 * @return prioridade representada por um inteiro
	 */
	public static int operatorPriority(char op) {
		if (op == '*' || op == '+' || op == '?') {
			return 3;
		} else if (op == '.'){
			return 2;
		} else if (op == '|') {
			return 1;
		} else {
			return 0;
		}
			
	}
	
	/**
	 * Algoritmo De Simone para transformar ER em AF
	 * @return AF correspondente à ER
	 */
	protected FiniteAutomata deSimone(){
		// monta a arvore com os operando e símbolos 
		Node root = buildTree(this.toPostOrder());
		// costura da arvore
		createThreaded(root);
		
		// obtém os símbolos da ER
		SortedSet<Character> alphabet = new TreeSet<Character>();
		for (int i = 0; i < formattedRE.length(); i++) {
			char c = formattedRE.charAt(i);
			if (Character.isLetterOrDigit(c)) {
				alphabet.add(c);
			}
		}
		
		FiniteAutomata fa = new FiniteAutomata(alphabet);
		
		//lista de composições correspondendo a cada novo estado
		HashMap<Set<Node>,State> composition = new HashMap<Set<Node>,State>();
		
		//obtém a composição de q0, percorrendo a arvore para baixo
		Set<Node> firstNodes = goDown(root, new HashSet<Node>());
		//novos estados a serem avaliados
		ArrayList<Set<Node>> nextNodes = new ArrayList<Set<Node>>();
		//adiciona a primeira composição à lista
		nextNodes.add(firstNodes);
		//determina se q0 é final, verificando se há lambda em sua composição
		boolean isFinal = false;
		for (Node nd : firstNodes) {
			if (nd.data == '$') {
				isFinal = true;
				break;
			}
		}
		State q0 = new State("q0", isFinal, 0);
		//relaciona a composição obtida a q0
		composition.put(firstNodes, q0);
		//determina q0 como estado inicial do AF
		fa.addInitialState(q0);
		int j = 1;
		//para cada nova composição(possivel estado)
		for(int i = 0; i < nextNodes.size(); i++) {
			Set<Node> stateComposition = nextNodes.get(i);
			//Estado relacionado à composição sendo verificada
			State out = composition.get(stateComposition);
			//Conjunto de composições separada por símbolos da ER
			HashMap<Character, Set<Node>> unionSymbolsComposition = new HashMap<Character, Set<Node>>();
			//Obtém as composições possíveis a partir da composição atual
			unionSymbolsComposition = getStateQiComposition(stateComposition);
			//Para cada símbolo presente na composição
			for (Character nodeSymbol : unionSymbolsComposition.keySet()) {
				Set<Node> symbolComposition = unionSymbolsComposition.get(nodeSymbol);
				//Obtém estado associado à composição do símbolo
				State in = composition.get(symbolComposition);
				//caso estado não exista
				if (in == null) {
					//cria novo estado
					in = new State("q" + j, false, j);
					j++;
					fa.addState(in);
					composition.put(symbolComposition, in); 
					nextNodes.add(symbolComposition);
					for(Node nd : symbolComposition) {
						if (nd.data == '$') { 
							in.setFinal();
							break;
						}
					}
				}
				//adiciona a transição ao AF
				fa.addTransition(out, nodeSymbol, in);
			}
		}
		
		
		return fa;
	}
	
	/**
	 * Percorre a arvore para cima para cada nodo folha na composição
	 * e obtém a proxima composição
	 * @param  qicomposition composição atual
	 * @return mapa de composições para cada simbolo
	 */
	public HashMap<Character, Set<Node>> getStateQiComposition (Set<Node> qiComposition) {
		HashMap<Character, Set<Node>> unionSymbolsComposition = new HashMap<Character, Set<Node>>();
		//para cada nodo folha
		for (Node nd : qiComposition) {
			//se não for lambda
			if (nd.data != '$') {
				//percorre a arvore para cima
				Set<Node> upComposition = goUp(nd, new HashSet<Node>()); 
				Set<Node> symbolComposition = unionSymbolsComposition.get(nd.data);
				if (symbolComposition != null) {
					symbolComposition.addAll(upComposition);
				} else { 
					unionSymbolsComposition.put(nd.data, new HashSet<Node>(upComposition));
				}
			}
		}
		return unionSymbolsComposition;
	}
	
	/**
	 * Obtém a rotina para baixo de cada operador
	 * @param  current nodo a obter rotina
	 * @param  visited lista de nodos já visitados
	 * @return nodos obtidos na rotina
	 */
	protected Set<Node> goDown(Node current, HashSet<Node> visited){
		
		char c = current.data;
		Set<Node> composition = new HashSet<Node>();
		if (c == '&') {
			if (visited.contains(current)) {
				return composition;
			} else {
				visited.add(current);
			}
		}
		switch(c) {
			case '*':
			case '?':
				composition.addAll(goDown(current.left, visited));
				if (current.right != null) {
                	composition.addAll(goUp(current.right, visited));
                } else {
                	composition.add(new Node('$'));
                } break;
			case '|':
				composition.addAll(goDown(current.left, visited));
				composition.addAll(goDown(current.right, visited)); break;
			case '.':
			case '+':
				composition.addAll(goDown(current.left, visited)); break;
			case '&':
				if (current.right != null) {
                	composition.addAll(goUp(current.right, visited));
                } else {
                    composition.add(new Node('$'));
                }
                break;
			default:
            	composition.add(current);
            	break;
            }
		return composition;
	}
	
	/**
	 * Obtém a rotina para cima de cada operador
	 * @param  current nodo a obter rotina
	 * @param  visited lista de nodos já visitados
	 * @return nodos obtidos na rotina
	 */
	protected Set<Node> goUp(Node current, HashSet<Node> visited){
		char c = current.data;
		Set<Node> composition = new HashSet<Node>();
		if (c == '*' || c == '+') {
			if (visited.contains(current)) {
				return composition;
			} else {
				visited.add(current);
			}
		}
		
		switch(c) {
		case '*':
		case '+':
			composition.addAll(goDown(current.left, visited));
			if (current.right != null) {
				composition.addAll(goUp(current.right, visited));
			} else {
				composition.add(new Node('$'));
			} break;
		case '?':
			if (current.right != null) {
				composition.addAll(goUp(current.right, visited));
			} else {
				composition.add(new Node('$'));
			} break;
		case '|':
			Node rightNode = current.right; 
            while (rightNode.data == '.' || rightNode.data == '|') {
            	rightNode = rightNode.right;
            }
            if (rightNode.right != null) {
                composition.addAll(goUp(rightNode.right, visited));
            } else {
                composition.add(new Node('$'));
            }
            break;
		case '.':
			composition.addAll(goDown(current.right, visited));
			break;
		
		default:
            if (current.right != null) {
                composition.addAll(goUp(current.right, visited));
            } else {
                composition.add(new Node('$'));
            }
            break;
        }
		return composition;
	}
	
	/**
	 * Monta a arvore relacionada à ER
	 * @param  in ER em pós ordem
	 * @return nodo raíz
	 */
	protected Node buildTree(String in) {
		int countLeafs = 0;
		int countOps = 0;
		Stack<Node> stack = new Stack<>();
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			Node node = new Node(c);
			
			if (Character.isLetterOrDigit(c) || c == '&') {
				node.n = countLeafs;
				countLeafs++;
				stack.push(node);
			} else {
				node.n = countOps;
				countOps++;
				if (operatorPriority(c) == operatorPriority('*')) {
                    node.left = stack.pop();
                } else {
                    node.right = stack.pop();
                    node.left = stack.pop();
                }
                stack.push(node);
			}
		}
		return stack.pop();
	}
	
	public static String formatRE(String in) {
		String inp = in;
		inp = inp.replaceAll("\\s+", "");
		inp = inp.replaceAll("\\*+", "*");
		inp = inp.replaceAll("\\++", "+");
		inp = inp.replaceAll("\\?\\*+", "*");
		inp = inp.replaceAll("\\?\\+", "*");
		inp = inp.replaceAll("\\+\\?", "*");
		inp = inp.replaceAll("\\*+\\?", "*");
		inp = inp.replaceAll("\\+\\*+", "*");
		inp = inp.replaceAll("\\*+\\+", "*");
		inp = inp.replaceAll("\\*+", "*");
		return inp;
	}

	
	/**
	 * Métodos utilizados para a construção da arvore com costura,
	 * obtidos em https://www.geeksforgeeks.org/convert-binary-tree-threaded-binary-tree-2/
	 * ultimo acesso 01/06/2018
	 */
	void populateQueue(Node node, Queue<Node> q) 
    {
        if (node == null)
            return;
        if (node.left != null)
            populateQueue(node.left, q);
        q.add(node);
        if (node.right != null)
            populateQueue(node.right, q);
    }
  
    // Function to traverse queue, and make tree threaded
    void createThreadedUtil(Node node, Queue<Node> q) 
    {
        if (node == null)
            return;
  
        if (node.left != null) 
            createThreadedUtil(node.left, q);        
        q.remove();
  
        if (node.right != null) 
            createThreadedUtil(node.right, q);        
  
        // If right pointer is NULL, link it to the
        // inorder successor and set 'isThreaded' bit.
        else
        {
            node.right = q.peek();
            node.isThreaded = true;
        }
    }
  
    // This function uses populateQueue() and
    // createThreadedUtil() to convert a given binary tree 
    // to threaded tree.
    void createThreaded(Node node) 
    {
        // Create a queue to store inorder traversal
        Queue<Node> q = new LinkedList<Node>();
  
        // Store inorder traversal in queue
        populateQueue(node, q);
  
        // Link NULL right pointers to inorder successor
        createThreadedUtil(node, q);
    }
    
    Node leftMost(Node node) 
    {
        while (node != null && node.left != null)
            node = node.left;
        return node;
    }
  
    // Function to do inorder traversal of a threadded binary tree
    void inOrder(Node node) 
    {
        if (node == null) 
            return;        
  
        // Find the leftmost node in Binary Tree
        Node cur = leftMost(node);
  
        while (cur != null) 
        {
            System.out.print(" " + cur.data + " ");
  
            // If this Node is a thread Node, then go to
            // inorder successor
            if (cur.isThreaded == true)
                cur = cur.right;
            else // Else go to the leftmost child in right subtree
                cur = leftMost(cur.right);
        }
    }
    
    public String toPostOrder() {
		String s = "";
		Stack<Character> stack = new Stack<Character>();
		char c;
		for (int i = 0; i < completeRE.length(); i++) {
			c = completeRE.charAt(i);
			// If the scanned character is an operand, add it to output.
			if (Character.isLetterOrDigit(c) || c == '&') {
				s += c;
			} else if (c == '(') { // If the scanned character is an '(', push it to the stack.
				stack.push(c);
			} else if (c == ')') { //  If the scanned character is an ')', pop and output from the stack 
	            					// until an '(' is encountered.
				while (!stack.isEmpty() && stack.peek() != '(') {
                    s += stack.pop();
				}
                if (!stack.isEmpty() && stack.peek() != '(') {
                    return "Invalid Expression"; // invalid expression                
                } else { 
                    stack.pop();
                }
			} else { // an operator is encountered
				while (!stack.isEmpty() && operatorPriority(c) <= operatorPriority(stack.peek())) {
                    s += stack.pop();
				}
                stack.push(c);
			}
		} 
		// pop all the operators from the stack
        while (!stack.isEmpty()) {
            s += stack.pop();
        }
		return s;
	}

	@Override
	public FiniteAutomata intersection(RegularLanguage rl) {
		if(rl.getType() == InputType.FA) {
			FiniteAutomata fa = (FiniteAutomata) rl;
			return this.getFA().intersection(fa);
		}
		else if(rl.getType() == InputType.RG) {
			RegularGrammar rg = (RegularGrammar) rl;
			FiniteAutomata fa = rg.getFA();
			return this.getFA().intersection(fa);
		}
		else if(rl.getType() == InputType.RE) {
			RegularExpression re = (RegularExpression) rl;
			FiniteAutomata fa = re.getFA();
			return this.getFA().intersection(fa);

		}
		return null;
	}

	@Override
	public FiniteAutomata difference(RegularLanguage rl) {
		if(rl.getType() == InputType.FA) {
			FiniteAutomata fa = (FiniteAutomata) rl;
			return this.getFA().difference(fa);
		}
		else if(rl.getType() == InputType.RG) {
			RegularGrammar rg = (RegularGrammar) rl;
			FiniteAutomata fa = rg.getFA();
			return this.getFA().difference(fa);
		}
		else if(rl.getType() == InputType.RE) {
			RegularExpression re = (RegularExpression) rl;
			FiniteAutomata fa = re.getFA();
			return this.getFA().difference(fa);

		}
		
		return null;
	}
}


class Node 
{
    public char data;
    public Node left, right;
    public boolean isThreaded;
    public int n;
  
    public Node(char item) 
    {
        data = item;
        left = right = null;
        int n = -1;
    }
    
    @Override
	public boolean equals(Object obj) {
		if(obj == null || !Node.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		Node node = (Node)obj;
		if (node.data == this.data
				&& node.n == this.n) {
			return true;
		}
		return false;
	}
    @Override
	public int hashCode() {
		return Objects.hash(this.n, this.data);
	}
}

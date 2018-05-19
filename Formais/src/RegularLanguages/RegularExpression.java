package RegularLanguages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import RegularLanguages.RegularLanguage.InputType;

public class RegularExpression extends RegularLanguage{

	private String re;
	private String formattedRE;
	private String completeRE;
	
	public RegularExpression(String input) {
		super(input, InputType.RE);
		this.re = input;
		this.formattedRE = input.replaceAll("\\s+", "");
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

	@Override
	public FiniteAutomata getFA() {
		return deSimone();
	}
	
	public String getCompleteExpression() {
		return completeRE;
	}
	
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
	
	public static boolean validateInput(String inp) {
		inp = inp.replaceAll("\\s+", "");
		
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
	
	protected FiniteAutomata deSimone(){
		Node root = buildTree(this.toPostOrder());
		
		createThreaded(root);
		
		SortedSet<Character> alphabet = new TreeSet<Character>();
		for (int i = 0; i < formattedRE.length(); i++) {
			char c = formattedRE.charAt(i);
			if (Character.isLetterOrDigit(c)) {
				alphabet.add(c);
			}
		}
		
		FiniteAutomata fa = new FiniteAutomata(alphabet);
		
		HashMap<Set<Node>,State> composition = new HashMap<Set<Node>,State>();
		
		Set<Node> firstNodes = goDown(root, new HashSet<Node>());
		ArrayList<Set<Node>> nextNodes = new ArrayList<Set<Node>>();
		nextNodes.add(firstNodes);
		boolean isFinal = false;
		for (Node nd : firstNodes) {
			if (nd.data == '$') {
				isFinal = true;
				break;
			}
		}
		State q0 = new State("q0", isFinal, 0);
		composition.put(firstNodes, q0);
		fa.addInitialState(q0);
		int j = 1;
		for(int i = 0; i < nextNodes.size(); i++) {
			Set<Node> stateComposition = nextNodes.get(i);
			State out = composition.get(stateComposition);
			HashMap<Character, Set<Node>> unionSymbolsComposition = new HashMap<Character, Set<Node>>();	
			unionSymbolsComposition = getStateQiComposition(unionSymbolsComposition, stateComposition);
			for (Character nodeSymbol : unionSymbolsComposition.keySet()) {
				Set<Node> symbolComposition = unionSymbolsComposition.get(nodeSymbol);
				State in = composition.get(symbolComposition);
				if (in == null) {
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
				fa.addTransition(out, nodeSymbol, in);
			}
		}
		
		
		return fa;
	}
	
	public HashMap<Character, Set<Node>> getStateQiComposition (HashMap<Character, Set<Node>> unionSymbolsComposition, Set<Node> qiComposition) {
		for (Node nd : qiComposition) {
			if (nd.data != '$') { 
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
	
	protected Set<Node> goDown(Node current, HashSet<Node> visited){
		
		char c = current.data;
		//Stack<Node> nodesToGo = new Stack<Node>();
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
	
	protected Set<Node> goUp(Node current, HashSet<Node> visited){
		char c = current.data;
		//Stack<Node> nodesToGo = new Stack<Node>();
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
	
	protected Node buildTree(String in) {
		int countLeafs = 0;
		Stack<Node> stack = new Stack<>();
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			Node node = new Node(c);
			
			if (Character.isLetterOrDigit(c) || c == '&') {
				node.n = countLeafs;
				countLeafs++;
				stack.push(node);
			} else {
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
	
	void printPreorder(Node node)
	{
	     if (node == null)
	          return;
	     System.out.print(node.data);
	     
	     printPreorder(node.left);  
	 
	     printPreorder(node.right);
	} 
	
	
	
	
	
	
	
	
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

package RegularLanguages;

public abstract class RegularLanguage {
	
	public enum InputType {RE, RG, FA, UNDEFINED};
	
	public enum Operation {UNION, CONCATENATION, INTERSECTION, DIFFERENCE};
	
	protected String input;
	private String id;
	private InputType type = InputType.UNDEFINED;
	
	
	public RegularLanguage(InputType type) {
		this.type = type;
	}
	
	public RegularLanguage(String input, InputType type) {
		this.input = input;
		this.type = type;
	}
	
	public static RegularLanguage validate(String inp) {
		if (inp.contains("->")) {
			return (RegularLanguage)RegularGrammar.isValidRG(inp);
		} else {
			return (RegularLanguage)RegularExpression.isValidRE(inp);
		}
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getInput() {
		return this.input;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public InputType getType() {
		return this.type;
	}
	
	public boolean isFinite() {
		return true;
	}
	
	public boolean isEmpty() {
		return true;
	}
	
	public boolean isEqualTo(RegularLanguage l1, RegularLanguage l2) {
		return true;
	}
	
	public boolean isContainedIn(RegularLanguage l1, RegularLanguage l2) {
		return true;
	}
	
	public String toString() {
		return this.id;
	}
	
	public abstract String getDefinition();
	
	public abstract RegularGrammar getRG();
	
	public abstract RegularExpression getRE();
	
	public abstract FiniteAutomata getFA();
	
	public abstract FiniteAutomata reverse();
	

}

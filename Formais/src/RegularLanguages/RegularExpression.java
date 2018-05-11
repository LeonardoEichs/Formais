package RegularLanguages;

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
		// TODO Auto-generated method stub
		return null;
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

}

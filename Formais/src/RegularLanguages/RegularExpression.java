package RegularLanguages;

import RegularLanguages.RegularLanguage.InputType;

public class RegularExpression extends RegularLanguage{

	private String re;
	
	public RegularExpression(String input) {
		super(input, InputType.RE);
		this.re = input;
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
		}
		return pcount == 0;
	}

}

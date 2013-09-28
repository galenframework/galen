package net.mindengine.galen.parser;

import org.apache.commons.lang3.StringUtils;

import net.mindengine.galen.specs.reader.StringCharReader;

public class MathParser {
	
	private char mathSymbol = '@';

	public String parse(String template, String initialValue) {
		
		StringCharReader reader = new StringCharReader(template);
		
		StringBuffer text = new StringBuffer();
		
		while(reader.hasMore()) {
			char ch = reader.next();
			
			if (ch == mathSymbol) {
				char nextCh = reader.next();
				if (nextCh == mathSymbol) {
					text.append(mathSymbol);
				}
				else if (nextCh == '{') {
					 String expression = reader.readUntilSymbol('}').replace(" ", "");
					 if (expression.length() < 2) {
						 throw new SyntaxException("Can't parse expression: " + expression);
					 }
					 
					 text.append(convertExpression(initialValue, expression));
					 
				}
				else {
					text.append(initialValue);
					text.append(nextCh);
				}
			}
			else {
				text.append(ch);
			}
		}
		
		return text.toString();
	}

	private String convertExpression(String initialValue, String expression) {
		String number = expression.substring(1);
		if (!StringUtils.isNumeric(number)) {
			throw new SyntaxException("Expected a number: " + number);
		}
		
		if (StringUtils.isNumeric(initialValue)) {
			char operationSymbol = expression.charAt(0);
			
			int initial = Integer.parseInt(initialValue);
			int added = Integer.parseInt(number);
			
			if (operationSymbol == '+') {
				return Integer.toString(initial + added);
			}
			else if (operationSymbol == '-') {
				return Integer.toString(initial - added);
			}
			else if (operationSymbol == '*') {
				return Integer.toString(initial * added);
			}
			else if (operationSymbol == '/') {
				return Integer.toString(initial / added);
			}
			else if (operationSymbol == '%') {
				return Integer.toString(initial % added);
			}
			else throw new SyntaxException("Unknown operation: " + operationSymbol);
		}
		else {
			return initialValue;
		}
	}

}

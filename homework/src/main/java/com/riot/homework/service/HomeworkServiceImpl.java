package com.riot.homework.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

@Service("homeworkService")
public class HomeworkServiceImpl implements HomeworkService{
	
	private final String EXPRESSION_FORMAT_ERROR = "403";
	private final String QUERY_PARAM_NOT_EXIST = "404";
	private final String QUERY_VALUE_WRONG_FORMAT = "503";
	
	private Map<String, Double> query = new HashMap<String, Double>();

	private	int operandCount = 0;
	private int operatorCount = 0;

	@Override
	public Double calculateResult(HttpServletRequest request, HttpServletResponse response, String expression) throws IOException {
		String errorcode = "";	// 에러 코드
		String postfixExp = ""; // 후위연산식
		
		Enumeration<String> keys = request.getParameterNames();
		
		String key = new String();
		double value;
		boolean hasQueryParam = false; // 쿼리 변수가 포함되어 있는지 확인하는 Flag
		
		// 쿼리 변수를 확인해서 있을 경우 Map에 담아서 넣는다.
		while (keys.hasMoreElements()) {
			key = keys.nextElement();
			
			// expression 쿼리는 제외한다.
			if (key.equals("expression")) {
				continue;
				
			}
			
			hasQueryParam = true;
			
			// Query Paramater의 형식이 숫자가 아닌 경우 에러 처리.
			try {
				value = Double.parseDouble(request.getParameter(key));
			} catch (NumberFormatException e) {
				errorcode = QUERY_VALUE_WRONG_FORMAT;
				//hasQueryParam = false;
				return executeError(response, QUERY_VALUE_WRONG_FORMAT);
			}

			query.put(key, value);
		}

		// 쿼리 변수가 존재한다면 변수에 값을 넣어준다.
		if (hasQueryParam) {
			String newExpression = inputQueryValue(expression);
			expression = newExpression;
		}
		
		if (expression.equals("error")) {
			return executeError(response, QUERY_PARAM_NOT_EXIST);
		}

		// Expression 괄호 체크검사
		if (!parenthesesCheck(expression)) {
			return executeError(response, EXPRESSION_FORMAT_ERROR);
			
		// 괄호 체크에 이상이 없으면 후외연산식으로 변환
		} else {
			postfixExp = postfix(expression);
		}
		
		
		if (!postfixExp.equals("") && operandCount != (operatorCount + 1)) {
			return executeError(response, EXPRESSION_FORMAT_ERROR);
		}
		
		String result = result(postfixExp).toString();
		
		// 연산자 갯수 체크 후 초기화
		operandCount = 0;
		operatorCount = 0;

		// 쿼리 변수 초기화
		query.clear();
		
		double resultParam = 0;
		
		if (!result.equals("")) {
			resultParam = Double.valueOf(result);
		}
		
		return resultParam;
	}
	
	/**
	 * 
	 * @param response 응답코드를 넣을  response
	 * 		403 - Expression 형식 오류
	 * 		404 - 변수명이 expression에 존재하는데 Query parameter에 해당 변수가 존재하지 않는 경우
	 * 		503 - 변수의 값이 적절한 숫자 형식의 값이 아닌 경우
	 * @param errorcode 에러코드 유형
	 * @return -1
	 * @throws IOException
	 */
	public Double executeError(HttpServletResponse response, String errorcode) throws IOException {
		// 응답코드 셋팅
		switch (errorcode) {
		case EXPRESSION_FORMAT_ERROR : 
			response.sendError(403, "Expression Format error");
			break;
		case QUERY_PARAM_NOT_EXIST :
			response.sendError(404, "Query paramater not exist in expression");
			break;
		case QUERY_VALUE_WRONG_FORMAT :
			response.sendError(503, "Query paramater value is wrong format");
			break;
		default : 
			break;
		}
		
		return (double) -1;
	}

	/**
	 * expression을 중위표기법 -> 후위표기법으로 변환하는 메서드
	 * @param expression 중위표기법으로 작성된 expression
	 * @return 후위표기법으로 작성된 expression
	 */
	public String postfix(String expression) {
		Double value;
		
		// 숫자의 끝 flag
		boolean endOfNumber = false;
		String postfixExp = new String();
		Stack<Character> stack = new Stack<Character>();
		int length = expression.length();

		for (int i = 0; i < length; i++) {
			switch (expression.charAt(i)) {
			// 피연산자는 그대로 출력한다.
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '.':
				postfixExp = postfixExp.concat(expression.charAt(i) + "");
				endOfNumber = true;
				break;
			// 왼쪽괄호는 스택에 푸시.
			case '(':
				if (endOfNumber == true) {
					postfixExp = postfixExp.concat(" ");
					endOfNumber = false;
					operandCount++;
				}

				stack.push('(');
				break;
			// 우측괄호인 경우 좌괄호가 나올때까지 팝하여 문자열에 더하고
			// 좌괄호는 팝하여 버린다.
			case ')':
				if (endOfNumber == true) {
					postfixExp = postfixExp.concat(" ");
					endOfNumber = false;
					operandCount++;
				}

				while (stack.peek() != '(') {
					postfixExp = postfixExp.concat(stack.pop().toString());
				}

				Object openParen = stack.pop();
				break;
			case '+' :
			case '-' :
			case '*' :
			case '/' :
			case '^' :
				if (endOfNumber == true) {
					postfixExp = postfixExp.concat(" ");
					endOfNumber = false;
					operandCount++;
				}
				// 연산자를 만나면 스택에서 그 연산자보다 낮은 우선순위의 연산자를 만날 때까지
				// 팝하여 출력한 뒤에 자신을 푸시한다.(우선순위가 같거나 높은것은 팝한다.)
				while (!stack.isEmpty() && stack.peek() != '('
						&& getPrec(expression.charAt(i)) <= getPrec(stack.peek())) {
					postfixExp = postfixExp.concat(stack.pop().toString());
				}
				stack.push(expression.charAt(i));
				operatorCount++;
				break;
			}
		}

		if (endOfNumber == true) {
			postfixExp = postfixExp.concat(" ");
			endOfNumber = false;
			operandCount++;
		}

		// 모든 작업이 끝나면 스택에 있는 연산자들을 모두 팝하여 출력한다.
		while (!stack.isEmpty()) {
			postfixExp = postfixExp.concat(((Character) stack.pop()).toString());
		}


		return postfixExp;
	}

	/**
	 *  연산자의 우선순위를 Return.
	 * @param op 연산자
	 * @return 우선순위
	 */
	private int getPrec(char op) {
		int prec = 0;

		switch (op) {
		case '+' :
		case '-' :
			prec = 1;
			break;
		case '*' :
		case '/' :
			prec = 2;
			break;
		}
		return prec;
	}

	/**
	 * 후위표기법으로 변경된 Expression을 이용해 연산을 하는 메서드
	 * @param 후위표기법으로 변경된 Expression
	 * @return 결과값
	 */
	public Double result(String input) {
		double value, buffer;
		double tempNum1, tempNum2;
		String temp = new String();
		Stack<Double> stack = new Stack<Double>();
		int length = input.length();

		for (int i = 0; i < length; i++) {
			switch (input.charAt(i)) {

			case '0' :
			case '1' :
			case '2' :
			case '3' :
			case '4' :
			case '5' :
			case '6' :
			case '7' :
			case '8' :
			case '9' :
			case '.' :
				// 여기까지는 아직 공백을 만나지 않았으므로 수식의 끝이 아니다.
				temp = temp.concat(input.charAt(i) + "");
				break;
			case ' ' :
				// 공백을 만나서야 비로서 수식을 스택에 넣는다.
				// 공백을 만나기전에 수식이 여러개 있었다면 temp에 붙어서 저장되어 있다.
				stack.push(new Double(temp));
				temp = new String();
				break;
			case '+' :
				tempNum1 = stack.pop();
				tempNum2 = stack.pop();
				value = tempNum1 + tempNum2;
				stack.push(value);
				break;
			case '-' :
				tempNum1 = stack.pop();
				tempNum2 = stack.pop();
				value = tempNum2 - tempNum1;
				stack.push(value);
				break;
			case '*' :
				tempNum1 = stack.pop();
				tempNum2 = stack.pop();
				value = tempNum1 * tempNum2;
				stack.push(value);
				break;
			case '/' :
				tempNum1 = stack.pop();
				tempNum2 = stack.pop();
				value = tempNum2 / tempNum1;
				stack.push(value);
				break;
			case '^' :
				tempNum1 = stack.pop();
				tempNum2 = stack.pop();
				value = Math.pow(tempNum2, tempNum1);
				stack.push(value);
				break;
			}
		}
		return stack.peek();
	}

	/**
	 * 괄호의 짝이 올바른지 체크하는 메서드
	 * @param input 괄호의 짝이 맞는지 확인할 expression
	 * @return 괄호의 짝이 맞는 경우 true, 괄호의 짝이 맞지않는 경우 false
	 */
	public boolean parenthesesCheck(String input) {
		Stack<Character> stack = new Stack<Character>();

		int length = input.length();
		boolean isError = true;

		for (int i = 0; i < length; i++) {
			char character = input.charAt(i);

			// 왼쪽 괄호일 경우 Stack에 쌓기
			if (character == '(' || character == '{') {
				stack.push(character);

				// 오른쪽 괄호일 경우 Stack에서 빼서 확인.
			} else if (character == ')' || character == '}') {
				if (stack.isEmpty()) {
					isError = false;
					break;
				} else {
					char startCharacter = stack.pop();

					// 입력된 두 괄호가 짝이 맞지 않는 경우 판별
					if ((character == ')' && startCharacter != '(') || character == '}' && startCharacter != '{') {
						isError = false;
						break;
					}
				}

			}

		}

		if (isError) {
			if (stack.size() == 0) {
				return true;
				// Stack에 데이터가 남아 있다면 에러
			} else if (stack.size() > 0) {
				return false;
			} else {
				return false;
			}

		} else {
			return false;
		}

	}

	/**
	 * 변수가 있는 Expression에 변수값을 넣어서 새로운 Expression을 반환하는 메서드
	 * @param 변수가 들어있는 expression
	 * @return 변수에 값을 넣은 새로운 expression
	 */
	public String inputQueryValue(String expression) {
		int length = expression.length();
		StringBuilder sb = new StringBuilder();
		StringBuilder newExpression = new StringBuilder();
		ArrayList<String> keyArray = new ArrayList<String>();

		String key = new String();

		for (int i = 0; i < length; i++) {
			char character = expression.charAt(i);

			if ((character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z')) {
				sb.append(character);
			} else {

				if (sb.length() > 0) {
					key = sb.toString();
					
					if (query.containsKey(key)) {
						newExpression.append(query.get(key)).append("");
						key = "";
					} else {
						return "error";
					}
				}

				newExpression.append(character);
				// query key값 저장 변수 초기화.
				sb.setLength(0);
			}

		}

		if (sb.length() > 0) {
			key = sb.toString();

			if (query.containsKey(key)) {
				newExpression.append(query.get(key)).append("");
				key = "";
			} else {
				return "error";
			}
		}

		return newExpression.toString();
	}
	

	public Map<String, Double> getQuery() {
		return query;
	}

	public void setQuery(Map<String, Double> query) {
		this.query = query;
	}
	
}

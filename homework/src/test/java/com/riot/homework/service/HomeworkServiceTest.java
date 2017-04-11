package com.riot.homework.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class HomeworkServiceTest {

	@Test
	/**
	 * 후위표기법으로 변경하는 메서드 테스트
	 */
	public void testPostfix() {
		HomeworkServiceImpl service = new HomeworkServiceImpl();
		String expression1 = "4 + (8 + 1/8 * 8 + 1) / { 1 + {1 - 1}} - 0.001";
		String expression2 = "1 + 2";
		String expression3 = "1 * 2 + 3 / 4";
		
		String result1 = service.postfix(expression1);
		String result2 = service.postfix(expression2);
		String result3 = service.postfix(expression3);
		
		assertTrue(result1.equals("4 8 1 8 /8 *+1 +1 /+1 +1 -0.001 -"));
		assertTrue(result2.equals("1 2 +"));
		assertTrue(result3.equals("1 2 *3 4 /+"));
	}
	
	@Test
	/**
	 * 괄호의 짝이 올바른지 체크하는 메서드.
	 */
	public void parenthesesCheck() {
		HomeworkServiceImpl service = new HomeworkServiceImpl();
		String expression1 = "(())())";
		String expression2 = "{{(()()}()";
		String expression3 = "{()()}{(())}";
		
		boolean result1 = service.parenthesesCheck(expression1);
		boolean result2 = service.parenthesesCheck(expression2);
		boolean result3 = service.parenthesesCheck(expression3);
		
		assertFalse(result1);
		assertFalse(result2);
		assertTrue(result3);
	}
	
	@Test
	/**
	 * 변수가 있는 Expression에 변수값을 넣어서 새로운 Expression을 반환하는 메서드
	 */
	public void inputQueryValueTest() {
		HomeworkServiceImpl service = new HomeworkServiceImpl();
		Map<String, Double> query = new HashMap<String, Double>();
		query.put("abc", (double) 1);
		query.put("abcd", (double) 2);
		query.put("abcde", (double) 3);
		service.setQuery(query);
		
		String expression1 = "abc + 1";
		String expression2 = "abc + abcd + 1";
		String expression3 = "abc + abcd + abcde + 1";
		
		String result1 = service.inputQueryValue(expression1);
		String result2 = service.inputQueryValue(expression2);
		String result3 = service.inputQueryValue(expression3);

		assertTrue(result1.equals("1.0 + 1"));
		assertTrue(result2.equals("1.0 + 2.0 + 1"));
		assertTrue(result3.equals("1.0 + 2.0 + 3.0 + 1"));
	}
	
	@Test
	/**
	 * 후위표기법으로 변경된 Expression을 이용해 연산을 하는 메서드
	 */
	public void resultTest() {
		HomeworkServiceImpl service = new HomeworkServiceImpl();
		
		String expression1 = "4 8 1 8 /8 *+1 +1 /+1 +1 -0.001 -";
		String expression2 = "1 2 +";
		String expression3 = "1 2 *3 4 /+";
		
		Double result1 = service.result(expression1);
		Double result2 = service.result(expression2);
		Double result3 = service.result(expression3);

		assertEquals(13.999, result1, 0);
		assertEquals(3.0, result2, 0);
		assertEquals(2.75, result3, 0);
	}

}

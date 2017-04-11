package com.riot.homework.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HomeworkService {
	Double calculateResult(HttpServletRequest request, 
						   HttpServletResponse response,
						   String expression) throws IOException;
	
}

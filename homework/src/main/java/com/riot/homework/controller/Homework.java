package com.riot.homework.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riot.homework.dto.HomeworkDTO;
import com.riot.homework.service.HomeworkService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api
@RestController
public class Homework {

	@Resource(name="homeworkService")
	HomeworkService service;

	@ApiOperation(value = "사칙연산 계산", notes = "사칙연산을 계산합니다.")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "expression", value = "expression", required = false, dataType = "string", paramType = "query")
    })
	
	@ApiResponses(value = {
			@ApiResponse(code = 403, message = "Expression Format error") ,
			@ApiResponse(code = 404, message = "Query paramater not exist in expression") ,
			@ApiResponse(code = 503, message = "Query paramater value is wrong format")
	})
	
	@RequestMapping(value = "/homework", method = RequestMethod.POST)
	public HomeworkDTO calculate(
						HttpServletRequest request, 
						HttpServletResponse response,
						@RequestParam("expression") String expression) throws IOException {
		
		Double result = service.calculateResult(request, response, expression);
		
		HomeworkDTO returnParam = new HomeworkDTO();
		returnParam.setResult(result);
		
		return returnParam;
	}

}

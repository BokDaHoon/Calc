package com.riot.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

public class HomeworkDTO {
	private Double result;
	
	@JsonProperty(required = true)
    @ApiModelProperty(notes = "calculate result", required = true)
	public Double getResult() {
		return result;
	}
	
	public HomeworkDTO setResult(Double result) {
		this.result = result;
		return this;
	}
}

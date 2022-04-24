package com.mustakim.codingchallenge.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class TaskResult {
	
@RequestMapping(value = "/", method = RequestMethod.GET)
public String getResult() {
	
	return "this is the result 2";
}

}

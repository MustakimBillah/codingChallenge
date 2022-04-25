package com.mustakim.codingchallenge.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController

public class TaskResult {
	
@RequestMapping(value = "/", method = RequestMethod.GET,produces = "application/json")

public Map<String, List<combinedDTO>> getResult() {
	
	int latestXkcd=getLatestXkcdNum();
	
	List<XkcdDTO> xkcdList = getLatestXkcds(latestXkcd);
	
	String baseUri = "https://xkcd.com/";
	String extension = "/info.0.json";
	
	List <combinedDTO> result = new ArrayList<combinedDTO>();
	
	for(int i=0;i<xkcdList.size();i++) {
		XkcdDTO item = xkcdList.get(i);
		
		String convertedDate = item.day+"-"+item.month+"-"+item.year;
		
		combinedDTO element = new combinedDTO();
		
		element.pictureURL=item.img;
		element.title=item.title;
		element.webURL=baseUri+item.num+extension;
		element.publishingDate=convertedDate;
		result.add(element);
	}
	

	Map<String, List<combinedDTO>> finalResult = new HashMap<>();
	finalResult.put("data", result);
	
	return finalResult;
}

public int getLatestXkcdNum() {
	
	final String uri = "https://xkcd.com/info.0.json";

	RestTemplate restTemplate = new RestTemplate();
	String result = restTemplate.getForObject(uri, String.class);
	
	JsonObject convertedObject = new Gson().fromJson(result, JsonObject.class);
	
	return convertedObject.get("num").getAsInt();
}

public List<XkcdDTO> getLatestXkcds(int id){
	
	List<XkcdDTO> data = new ArrayList<>();
	
	String baseUri = "https://xkcd.com/";
	String extension = "/info.0.json";
	
	String uri=null;
	String result = null;
	RestTemplate restTemplate = new RestTemplate();
	
	for(int i=1;i<=10;i++) {
		uri = baseUri + id + extension;
		id--;
		result = restTemplate.getForObject(uri, String.class);
		XkcdDTO temp= new Gson().fromJson(result, XkcdDTO.class);
		data.add(temp);
	}
	return data;
}

}

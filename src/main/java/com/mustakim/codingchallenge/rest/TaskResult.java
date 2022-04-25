package com.mustakim.codingchallenge.rest;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

@RestController

public class TaskResult {

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")

	public Map<String, List<combinedDTO>> getResult() {

		int latestXkcd = getLatestXkcdNum();

		List<XkcdDTO> xkcdList = getLatestXkcds(latestXkcd);

		String baseUri = "https://xkcd.com/";
		String extension = "/info.0.json";

		List<combinedDTO> result = new ArrayList<combinedDTO>();
        
		Calendar cal  = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		for (int i = 0; i < xkcdList.size(); i++) {
			XkcdDTO item = xkcdList.get(i);

			//String convertedDate = item.day + "-" + item.month + "-" + item.year;
			
			cal.set(Integer.parseInt(item.year) ,Integer.parseInt(item.month)-1,Integer.parseInt(item.day),0,0);

			combinedDTO element = new combinedDTO();

			element.pictureURL = item.img;
			element.title = item.title;
			element.webURL = baseUri + item.num + extension;
			element.publishingDate = df.format(cal.getTime());
			result.add(element);
		}

		
		List<combinedDTO> rssData = getLatestFeeds();
		result.addAll(rssData);
		
		Collections.sort(result, new Comparator<combinedDTO>() {
            @Override
            public int compare(combinedDTO object1, combinedDTO object2) {
                return object2.getPublishingDate().compareTo(object1.getPublishingDate());
            }
        });
		
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

	public List<XkcdDTO> getLatestXkcds(int id) {

		List<XkcdDTO> data = new ArrayList<>();

		String baseUri = "https://xkcd.com/";
		String extension = "/info.0.json";

		String uri = null;
		String result = null;
		RestTemplate restTemplate = new RestTemplate();

		for (int i = 1; i <= 10; i++) {
			uri = baseUri + id + extension;
			id--;
			result = restTemplate.getForObject(uri, String.class);
			XkcdDTO temp = new Gson().fromJson(result, XkcdDTO.class);
			data.add(temp);
		}
		return data;
	}

	public List<combinedDTO> getLatestFeeds() {

		List<combinedDTO> result = new ArrayList<combinedDTO>();

		try {
			String url = "http://feeds.feedburner.com/PoorlyDrawnLines";

			try (XmlReader reader = new XmlReader(new URL(url))) {

				SyndFeed feed = new SyndFeedInput().build(reader);

				int count = 0;
				
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				for (SyndEntry entry : feed.getEntries()) {

					combinedDTO element = new combinedDTO();

					element.setWebURL(entry.getLink());
					element.setTitle(entry.getTitle());
					element.setPublishingDate(df.format(entry.getPublishedDate()));

					Pattern p = Pattern.compile("src=\"(.*?)\"");
					Matcher m = p.matcher(entry.getContents().get(0).getValue());
					if (m.find()) {
						element.setPictureURL(m.group(1));
					}

					result.add(element);

					count++;
					if (count == 10)
						break;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}

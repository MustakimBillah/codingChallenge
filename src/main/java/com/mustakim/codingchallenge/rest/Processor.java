package com.mustakim.codingchallenge.rest;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mustakim.codingchallenge.dto.CombinedDTO;
import com.mustakim.codingchallenge.dto.XkcdDTO;
import com.mustakim.codingchallenge.utils.Configuration;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

@Service
public class Processor {

	public int getLatestXkcdNum() {

		final String uri = Configuration.getInstance().getLatestURI();

		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(uri, String.class);

		JsonObject convertedObject = new Gson().fromJson(result, JsonObject.class);

		return convertedObject.get("num").getAsInt();
	}

	public List<XkcdDTO> getLatestXkcds(int id) {

		List<XkcdDTO> data = new ArrayList<>();

		String baseUri = Configuration.getInstance().getBaseURI();
		String extension = Configuration.getInstance().getExtension();

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

	public List<CombinedDTO> getLatestFeeds() {

		List<CombinedDTO> result = new ArrayList<CombinedDTO>();

		try {
			
			String url = Configuration.getInstance().getRssURI();

			try (XmlReader reader = new XmlReader(new URL(url))) {

				SyndFeed feed = new SyndFeedInput().build(reader);

				int count = 0;

				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				for (SyndEntry entry : feed.getEntries()) {

					CombinedDTO element = new CombinedDTO();

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
			System.out.println(e);
		}
		return result;
	}
}

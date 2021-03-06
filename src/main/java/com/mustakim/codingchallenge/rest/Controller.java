package com.mustakim.codingchallenge.rest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mustakim.codingchallenge.dto.CombinedDTO;
import com.mustakim.codingchallenge.dto.XkcdDTO;
import com.mustakim.codingchallenge.utils.Configuration;

@RestController

public class Controller {

	@Autowired
	private Processor processor;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Map<String, List<CombinedDTO>> getResult() {

		Map<String, List<CombinedDTO>> finalResult = new HashMap<>();

		try {
			
            //loading configuraion file 
			Configuration.getInstance().readConfFile();
			
			//get latest web comic id
			int latestXkcd = processor.getLatestXkcdNum();
			
            // get latest 10 web comic data
			List<XkcdDTO> xkcdList = processor.getLatestXkcds(latestXkcd);

			String baseUri = Configuration.getInstance().getBaseURI();
			String extension = Configuration.getInstance().getExtension();

			List<CombinedDTO> result = new ArrayList<CombinedDTO>();

			Calendar cal = Calendar.getInstance();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			for (int i = 0; i < xkcdList.size(); i++) {

				XkcdDTO item = xkcdList.get(i);

				cal.set(Integer.parseInt(item.year), Integer.parseInt(item.month) - 1, Integer.parseInt(item.day), 0,
						0);

				CombinedDTO element = new CombinedDTO();

				element.pictureURL = item.img;
				element.title = item.title;
				element.webURL = baseUri + item.num + extension;
				element.publishingDate = df.format(cal.getTime()); // conversion of date
				result.add(element);
			}

			List<CombinedDTO> rssData = processor.getLatestFeeds();
			result.addAll(rssData);
			
			//sorting date wise in descending order
			Collections.sort(result, new Comparator<CombinedDTO>() {
				@Override
				public int compare(CombinedDTO object1, CombinedDTO object2) {
					return object2.getPublishingDate().compareTo(object1.getPublishingDate());
				}
			});

			finalResult.put("data", result);

		} catch (Exception ex) {
			finalResult.put("data", null);
			return finalResult;
		}
		return finalResult;
	}

}

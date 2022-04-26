package com.mustakim.codingchallenge;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mustakim.codingchallenge.dto.CombinedDTO;
import com.mustakim.codingchallenge.dto.XkcdDTO;
import com.mustakim.codingchallenge.rest.Controller;
import com.mustakim.codingchallenge.rest.Processor;
import com.mustakim.codingchallenge.utils.Configuration;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)

public class ControllerTest {

	private MockMvc mockMvc;

	@Mock
	private Processor processor;

	@InjectMocks
	private Controller controller;

	@Before
	public void setUp() throws Exception {

		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void testGetResult() throws Exception {

		Processor temp = new Processor();
		Configuration.getInstance().readConfFile();
		int latestNum = temp.getLatestXkcdNum();

		List<XkcdDTO> getLatestXkcds = temp.getLatestXkcds(latestNum);

		List<CombinedDTO> feeds = temp.getLatestFeeds();

		when(processor.getLatestXkcdNum()).thenReturn(latestNum);
		when(processor.getLatestXkcds(latestNum)).thenReturn(getLatestXkcds);
		when(processor.getLatestFeeds()).thenReturn(feeds);

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data", Matchers.hasSize(20)));

	}

}

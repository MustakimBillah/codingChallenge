package com.mustakim.codingchallenge.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Configuration {

	private static Configuration instance = null;
	private final String CONFIGURATION_FILE_NAME = "configuration.properties";
	private String baseURI;
	private String extension;
	private String latestURI;
	private String rssURI;

	public void readConfFile() throws IOException {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(CONFIGURATION_FILE_NAME);
			prop.load(input);
		} catch (FileNotFoundException ex) {
			System.out.println("CONFIGURATION FILE NOT FOUND !!" + ex);
		} catch (IOException ex) {
			System.out.println(" CANNOT LOAD CONFIGURATION FILE !!");
		}

		setBaseURI(prop.getProperty("baseURI"));
		setExtension(prop.getProperty("extension"));
		setLatestURI(prop.getProperty("latestURI"));
		setRssURI(prop.getProperty("rssURI"));

	}

	public static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
		}
		return instance;
	}

}

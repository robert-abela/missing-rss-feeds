package com.robertabela.rss.lidl;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Constants {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd/MM/yyyy");
	public static final TimeZone MT_TIMEZOME = TimeZone.getTimeZone("CEST");
	
	public static final String BASE_URL = "https://www.lidl.com.mt";
	public static final String START_URL = BASE_URL+"/en/non-food.htm";

	public static final String AUTHOR = "robert-abela/missing-rss-feeds/lidl";
	public static final int NOT_YET_SCRAPED = -1;

}

package com.robertabela.rss;

import com.rometools.rome.feed.rss.Guid;

import twitter4j.Status;

public class GUID extends Guid {

	private static final long serialVersionUID = 8983872035491726963L;

	private static final String PREFIX = "missing_rss_feed:";

	public GUID(String value) {
		super();
		this.setGUIDValue(value);
	}

	public GUID(Status tweetStatus) {
		super();
		this.setGUIDValue(Long.toString(tweetStatus.getId()));
	}
	
	private void setGUIDValue(String value) {
		String guidValue = PREFIX + value;
		this.setValue(guidValue.replace(" ", ""));
	}
}

package com.robertabela.twittertools;

import com.rometools.rome.feed.rss.Item;

import twitter4j.Status;

public interface TweetParser {
	
	public Item parse(Status tweetStatus);

}

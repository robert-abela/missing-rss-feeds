package com.robertabela.twittertools;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rometools.rome.feed.rss.Item;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Twitter {
	private static final int NUM_OF_TWEETS = 25;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String twitterUserName;
	private TweetParser parser;
	
	private twitter4j.Twitter twitter;
	private long lastTweet;
	private String twitterOauthConsumerKey;
	private String twitterOauthConsumerSecret;
	private String twitterOauthAccessToken;
	private String twitterOauthAccessSecret;
	
	private List<Item> cachedItems;
	
	public Twitter(String twitterUserName, TweetParser parser) throws IllegalStateException {
		this.twitterUserName = twitterUserName;
		this.parser = parser;
		
		logger.info("Loading Twitter API variables from environment...");
		twitterOauthConsumerKey = System.getenv().get("OAUTHCONSUMERKEY");
	    if (twitterOauthConsumerKey == null)
	    	throw new IllegalStateException("Failed to load environment variable: OAUTHCONSUMERKEY");
	    
	    twitterOauthConsumerSecret = System.getenv().get("OAUTHCONSUMERSECRET");
	    if (twitterOauthConsumerSecret == null)
	    	throw new IllegalStateException("Failed to load environment variable: OAUTHCONSUMERSECRET");

	    twitterOauthAccessToken = System.getenv().get("OAUTHACCESSTOKEN");
	    if (twitterOauthAccessToken == null)
	    	throw new IllegalStateException("Failed to load environment variable: OAUTHACCESSTOKEN");

	    twitterOauthAccessSecret = System.getenv().get("OAUTHACCESSSECRET");
	    if (twitterOauthAccessSecret == null)
	    	throw new IllegalStateException("Failed to load environment variable: OAUTHACCESSSECRET");
	    
		// gets Twitter instance with configured credentials
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
			.setOAuthConsumerKey(twitterOauthConsumerKey)
			.setOAuthConsumerSecret(twitterOauthConsumerSecret)
			.setOAuthAccessToken(twitterOauthAccessToken)
			.setOAuthAccessTokenSecret(twitterOauthAccessSecret);
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();

		cachedItems = getTweets(new Paging(1, NUM_OF_TWEETS));
	}
	
	public void updateTweets() {
		logger.info("Updating tweets, starting from " + lastTweet);
		List<Item> freshTweets = getTweets(new Paging(1, NUM_OF_TWEETS, lastTweet));
		
		if (freshTweets.isEmpty()) {
			logger.debug("No new tweets since last check");
			return;
		}
			
		logger.debug("Updating tweets, returned " + freshTweets.size());
		
		synchronized (cachedItems) {
			cachedItems.addAll(freshTweets);
			if (cachedItems.size() <= NUM_OF_TWEETS)
				return;
			
			cachedItems = cachedItems.subList(0, NUM_OF_TWEETS-1);
		}
		
		logger.debug("Exceeded maximum number of tweets, removing older ones");
	}
	
	public List<Item> getTweets() {
		synchronized (cachedItems) {
			return cachedItems;
		}
	}
	
	private List<Item> getTweets(Paging page) {
		try {
			List<Item> tweets = new ArrayList<Item>();
			List<Status> statuses = twitter.getUserTimeline(twitterUserName, page);
			for (Status tweetStatus : statuses) {
				logger.debug(tweetStatus.getId() + " - " + tweetStatus.getText());
				
				Item parsedItem = parser.parse(tweetStatus);
				if (parsedItem != null) {
					tweets.add(parsedItem);
					if (tweetStatus.getId() > lastTweet)
						lastTweet = tweetStatus.getId();
				}
			}
			return tweets;
		}
		catch (TwitterException e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}

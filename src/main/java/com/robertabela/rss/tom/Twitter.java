package com.robertabela.rss.tom;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.conf.ConfigurationBuilder;

public class Twitter {
	private static final int NUM_OF_TWEETS = 25;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private twitter4j.Twitter twitter;
	private long lastTweet;
	private String twitterUserName;
	private String twitterOauthConsumerKey;
	private String twitterOauthConsumerSecret;
	private String twitterOauthAccessToken;
	private String twitterOauthAccessSecret;
	
	private List<Item> cachedItems;
	
	public Twitter(String twitterUserName) throws IllegalStateException {
		this.twitterUserName = twitterUserName;
		
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
			logger.debug("Nothing new returned");
			return;
		}
			
		logger.debug("Updating tweets, returned " + freshTweets.size());
		cachedItems.addAll(freshTweets);		
		if (cachedItems.size() > NUM_OF_TWEETS) {
			cachedItems = cachedItems.subList(0, NUM_OF_TWEETS-1);
			logger.debug("Exceeded maximum number of tweets, shortening list");
		}
	}
	
	public List<Item> getTweets() {
		return cachedItems;
	}
	
	private List<Item> getTweets(Paging page) {
		
		try {
			List<Item> tweets = new ArrayList<Item>();
			List<Status> statuses = twitter.getUserTimeline(twitterUserName, page);
			for (Status tweetStatus : statuses) {
				logger.debug(tweetStatus.getId() + " - " + tweetStatus.getText());
				Item newsItem = new Item();
				newsItem.setAuthor(tweetStatus.getUser().getScreenName());
				String title = tweetStatus.getText();
				if (title.contains("https"))
					title = title.substring(0, title.indexOf("https"));
				newsItem.setTitle(title);
				newsItem.setPubDate(tweetStatus.getCreatedAt());
				for (URLEntity url : tweetStatus.getURLEntities())
					newsItem.setLink(url.getURL());
				
				Description description = new Description();
				description.setType(Content.HTML);
				String descStr = String.format("<a href=\"%s\">%s</a>", newsItem.getLink(), title);
				description.setValue(descStr);
				newsItem.setDescription(description);
				
				tweets.add(newsItem);
				if (tweetStatus.getId() > lastTweet)
					lastTweet = tweetStatus.getId();
			}
			return tweets;
		}
		catch (TwitterException e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}

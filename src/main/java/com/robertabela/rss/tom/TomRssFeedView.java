package com.robertabela.rss.tom;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import com.robertabela.rss.Constants;
import com.robertabela.twittertools.Twitter;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Image;
import com.rometools.rome.feed.rss.Item;

@Component
@EnableScheduling
public class TomRssFeedView extends AbstractRssFeedView {

	private Twitter twitter;
	private Image img;
	
	public TomRssFeedView() {
		img = new Image();
		img.setTitle("TOM Logo");
		img.setUrl(Constants.TOM_LOGO);
		img.setLink(Constants.TOM_LOGO);
		img.setWidth(185);
		img.setHeight(185);
	}

	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest req) {
		feed.setTitle("Times of Malta Twitter feed");
		feed.setDescription("Times of Malta: all the tweets by @TheTimesofMalta");
		feed.setLink(Constants.TOM_BASE_URL);
		feed.setImage(img);
	}

	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return twitter.getTweets();
	}

	/**
	 * Twitter update runs automatically every 30 mins (first time 30 mins)
	 */
	@Scheduled(fixedRate=1000*60*30, initialDelay=1000*60*30)
    public void scrapeNewOffers() {
		logger.info("Scheduled hourly Twitter update...");
		twitter.updateTweets();
    }

	/**
	 * On first run after boot, initialise twitter client
	 */
	@PostConstruct
	private void firstRun() {
		try {
			twitter = new Twitter(Constants.TOM_TWITTER_HANDLE, new ToMTweetParser());
		} catch (IllegalStateException e) {
			logger.error(e.getMessage());
		}
	}
}

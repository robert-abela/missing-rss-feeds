package com.robertabela.rss.lidl;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Image;
import com.rometools.rome.feed.rss.Item; 

@Component
@EnableScheduling
public class LidlRssFeedView extends AbstractRssFeedView {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Offers cachedOffers = new Offers();

	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest req) {
		feed.setTitle("Lidl Non-Food Offers");
		feed.setDescription("Lidl Malta: non-food offers for this and next week");
		feed.setLink(Constants.BASE_URL);

		Image img = new Image();
		img.setUrl("https://raw.githubusercontent.com/robert-abela/missing-rss-feeds/master/src/main/resources/public/images/lidl.png");
		img.setTitle("LIDL Logo");
		img.setHeight(100);
		img.setWidth(100);
		feed.setImage(img);
	}

	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest req, HttpServletResponse res) {
		return cachedOffers.getProducts();
	}
	
	/**
	 * Scraping runs automatically on Monday and Thursday at 4am, CET
	 */
    @Scheduled(cron="0 0 4 * * MON,THU", zone="Europe/Rome")
    public void scrapeNewOffers() {
		logger.info("It's offer day, fresh scrape is needed...");
		cachedOffers.scrapeNewOffers();
    }
    
    @PostConstruct
    private void firstRun() {
		// First run after boot, generate list
		logger.info("First scrape during webapp boot");
		cachedOffers.scrapeNewOffers();
    }
}

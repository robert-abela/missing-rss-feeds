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

	private Offers cachedOffers;
	private Image img;
	
	public LidlRssFeedView() {
		cachedOffers = new Offers();

		img = new Image();
		img.setTitle("LIDL Logo");
		img.setUrl(Constants.LIDL_LOGO);
		img.setLink(Constants.LIDL_LOGO);
		img.setWidth(142);
		img.setHeight(142);
	}

	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest req) {
		feed.setTitle("LIDL Non-Food Offers");
		feed.setDescription("LIDL Malta: non-food offers for this and next week");
		feed.setLink(Constants.BASE_URL);
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

    /**
     * On first run after boot, perform scraping to generate product list
     */
    @PostConstruct
    private void firstRun() {
		logger.info("First scrape during webapp boot");
		cachedOffers.scrapeNewOffers();
    }
}

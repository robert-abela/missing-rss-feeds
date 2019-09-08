package com.robertabela.rss.lidl;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.THURSDAY;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Item; 

@Component
public class LidlRssFeedView extends AbstractRssFeedView {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Offers cachedOffers = null;

	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest req) {
		feed.setTitle("Lidl Non-Food Offers");
		feed.setDescription("Lidl Malta: non-food offers for this and next week");
		feed.setLink(Constants.BASE_URL);

		/*Image img = new Image();
		img.setUrl("https://raw.githubusercontent.com/robert-abela/missing-rss-feeds/master/src/main/resources/Lidl_logo.png");
		img.setTitle("LIDL Logo");
		img.setHeight(128);
		img.setWidth(128);
		feed.setImage(img);*/
	}

	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest req, HttpServletResponse res) {
		if (cachedOffers == null) {
			// First run after boot, generate list
			logger.info("First list generated after webapp boot");
			cachedOffers = new Offers();
			cachedOffers.scrapeNewOffers();
		} else {
			switch (Calendar.getInstance(Constants.MT_TIMEZOME).get(DAY_OF_WEEK)) {
			case MONDAY:
			case THURSDAY:
				// Scrape on first request on Monday and Thursday
				logger.info("It's an offer day, scrape if needed...");
				cachedOffers.scrapeNewOffers();
				break;
			default:
				// Don't do anything if it's not Monday or Thursday
				logger.info("It's not offer day, skipping...");
				break;
			}
		}

		return cachedOffers.getProducts();
	}
}

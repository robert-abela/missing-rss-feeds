package com.robertabela.rss.lidl;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.THURSDAY;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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

	private static final TimeZone MT_TIMEZOME = TimeZone.getTimeZone("CEST");

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private List<Item> cachedList = null;
	private int lastScrapeDayOfYear = -1;

	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest req) {
		feed.setTitle("Lidl non-food offers");
		feed.setDescription("Lidl Malta: non-food offers for this and next week");
		feed.setLink(Offers.BASE_URL);

		/*Image img = new Image();
		img.setUrl("https://raw.githubusercontent.com/robert-abela/missing-rss-feeds/master/src/main/resources/Lidl_logo.png");
		img.setTitle("LIDL Logo");
		img.setHeight(128);
		img.setWidth(128);
		feed.setImage(img);*/
	}

	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest req, HttpServletResponse res) {
		if (cachedList == null) {
			// First run after boot, generate list
			logger.info("First list generated after webapp boot");
			generateList();
		} else {
			Calendar todayCET = Calendar.getInstance(MT_TIMEZOME);
			switch (todayCET.get(DAY_OF_WEEK)) {
			case MONDAY:
			case THURSDAY:
				// Generate list on first request on Monday and Thursday
				logger.info("First request on offer days");

				int todayDayOfYear = todayCET.get(DAY_OF_YEAR);
				if (lastScrapeDayOfYear < todayDayOfYear)
					generateList();
				else
					logger.info("Already scraped today, skipping...");
				break;
			default:
				// Don't do anything if it's not Monday or Thursday
				logger.info("It's not offer day, skipping...");
				break;
			}
		}

		return cachedList;
	}

	private void generateList() {
		logger.info("Generating list...");
		cachedList = new Offers().getItems(cachedList);
		lastScrapeDayOfYear = Calendar.getInstance(MT_TIMEZOME).get(DAY_OF_YEAR);
	}
}

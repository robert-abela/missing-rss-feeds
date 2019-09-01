package com.robertabela.rss.lidl;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.THURSDAY;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Item;

@Component
public class LidlRssFeedView extends AbstractRssFeedView {
	
	private List<Item> cachedList = null;
	private int lastScrapeDayOfYear = -1;

	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest req) {
		feed.setTitle("Lidl non-food offers");
		feed.setDescription("Lidl Malta: non-food offers for this and next week");
		feed.setLink("https://github.com/robert-abela/missing-rss-feeds");
	}

	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest req, HttpServletResponse res) {
		if (cachedList == null) {
			//First run after boot, generate list
			System.out.println("First run after boot");
			generateList();
		}
		else {
			Calendar today = Calendar.getInstance();
			switch (today.get(DAY_OF_WEEK)) {
			case MONDAY:
			case THURSDAY:
				//Generate list on first request on Monday and Thursday
				System.out.println("First request on offer days");
				
				int todayDayOfYear = today.get(DAY_OF_YEAR);
				if (lastScrapeDayOfYear < todayDayOfYear)
					generateList();
				else
					System.out.println("Already scraped today, skipping...");
				break;
			default:
				//Don't do anything if it's not Monday or Thursday
				System.out.println("It's not offer day, skipping...");
				break;
			}
		}
		
		return cachedList;
	}
	
	private void generateList() {
		System.out.println("Generating list...");
		cachedList = new Offers().getItems();
		lastScrapeDayOfYear = Calendar.getInstance().get(DAY_OF_YEAR);
	}
}

package com.robertabela.rss.lidl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robertabela.rss.Constants;
import com.rometools.rome.feed.rss.Item;

public class Page {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private int id;
	private String url;
	private String title;
	private String startingDate;
	private List<Item> products;

	public Page(String url) {
		this.url = url;
		this.products = new ArrayList<>();
		
		String idNum = this.url.substring(this.url.lastIndexOf("/c") + 2); 
		int secondSash = idNum.indexOf('/');
		if (secondSash != -1)
			idNum = idNum.substring(0, secondSash);
		
		this.id = Integer.parseInt(idNum);
	}

	public List<Item> scrapeProducts() {
		logger.info("Getting products in: " + url);

		try {
			Document doc = Jsoup.connect(url).get();
			
			try {
				title = doc.title();
				title = title.substring(0, title.indexOf("from")-1) ;
			}
			catch (IndexOutOfBoundsException e) {
				logger.debug("Failed to trim title, using the oroginal");
			}
			
			Elements productTiles = doc.getElementsByClass("product");
			for (Element tile : productTiles) {
				try {
					products.add(new ProductItem(this, tile));
				}
				catch (TeaserException e) {
					logger.debug("Ignoring teaser tile...");
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		return products;
	}

	public String getTitle() {
		return title;
	}
	
	public int getId() {
		return id;
	}

	public String getDate(Element tile) {
		if (startingDate == null) {
			try {
				String rawDate = tile.getElementsByClass("ribbon__item--primary").text();
				int day = Integer.parseInt(rawDate.substring(5, 7));
				int month = Integer.parseInt(rawDate.substring(8, 10)) - 1;
				Calendar pubCal = new GregorianCalendar(); //use current year (will not work end of Dec for Jan offers)
				pubCal.set(Calendar.DAY_OF_MONTH, day);
				pubCal.set(Calendar.MONTH, month);
				startingDate = Constants.DATE_FORMAT.format(pubCal.getTime());
			}
			catch (NumberFormatException | StringIndexOutOfBoundsException e) {
				logger.error("Failed to read date: " + e.getMessage());
			}
		}
		
		return startingDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Page other = (Page) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
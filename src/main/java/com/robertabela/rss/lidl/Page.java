package com.robertabela.rss.lidl;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

import com.rometools.rome.feed.rss.Item;

public class Page {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd/MM/yyyy");

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private int id;
	private String url;
	private String title;
	private String startingDate;
	private List<Item> products;

	public Page(String url) {
		this.url = url;
		this.products = new ArrayList<>();
		String idNum = this.url.substring(this.url.indexOf("?id=") + 4, this.url.indexOf("&"));
		this.id = Integer.parseInt(idNum);
	}

	public List<Item> getProducts(List<Item> cachedProducts) {
		logger.info("Getting products in: " + url);

		try {
			Document doc = Jsoup.connect(url).get();
			
			title = doc.title();
			try {
				title = title.substring(0, title.indexOf("from")-1) ;
			}
			catch (IndexOutOfBoundsException e) {
				logger.debug("Failed to trim title, using the oroginal");
			}
			
			Elements productTiles = doc.getElementsByClass("product");
			for (Element tile : productTiles) {
				try {
					ProductItem p = new ProductItem(this, tile);
					if (!cachedProducts.contains(p)) {
						p.readInfo();
						products.add(p);
					}
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
				Calendar pubCal = new GregorianCalendar(2019, month, day, 0, 0);
				startingDate = DATE_FORMAT.format(pubCal.getTime());
			}
			catch (NumberFormatException | StringIndexOutOfBoundsException e) {
				logger.error("Failed to read date: " + e.getMessage());
			}
		}
		
		return startingDate;
	}
}
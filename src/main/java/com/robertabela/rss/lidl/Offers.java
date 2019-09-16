package com.robertabela.rss.lidl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rometools.rome.feed.rss.Item;

public class Offers {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private List<Page> cachedPages;
	private List<Item> cachedProducts;

	public Offers() {
		this.cachedPages = new ArrayList<Page>();
		this.cachedProducts = new ArrayList<Item>();
	}

	public List<Item> getProducts() {
		return cachedProducts;
	}

	public void scrapeNewOffers() {
		try {
			boolean productsAdded = false;
			Document doc = Jsoup.connect(Constants.START_URL).get();
			Elements pages = doc.getElementsByClass("theme__item");

			for (Element page : pages) {
				Page foundPage = new Page(Constants.BASE_URL+page.attr("href"));
				if (!cachedPages.contains(foundPage)) {
					logger.debug("Found new page to scrape: " + foundPage.getId());
					productsAdded = true;
					cachedPages.add(foundPage);
					cachedProducts.addAll(foundPage.scrapeProducts());
				}
			}
			
			if (!productsAdded)
				logger.warn("Scrape failed to add any new products");
		} catch (IOException e) {
			logger.error("Scraping new offers failed: "+e.getMessage());
		}
	}
}

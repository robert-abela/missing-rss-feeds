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

	public static final String BASE_URL = "https://www.lidl.com.mt";
	private static final String START_URL = BASE_URL+"/en/non-food.htm";
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private List<Page> offerPages;

	public Offers() {
		this.offerPages = new ArrayList<Page>();

		try {
			Document doc = Jsoup.connect(START_URL).get();
			Elements pages = doc.getElementsByClass("theme__item");

			for (Element page : pages) {
				offerPages.add(new Page(BASE_URL+page.attr("href")));
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public List<Item> getItems(List<Item> cachedProducts) {
		if (cachedProducts == null)
			cachedProducts = new ArrayList<Item>();
		
		for (Page page : offerPages) {
			cachedProducts.addAll(page.getProducts(cachedProducts));
		}
		return cachedProducts;
	}
}
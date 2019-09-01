package com.robertabela.rss.lidl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.rometools.rome.feed.rss.Item;

public class Offers {

	private List<Page> offerPages;
	public static final String BASE_URL = "https://www.lidl.com.mt";
	private static final String START_URL = BASE_URL+"/en/non-food.htm";

	public Offers() {
		this.offerPages = new ArrayList<Page>();

		try {
			Document doc = Jsoup.connect(START_URL).get();
			Elements pages = doc.getElementsByClass("theme__item");

			for (Element page : pages) {
				offerPages.add(new Page(BASE_URL+page.attr("href")));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Item> getItems() {
		List<Item> products = new ArrayList<Item>();
		for (Page page : offerPages) {
			products.addAll(page.getProducts());
		}
		return products;
	}
}
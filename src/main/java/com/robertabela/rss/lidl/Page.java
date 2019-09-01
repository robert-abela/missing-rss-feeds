package com.robertabela.rss.lidl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.rometools.rome.feed.rss.Item;

public class Page {

	private int id;
	private String url;
	private String title;
	private Date startingDate;
	private List<Item> products;

	public Page(String url) {
		this.url = url;
		this.products = new ArrayList<>();
		String idNum = this.url.substring(this.url.indexOf("?id=") + 4, this.url.indexOf("&"));
		this.id = Integer.parseInt(idNum);
	}

	public List<Item> getProducts() {
		System.out.println("Getting products in: " + url);

		try {
			Document doc = Jsoup.connect(url).get();
			
			title = doc.title();
			try {
				//try to trim title
				title = title.substring(0, title.indexOf("from")-1) ;
			}
			catch (IndexOutOfBoundsException e) {/*leave as is if anything happens*/}
			
			Elements productTiles = doc.getElementsByClass("product");

			for (Element tile : productTiles) {
				try {
					products.add(new ProductItem(this, tile));
				}
				catch (TeaserException e) {
					System.out.println("Ignoring teaser tile...");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return products;
	}

	public String getTitle() {
		return title;
	}
	
	public int getId() {
		return id;
	}


	public Date getDate(Element tile) {
		if (startingDate == null) {
			String rawDate = tile.getElementsByClass("ribbon__item--primary").text();
			int day = Integer.parseInt(rawDate.substring(5, 7));
			int month = Integer.parseInt(rawDate.substring(8, 10)) - 1;
			Calendar pubCal = new GregorianCalendar(2019, month, day, 0, 0);
			startingDate = pubCal.getTime();
			System.out.println(startingDate);
		}
		
		return startingDate;
	}
}
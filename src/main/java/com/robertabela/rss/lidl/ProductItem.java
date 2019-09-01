package com.robertabela.rss.lidl;

import java.util.Date;

import org.jsoup.nodes.Element;

import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;

public class ProductItem extends Item {

	private static final long serialVersionUID = 5166789136384820420L;
	
	public ProductItem(Page page, Element tile) throws TeaserException {
		super();
		
		if (tile.getElementsByClass("productgrid__teaser-item").size() > 0)
			throw new TeaserException();
		
		String itemTitle = tile.getElementsByClass("product__title").text();
		setTitle(itemTitle);

		setAuthor("robert-abela/missing-rss-feeds/lidl");
		
		setLink(Offers.BASE_URL+tile.getElementsByClass("product__body").attr("href"));
		
		Description description = new Description();
		description.setType(Content.HTML);
		String descStr = String.format("%s: %s<br>Price: €%s", page.getTitle(), itemTitle, 
				tile.getElementsByClass("pricefield__price").attr("content"));
		description.setValue(descStr);
		setDescription(description);
		
		try {
			setPubDate(page.getDate(tile));
		}
		catch (NumberFormatException | StringIndexOutOfBoundsException e) {
			setPubDate(new Date());	
			System.err.println(e);
		}
	}
}

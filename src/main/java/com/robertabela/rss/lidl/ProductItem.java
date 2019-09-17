package com.robertabela.rss.lidl;

import java.util.Date;

import org.jsoup.nodes.Element;

import com.robertabela.rss.Constants;
import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;

public class ProductItem extends Item {

	private static final long serialVersionUID = 5166789136384820420L;
	
	public ProductItem(Page page, Element tile) throws TeaserException {
		if (tile.getElementsByClass("productgrid__teaser-item").size() > 0)
			throw new TeaserException();

		String relativeLink = tile.getElementsByClass("product__body").attr("href");
		String itemTitle = tile.getElementsByClass("product__title").text();
		setTitle(itemTitle);
		setAuthor(Constants.AUTHOR);
		setLink(Constants.LIDL_BASE_URL+relativeLink);
		
		Element bigDiv = tile.getElementsByClass("product__image").get(0);
		Element innerDiv = bigDiv.getElementsByTag("div").get(0);
		Element image = innerDiv.getElementsByTag("img").get(0);
		String imageUrl = image.attr("src").replace("-lazy", "");
		
		Description description = new Description();
		description.setType(Content.HTML);
		String descStr = String.format("<img src=\"%s\"><br>%s: %s<br>Price: €%s<br>From: %s", 
				imageUrl,
				page.getTitle(), 
				itemTitle, 
				tile.getElementsByClass("pricefield__price").attr("content"),
				page.getDate(tile));
		description.setValue(descStr);
		setDescription(description);
		setPubDate(new Date());		
	}
}

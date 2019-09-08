package com.robertabela.rss.lidl;

import java.util.Date;

import org.jsoup.nodes.Element;

import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;

public class ProductItem extends Item {

	private static final long serialVersionUID = 5166789136384820420L;
	private static final String AUTHOR = "robert-abela/missing-rss-feeds/lidl";
	
	private Page page;
	private Element tile;
	private String relativeLink;
	
	public ProductItem(Page page, Element tile) throws TeaserException {
		super();
		
		if (tile.getElementsByClass("productgrid__teaser-item").size() > 0)
			throw new TeaserException();

		this.tile = tile;
		this.page = page;
		relativeLink = tile.getElementsByClass("product__body").attr("href");
	}
		
	public void readInfo() {
		String itemTitle = tile.getElementsByClass("product__title").text();
		setTitle(itemTitle);
		setAuthor(AUTHOR);
		setLink(Offers.BASE_URL+relativeLink);
		
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((relativeLink == null) ? 0 : relativeLink.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductItem other = (ProductItem) obj;
		if (relativeLink == null) {
			if (other.relativeLink != null)
				return false;
		} else if (!relativeLink.equals(other.relativeLink))
			return false;
		return true;
	}
}

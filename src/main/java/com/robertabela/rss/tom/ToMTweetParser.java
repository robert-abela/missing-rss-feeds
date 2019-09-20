package com.robertabela.rss.tom;

import java.io.IOException;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robertabela.rss.GUID;
import com.robertabela.twittertools.TweetParser;
import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;

import twitter4j.Status;
import twitter4j.URLEntity;

public class ToMTweetParser implements TweetParser {
	
	private static final String DEFAULT_IMAGE = "https://raw.githubusercontent.com/robert-abela/missing-rss-feeds/master/src/main/resources/public/images/tom.png";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Item parse(Status tweetStatus) {
		
		Item newsItem = new Item();
		newsItem.setGuid(new GUID(tweetStatus));
		newsItem.setPubDate(tweetStatus.getCreatedAt());
		newsItem.setAuthor("TimesofMalta");
		
		for (URLEntity url : tweetStatus.getURLEntities())
			newsItem.setLink(url.getURL());
		
		fetchInfoFromSource(newsItem);
		
		return newsItem;
	}
	
	private void fetchInfoFromSource(Item newsItem) {
		try {
			String twtURL = newsItem.getLink();
			Document bitlyDoc = Jsoup.connect(twtURL).get();
			String bitlyURL = bitlyDoc.title();
			logger.debug("Expanding: " + twtURL + "->" + bitlyURL);

			Response response = Jsoup.connect(bitlyURL).followRedirects(false).execute();
			String tomURL = response.header("location");
			logger.debug("Expanding: " + bitlyURL + "->" + tomURL);

			Document timesDoc = Jsoup.connect(tomURL).get();
			newsItem.setTitle(timesDoc.title());
			
			Element article = timesDoc.getElementsByClass("ar-Article_Content").get(0);
			Elements metaTags = timesDoc.getElementsByTag("meta");
			String mainIgmURL = DEFAULT_IMAGE;

			for (Element metaTag : metaTags) {
			  String property = metaTag.attr("property");

			  if ("og:image".equals(property)) {
				  mainIgmURL = metaTag.attr("content");
				  break;
			  }
			}

			String descStr = String.format("<img src=\"%s\" />%s", mainIgmURL, article.html());
			Description description = new Description();
			description.setType(Content.HTML);
			description.setValue(descStr);
			newsItem.setDescription(description);
		}
		catch (IOException | IndexOutOfBoundsException e) {
			logger.error(e.getMessage());
		}
	}
}

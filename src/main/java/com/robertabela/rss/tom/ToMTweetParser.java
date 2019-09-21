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
	private static final String NO_NAMED_AUTHOR = "TimesofMalta";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Item parse(Status tweetStatus) {

		Item newsItem = new Item();
		newsItem.setGuid(new GUID(tweetStatus));
		newsItem.setPubDate(tweetStatus.getCreatedAt());

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
			newsItem.setAuthor(fetchAuthor(timesDoc));
			newsItem.setDescription(fetchDescription(timesDoc));
		}
		catch (IOException | IndexOutOfBoundsException e) {
			logger.error(e.getMessage());
		}
	}
	
	private Description fetchDescription(Document timesDoc) {
		Element article = timesDoc.getElementsByClass("ar-Article_Content").get(0);

		String descStr = String.format("<img src=\"%s\" />%s", fetchImage(timesDoc), article.html());
		Description description = new Description();
		description.setType(Content.HTML);
		description.setValue(descStr);
		return description;
	}

	private String fetchImage(Document timesDoc) {
		Elements metaTags = timesDoc.getElementsByTag("meta");

		for (Element metaTag : metaTags) {
			String property = metaTag.attr("property");

			if ("og:image".equals(property)) {
				return metaTag.attr("content");
			}
		}

		return DEFAULT_IMAGE;
	}

	private String fetchAuthor(Document timesDoc) {
		Elements scriptTags = timesDoc.getElementsByTag("script");
		final String JSON_NAME = "name\":\"";

		for (Element scriptTag : scriptTags) {
			String property = scriptTag.attr("id");

			if ("author-ld".equals(property)) {
				String authorData = scriptTag.html();
				int start = authorData.indexOf(JSON_NAME) + JSON_NAME.length();
				int end = authorData.indexOf("\"", start);
				try {
					return authorData.substring(start, end).trim();
				}
				catch (IndexOutOfBoundsException e) {
					return NO_NAMED_AUTHOR;
				}
			}
		}
		
		return NO_NAMED_AUTHOR;
	}
}

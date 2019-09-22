package com.robertabela.rss;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import com.robertabela.rss.lidl.LidlRssFeedView;
import com.robertabela.rss.tom.TomRssFeedView;

@RestController
public class RssFeedContoller {

	@Autowired
	private LidlRssFeedView lidlView;

	@Autowired
	private TomRssFeedView tomView;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String feedApiKey;

	@GetMapping("/lidl")
	public View getLidlFeed(@RequestParam("key") String key, HttpServletResponse resp) throws IOException {
		return checkKey(lidlView, key, resp);
	}

	@GetMapping("/tom")
	public View getTomFeed(@RequestParam("key") String key, HttpServletResponse resp) throws IOException {
		return checkKey(tomView, key, resp);
	}

	private View checkKey(View view, String key, HttpServletResponse resp) throws IOException {
		if (feedApiKey == null) {
	        resp.sendError(SC_INTERNAL_SERVER_ERROR, "Failed to load environment variable: APIKEY");
	        return null;
		}
		else if (!feedApiKey.equals(key)) {
	        resp.sendError(SC_UNAUTHORIZED, "Key sent in request not accepted");
	        return null;
		}
		else {
			return view;
		}
	}

	@PostConstruct
	private void firstRun() {
		feedApiKey = System.getenv().get("APIKEY");
		if (feedApiKey == null)
			logger.error("Failed to load environment variable: APIKEY");
	}
}

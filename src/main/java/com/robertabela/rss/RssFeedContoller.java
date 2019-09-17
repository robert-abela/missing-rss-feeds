package com.robertabela.rss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/lidl")
    public View getLidlFeed() {
        return lidlView;
    }

    @GetMapping("/tom")
    public View getTomFeed() {
        return tomView;
    }
}

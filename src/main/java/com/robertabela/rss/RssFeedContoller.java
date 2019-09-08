package com.robertabela.rss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import com.robertabela.rss.lidl.LidlRssFeedView;

@RestController
public class RssFeedContoller {
 
    @Autowired
    private LidlRssFeedView view;
     
    @GetMapping("/lidl")
    public View getFeed() {
        return view;
    }
}
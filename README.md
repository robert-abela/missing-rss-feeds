# missing-rss-feeds v2.1.3
RSS feeds for sites that do not officially provide them. This project was developed to add missing feeds to my RSS feed library.
It was also my first project using Spring and Bootstrap, don't expect it to be a showcase of best practices. 
All images, text and links are the ones found in the original web site.

A live version is available at: [https://missing-rss-feed.herokuapp.com/](https://missing-rss-feed.herokuapp.com/)

## LIDL Malta non-food offers
LIDL Malta updates [non-food offers](https://www.lidl.com.mt/en/non-food.htm) every Monday and Thursday but do not offer any RSS feed to make it easier for potential customers to keep themselves updated. This feed aims to fill in that gap by scraping the LIDL offers and making them available as an RSS feed. 

The RSS feed can be accessed at: https://missing-rss-feed.herokuapp.com/lidl?key=XXX

## @TheTimesofMalta Twitter
[The Times of Malta](https://www.timesofmalta.com) maintains a Twitter handle named [@TheTimesofMalta](https://twitter.com/TheTimesofMalta) which it uses to tweet some of the news items on its web site. 
In the past there used to be an RSS feed which has now been turned off. This project aims to fill that gap by converting the tweet stream into an RSS feed. It also fetches whole article and main image from Times of Malta after following t.co and bit.ly redirects.

The RSS feed can be accessed at: https://missing-rss-feed.herokuapp.com/tom?key=XXX

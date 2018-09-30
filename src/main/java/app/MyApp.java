package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webcrawler.Crawler;
import webcrawler.DocumentFetcherFactory;
import webcrawler.SiteLinks;

public class MyApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyApp.class);

    public static void main(String[] args) {
        SiteLinks crawl = new Crawler(new DocumentFetcherFactory()).crawl("https://wiprodigital.com/");
        LOGGER.info("External links");
        for (String key : crawl.getExternalLinks().keySet()) {
            LOGGER.info(".............On page {} we have external links:", key);

            for (String link : crawl.getExternalLinks().get(key)) {
                LOGGER.info("Link: {}", link);
            }
        }

        for (String key : crawl.getInternalLinks().keySet()) {
            LOGGER.info(".............On page {} we have internal links:", key);
            for (String link : crawl.getInternalLinks().get(key)) {
                LOGGER.info("Link: {}", link);
            }
        }
    }
}

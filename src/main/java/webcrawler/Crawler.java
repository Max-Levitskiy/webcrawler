package webcrawler;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webcrawler.exception.PageFetchException;
import webcrawler.exception.PageNotFoundException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class Crawler {
    public static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    private DocumentFetcherFactory documentFetcherFactory;
    private Set<String> processedPages = new HashSet<>();

    public Crawler(DocumentFetcherFactory documentFetcherFactory) {
        this.documentFetcherFactory = documentFetcherFactory;
    }

    public SiteLinks crawl(String url) {
        try {
            LOGGER.info("Crawl url: {}", url);
            SiteLinks result = new SiteLinks();
            String currentPage = "/";
            crawlPage(url, result, currentPage);
            return result;
        } finally {
            processedPages.clear();
        }
    }

    private void crawlPage(String parseUrl, SiteLinks result, String page) {
        LOGGER.info("Process page {}", page);
        Elements links = documentFetcherFactory.create(parseUrl).fetch(page).select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            LOGGER.info("Link {}", href);
            if (!shouldSkip(href)) {
                if (isExternal(href, parseUrl)) {
                    result.getExternalLinks().put(page, href);
                    LOGGER.info("...added to external");
                } else {
                    String value = isUrl(href) ? toUrl(href).getPath() : href;
                    result.getInternalLinks().put(page, value);
                    LOGGER.info("...added to internal");
                }
            }
        }
        LOGGER.info("Process internal pages");
        for (String internalPage : result.getInternalLinks().get(page)) {
            if (!result.getInternalLinks().containsKey(internalPage)) {
                try {
                    crawlPage(parseUrl, result, internalPage);
                } catch (PageNotFoundException e) {
                    LOGGER.warn("There is missed page {}", internalPage);
                } catch (PageFetchException e) {
                    LOGGER.error("Can't parse page {}", internalPage);
                }
            }
        }

    }

    private boolean shouldSkip(String href) {
        return href.startsWith("mailto:");
    }

    private boolean isExternal(String url, String parseUrl) {
        if (isUrl(url) && isUrl(parseUrl)) {
            return !(toUrl(url).getHost().equals(toUrl(parseUrl).getHost()));
        }
        return false;
    }

    private boolean isUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private URL toUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new PageFetchException(e);
        }
    }
}

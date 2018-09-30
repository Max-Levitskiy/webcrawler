package webcrawler;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import webcrawler.exception.PageFetchException;
import webcrawler.exception.PageNotFoundException;

import java.io.IOException;

public class DocumentFetcher {
    private String baseUrl;

    public DocumentFetcher(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Document fetch(String page) {
        try {
            return Jsoup.connect(baseUrl + page).get();
        } catch (HttpStatusException e) {
            if (e.getStatusCode() == HTTP_NOT_FOUND) {
                throw new PageNotFoundException(e);
            } else {
                throw new PageFetchException(e);
            }
        } catch (IOException e) {
            throw new PageFetchException(e);
        }
    }
}

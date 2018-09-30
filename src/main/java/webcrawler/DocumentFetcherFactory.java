package webcrawler;

public class DocumentFetcherFactory {
    public DocumentFetcher create(String url) {
        return new DocumentFetcher(url);
    }
}

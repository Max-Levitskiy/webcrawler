package webcrawler.exception;

public class PageNotFoundException extends PageFetchException {
    public PageNotFoundException(Throwable cause) {
        super(cause);
    }
}

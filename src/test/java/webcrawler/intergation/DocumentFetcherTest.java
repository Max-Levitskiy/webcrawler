package webcrawler.intergation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import webcrawler.DocumentFetcher;
import webcrawler.exception.PageNotFoundException;

class DocumentFetcherTest {

    private DocumentFetcher documentFetcher;

    @BeforeEach
    void setUp() {
        documentFetcher = new DocumentFetcher("https://google.com");
    }

    @Test
    void shouldReturnSiteContent() {
        Document document = documentFetcher.fetch("/");
        Elements title = document.select("title");
        assertThat(title.get(0).text()).contains("Google");
    }

    @Test
    void shouldThrowPageNotFoundOnUnknownPage() {
        assertThatExceptionOfType(PageNotFoundException.class).isThrownBy(() -> documentFetcher.fetch("/SomeUnknownPage"));
    }
}
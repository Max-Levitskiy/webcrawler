package webcrawler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import webcrawler.exception.PageNotFoundException;

import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class CrawlerTest {
    private Crawler crawler;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DocumentFetcher documentFetcher;
    @Mock
    private DocumentFetcherFactory documentFetcherFactory;

    @BeforeEach
    void setUp() {
        when(documentFetcherFactory.create(anyString())).thenReturn(documentFetcher);
        crawler = new Crawler(documentFetcherFactory);
    }

    @Nested
    class Empty {
        @Test
        void shouldStartFromRoot() {
            crawler.crawl("");
            verify(documentFetcher).fetch("/");
        }

        @Test
        void shouldBeEmptyForEmptyContent() {
            SiteLinks crawl = crawler.crawl("");

            //expect
            assertThat(crawl).isNotNull();
            assertThat(crawl.getInternalLinks().keySet()).isEmpty();
            assertThat(crawl.getExternalLinks().keySet()).isEmpty();
        }
    }

    @Nested
    class Links {
        static final String DOMAIN = "http://mydomain";

        @Test
        void shouldSkipMailTo() {
            //when
            returnLinks(newLink("mailto:somebody@somewhere"));

            //then
            SiteLinks crawl = crawler.crawl(DOMAIN);

            //expect
            assertThat(crawl.getInternalLinks().keySet()).isEmpty();
            assertThat(crawl.getExternalLinks().keySet()).isEmpty();
        }

        private OngoingStubbing<Elements> returnLinks(String forPath, Element... elements) {
            Elements links = new Elements();
            links.addAll(Arrays.asList(elements));
            return when(documentFetcher.fetch(forPath).select("a[href]")).thenReturn(links);
        }

        private void returnLinks(Element... elements) {
            returnLinks("/", elements);
        }

        private Element newLink(String url) {
            return new Element(Tag.valueOf("a"), "").attr("href", url);
        }

        @Nested
        class Internal {

            @Test
            void shouldExtractUrlFromLinks() {
                //when
                String url = "/FindMe";
                returnLinks(newLink(url));

                //then
                SiteLinks crawl = crawler.crawl(DOMAIN);

                //expect
                assertThat(crawl.getInternalLinks().get("/")).containsExactly(url);
            }

            @Test
            void shouldReturnMultipleLinks() {
                //when
                returnLinks(newLink("one"), newLink("two"), newLink("three"));

                //then
                SiteLinks crawl = crawler.crawl(DOMAIN);

                //expect
                assertThat(crawl.getInternalLinks().get("/")).contains("one", "two", "three");
            }

            @Test
            void shouldFindInternalIfSameDomain() {
                //when
                String path = "/myPath";
                returnLinks(newLink(DOMAIN + path));

                //then
                SiteLinks crawl = crawler.crawl(DOMAIN);

                //expect
                assertThat(crawl.getExternalLinks().keySet()).isEmpty();
                assertThat(crawl.getInternalLinks().get("/")).containsExactly(path);
            }
        }

        @Nested
        class External {

            @Test
            void shouldExtractExternalLinks() {
                //when
                String url = "http://google.com";
                returnLinks(newLink(url));

                //then
                SiteLinks crawl = crawler.crawl(DOMAIN);

                //expect
                assertThat(crawl.getExternalLinks().get("/")).containsExactly(url);
            }
        }

        @Nested
        class SubPages {
            @Test
            void shouldScanInnerPages() {
                //given
                String innerPage = "/inner";
                returnLinks(newLink(innerPage));
                String path = "/FindMe";
                returnLinks(innerPage, newLink(path));

                //then
                SiteLinks crawl = crawler.crawl(DOMAIN);

                //expect
                assertThat(crawl.getInternalLinks().get(innerPage)).contains(path);
            }

            @Test
            void shouldSkipProcessedPages() {
                //when
                String page = "internalPage";
                String root = "/";
                Answer<Object> answer = invocation -> fail("Should process page only once");
                returnLinks(root, newLink(page)).thenAnswer(answer);
                returnLinks(page, newLink(root)).thenAnswer(answer);

                //then
                SiteLinks crawl = crawler.crawl(DOMAIN);

                //expect
                assertThat(crawl.getInternalLinks().get(root)).containsExactly(page);
                assertThat(crawl.getInternalLinks().get(page)).containsExactly(root);
            }
        }

        @Nested
        class Exceptions {
            @Test
            void shouldIgnore404Exception() {
                //when
                String page = "/someUnknownPage";
                returnLinks(newLink(page));
                when(documentFetcher.fetch(page)).thenThrow(PageNotFoundException.class);

                //then
                crawler.crawl("");

                //expect
                verify(documentFetcher).fetch(page);
            }
        }

    }
}
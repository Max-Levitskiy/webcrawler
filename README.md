# Run
- Run application. Application run for https://wiprodigital.comsite. Work result in the logs (everithing prints in 
the log).
 ```
mvnw compile exec:java
 ```
- Tests
```
mvnw test
```

# Output explanation
Output is instance of SiteLinks class. It contains internal end external links (resources can be easily added, but I 
was out of time).


# Trade offs
- For collecting links was chosen Multimap with set (avoid duplicates) implementation. 
- Use google.com for test of DocumentFetcher, because of Jsoup.connect() static method.
- Crawler won't work with frontend applications 

# TODO:
- What to do with missed pages
- Parse content only, exclude same elements from output.
- Refactor Crawler.crawlPage() method. Out of time.
- Add delay to request for avoid DDoS protection auto-bans.
- Rewrite implementation to working queue for avoid stack overflow and out of memory exception, process 
parallel
- How deep do we need to go.
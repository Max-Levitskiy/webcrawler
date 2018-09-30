package webcrawler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class SiteLinks {
    private Multimap<String, String> externalLinks = HashMultimap.create();
    private Multimap<String, String> internalLinks = HashMultimap.create();

    public Multimap<String, String> getExternalLinks() {
        return externalLinks;
    }

    public Multimap<String, String> getInternalLinks() {
        return internalLinks;
    }
}

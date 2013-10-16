package eu.socialsensor.framework.client.dao;

import eu.socialsensor.framework.common.domain.JSONable;
import eu.socialsensor.framework.common.domain.WebPage;
import java.util.List;

/**
 *
 * @author etzoannos
 */
public interface WebPageDAO {

    public void addWebPage(WebPage webPage);

    public void removeWebPage(String webPageURL);

    public void updateWebPage(String webPageURL, String name, Object value);

    public void updateWebPage(String webPageURL, JSONable changes);

    public WebPage getWebPage(String webPageURL);

    public List<WebPage> getLastWebPages(int size);

    public void clearAll();

    public List<WebPage> getWebPagesForTweets(List<String> tweetIds);

    public List<WebPage> getWebPagesForUrls(List<String> urls);

	void updateWebPageShares(String webPageURL);
}

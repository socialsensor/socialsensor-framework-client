package eu.socialsensor.framework.client.search.solr;

import eu.socialsensor.framework.common.domain.WebPage;

import java.net.MalformedURLException;
import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class SolrWebPage {

	
	
	@Field(value = "url")
	private String url;

	@Field(value = "title")
	private String title;
	
	@Field(value = "date")
	private Date date;

	@Field(value = "reference")
	private String reference;

	@Field(value = "streamId")
	private String streamId;	
	
	public SolrWebPage() {
		
	}

	public SolrWebPage(WebPage webPage) {

        

        url = webPage.getUrl();
        title = webPage.getTitle();
        date = webPage.getDate();
        reference = webPage.getReference();
        streamId = webPage.getStreamId();
    }

    public WebPage toWebPage() throws MalformedURLException {

    	WebPage webPage = new WebPage(url, reference);

    	webPage.setTitle(title);
    	webPage.setStreamId(streamId);
    	webPage.setDate(date);
    	
        return webPage;
    }
    
    
    public String getUrl() {
    	return url;
	}
    
    public String getTitle() {
    	return title;
	}
    
    public Date getDate() {
    	return date;
	}
    
    public String getReference() {
    	return reference;
	}

    public String getStreamId() {
    	return streamId;
	}
	
}

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

	@Field(value = "domain")
	private String domain;
	
	@Field(value = "title")
	private String title;
	
	@Field(value = "text")
	private String text;
	
	@Field(value = "date")
	private Date date;

	@Field(value = "reference")
	private String reference;

	@Field(value = "streamId")
	private String streamId;	
	
	@Field(value = "shares")
	private int shares;	
	
	@Field(value = "media")
	private int media;	
	
	@Field(value = "mediaIds")
	private String[] mediaIds;	
	
	
	public SolrWebPage() {
		
	}

	public SolrWebPage(WebPage webPage) {
        url = webPage.getUrl();
        domain = webPage.getDomain();
        title = webPage.getTitle();
        text = webPage.getText();
        date = webPage.getDate();
        reference = webPage.getReference();
        streamId = webPage.getStreamId();
        shares = webPage.getShares();
        media = webPage.getMedia();
        mediaIds = webPage.getMediaIds();
    }

    public WebPage toWebPage() throws MalformedURLException {

    	WebPage webPage = new WebPage(url, reference);

    	webPage.setTitle(title);
    	webPage.setText(text);
    	webPage.setStreamId(streamId);
    	webPage.setDate(date);
    	webPage.setDomain(domain);
    	webPage.setShares(shares);
    	webPage.setMedia(media);
    	webPage.setMediaIds(mediaIds);
    	
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
	
    public int getShares() {
    	return shares;
	}
    
}

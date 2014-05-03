package eu.socialsensor.framework.client.search.solr;

import eu.socialsensor.framework.common.domain.WebPage;

import java.net.MalformedURLException;
import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

/**
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class SolrWebPage {
	
	@Field(value = "url")
	private String url;

	@Field(value = "expandedUrl")
	private String expandedUrl;
	
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
	
	@Field(value = "mediaThumbnail")
	private String mediaThumbnail;	
	
	public SolrWebPage() {
		
	}

	public SolrWebPage(WebPage webPage) {
        url = webPage.getUrl();
        expandedUrl = webPage.getExpandedUrl();
        domain = webPage.getDomain();
        title = webPage.getTitle();
        text = webPage.getText();
        date = webPage.getDate();
        reference = webPage.getReference();
        streamId = webPage.getStreamId();
        shares = webPage.getShares();
        media = webPage.getMedia();
        mediaIds = webPage.getMediaIds();
        mediaThumbnail = webPage.getMediaThumbnail();
    }

    public WebPage toWebPage() throws MalformedURLException {

    	WebPage webPage = new WebPage(url, reference);

    	webPage.setExpandedUrl(expandedUrl);
    	webPage.setTitle(title);
    	webPage.setText(text);
    	webPage.setStreamId(streamId);
    	webPage.setDate(date);
    	webPage.setDomain(domain);
    	webPage.setShares(shares);
    	webPage.setMedia(media);
    	webPage.setMediaIds(mediaIds);
    	webPage.setMediaThumbnail(mediaThumbnail);
    	
        return webPage;
    }
    
    public String getUrl() {
    	return url;
	}
    
    public String getExpandedUrl() {
    	return expandedUrl;
	}
    
    public String getDomain() {
    	return domain;
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
    
    public int getNumberOfMedia() {
    	return media;
	}
    
    public String[] getMediaIds() {
    	return mediaIds;
	}
    
    public String getMediaThumbnail() {
    	return mediaThumbnail;
	}
}

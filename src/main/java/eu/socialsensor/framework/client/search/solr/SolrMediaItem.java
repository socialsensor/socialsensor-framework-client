package eu.socialsensor.framework.client.search.solr;

import eu.socialsensor.framework.common.domain.Location;
import eu.socialsensor.framework.common.domain.MediaItem;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class SolrMediaItem {

	public SolrMediaItem() {
	}

	public SolrMediaItem(MediaItem mediaItem) {

        id = mediaItem.getId();
        streamId = mediaItem.getStreamId();
        title = mediaItem.getTitle();
        description = mediaItem.getDescription();
        tags = mediaItem.getTags();
        author = mediaItem.getAuthor();
        
        url = mediaItem.getUrl();
        thumbnail = mediaItem.getThumbnail();
        publicationTime = mediaItem.getPublicationTime();

        latitude = mediaItem.getLatitude();
        longitude = mediaItem.getLongitude();
        location = mediaItem.getLocationName();

        dyscoId = mediaItem.getDyscoId();

        //this is a map
        popularity = new ArrayList<String>();
        if (mediaItem.getPopularity() != null) {
            for (String popularityKey : mediaItem.getPopularity().keySet()) {
                popularity.add(popularityKey + "%%" + mediaItem.getPopularity().get(popularityKey));
            }
        }
        
        List<String> feedKeywords = mediaItem.getFeedKeywords();
        if(feedKeywords != null) {
        	this.feedKeywords = new ArrayList<String>(feedKeywords);
        		
        }
        
        List<String> feedKeywordsString = mediaItem.getFeedKeywordsString();
        if(feedKeywordsString != null) {
        	this.feedKeywordsString = new ArrayList<String>(feedKeywordsString);
        		
        }
        
        
        mentions = mediaItem.getMentions();
       
        type = mediaItem.getType();
    }

    public MediaItem toMediaItem() throws MalformedURLException {

    	MediaItem mediaItem = new MediaItem(new URL(url));

    	mediaItem.setId(id);
    	mediaItem.setStreamId(streamId);
    	mediaItem.setThumbnail(thumbnail);

        mediaItem.setTitle(title);
        mediaItem.setDescription(description);
        mediaItem.setTags(tags);
        mediaItem.setAuthor(author);

        mediaItem.setPublicationTime(publicationTime);
        mediaItem.setFeedKeywords(feedKeywords);
        mediaItem.setFeedKeywordsString(feedKeywordsString);
        mediaItem.setLocation(new Location(latitude, longitude, location));
        mediaItem.setDyscoId(dyscoId);
        mediaItem.setType(type);
        
        //this is a Map<String, Long>
        if (popularity != null) {
            Map<String, Integer> _popularity = new HashMap<String, Integer>();
            for (String popularityEntry : popularity) {
                String[] popularityPair = popularityEntry.split("%%");
                if (popularityPair.length == 2) {
                    _popularity.put(popularityPair[0], new Integer(popularityPair[1]));
                }
            }
            mediaItem.setPopularity(_popularity);
        }

        return mediaItem;
    }
    @Field(value = "id")
    private String id;
    
    @Field(value = "url")
    private String url;
    
    @Field(value = "thumbnail")
    private String thumbnail;
    
    @Field(value = "streamId")
    private String streamId;

    @Field(value = "title")
    private String title;
    
    @Field(value = "description")
    private String description;
    
    @Field(value = "tags")
    private String[] tags;
    
    @Field(value = "author")
    private String author;
    
    @Field(value = "popularity")
    private List<String> popularity;
    
    @Field(value = "publicationTime")
    private long publicationTime;
    
    @Field(value = "operation")
    private String operation;
    
    @Field(value = "latitude")
    private Double latitude;
    
    @Field(value = "longitude")
    private Double longitude;
    
    @Field(value = "location")
    private String location;

    @Field(value = "dyscoId")
    private String dyscoId;

    @Field(value = "mentions")
    private String[] mentions;
   
    @Field(value = "feedKeywords")
    private List<String> feedKeywords = new ArrayList<String>();
    
    @Field(value = "feedKeywordsString")
    private List<String> feedKeywordsString = new ArrayList<String>();
    
    @Field(value = "type")
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getPopularity() {
        return popularity;
    }

    public void setPopularity(List<String> popularity) {
        this.popularity = popularity;
    }

    public Long getPublicationTime() {
        return publicationTime;
    }

    public void setPublicationTime(Long publicationTime) {
        this.publicationTime = publicationTime;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getFeedKeywords() {
    	return feedKeywords;
    }

    public void setFeedKeywords(List<String> feedKeywords) {
    	this.feedKeywords = feedKeywords;
    }
    
    public List<String> getFeedKeywordsString() {
    	return feedKeywordsString;
    }

    public void setFeedKeywordsString(List<String> feedKeywordsString) {
    	this.feedKeywordsString = feedKeywordsString;
    }


    public String getDyscoId() {
        return dyscoId;
    }

    public void setDyscoId(String dyscoId) {
        this.dyscoId = dyscoId;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

package eu.socialsensor.framework.client.search.solr;

import eu.socialsensor.framework.common.domain.Location;
import eu.socialsensor.framework.common.domain.MediaItem;
import java.net.MalformedURLException;
import java.net.URL;

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
        
        if(mediaItem.getUser() != null)
        	author = mediaItem.getUser().getName();
        mentions = mediaItem.getMentions();
        
        url = mediaItem.getUrl();
        thumbnail = mediaItem.getThumbnail();
        publicationTime = mediaItem.getPublicationTime();
        
        popularity = mediaItem.getLikes() + mediaItem.getShares() + mediaItem.getComments() + mediaItem.getViews();

        latitude = mediaItem.getLatitude();
        longitude = mediaItem.getLongitude();
        location = mediaItem.getLocationName();
       
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
        
        //author needs to be added here

        mediaItem.setPublicationTime(publicationTime);
        
        //popularity needs to be added here
        
        mediaItem.setLocation(new Location(latitude, longitude, location));
        mediaItem.setType(type);

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
    
    @Field(value = "publicationTime")
    private long publicationTime;
    
    @Field(value = "popularity")
    private long popularity;
   
    @Field(value = "latitude")
    private Double latitude;
    
    @Field(value = "longitude")
    private Double longitude;
    
    @Field(value = "location")
    private String location;
    
    @Field(value = "author")
    private String author;

    @Field(value = "mentions")
    private String[] mentions;
    
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

    public Long getPublicationTime() {
        return publicationTime;
    }

    public void setPublicationTime(Long publicationTime) {
        this.publicationTime = publicationTime;
    }
    
    public Long getPopularity() {
        return popularity;
    }

    public void setPopularity(Long popularity) {
        this.popularity = popularity;
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
    
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String[] getMentions() {
        return mentions;
    }

    public void setMentions(String[] mentions) {
        this.mentions = mentions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

package eu.socialsensor.framework.client.search.solr;

import eu.socialsensor.framework.common.domain.MediaItem;

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
        
        uid = mediaItem.getUserId();

        publicationTime = mediaItem.getPublicationTime();
        
        popularity = mediaItem.getLikes() + mediaItem.getShares() + mediaItem.getComments() + mediaItem.getViews();

        latitude = mediaItem.getLatitude();
        longitude = mediaItem.getLongitude();
        location = mediaItem.getLocationName();
       
        type = mediaItem.getType();
    }
    
    
    @Field(value = "id")
    private String id;
    
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
   
    @Field(value = "longitude")
    private Long popularity;
    
    @Field(value = "latitude")
    private Double latitude;
    
    @Field(value = "longitude")
    private Double longitude;
    
    @Field(value = "location")
    private String location;
    
    @Field(value = "uid")
    private String uid;
    
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
    
    public String getUserid() {
        return uid;
    }

    public void setUserid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

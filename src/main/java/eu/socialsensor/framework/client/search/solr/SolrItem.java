package eu.socialsensor.framework.client.search.solr;

import eu.socialsensor.framework.common.domain.Item;

import org.apache.solr.client.solrj.beans.Field;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class SolrItem {

    public SolrItem() {
    }

    public SolrItem(Item item) {

        id = item.getId();
        streamId = item.getStreamId();
        title = item.getTitle();
        description = item.getDescription();
        tags = item.getTags();

        uid = item.getUserId();

        //this is long
        publicationTime = item.getPublicationTime();

        latitude = item.getLatitude();
        longitude = item.getLongitude();
        location = item.getLocationName();
        sentiment = item.getSentiment();
        
        language = item.getLang();


       


    }

    
    @Field(value = "id")
    private String id;
    
    @Field(value = "streamId")
    private String streamId;
    
    @Field(value = "source")
    private String source;
    
    @Field(value = "title")
    private String title;
    
    @Field(value = "description")
    private String description;
    
    @Field(value = "tags")
    private String[] tags;
    
    @Field(value = "categories")
    private String[] categories;
    
    @Field(value = "uid")
    private String uid;

    @Field(value = "publicationTime")
    private long publicationTime;

    @Field(value = "latitude")
    private Double latitude;
    
    @Field(value = "longitude")
    private Double longitude;
    
    @Field(value = "location")
    private String location;
    
    @Field(value = "language")
    private String language;
    
    @Field(value = "sentiment")
    private String sentiment;

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String getUserid() {
        return uid;
    }

    public void setUserid(String uid) {
        this.uid = uid;
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

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }


  
}

package eu.socialsensor.framework.client.search.solr;

import eu.socialsensor.framework.common.domain.Concept;
import eu.socialsensor.framework.common.domain.Location;
import eu.socialsensor.framework.common.domain.MediaItem;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.common.SolrDocument;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class SolrMediaItem {

    public SolrMediaItem() {
    }
    
    public SolrMediaItem(SolrDocument solrDocument){
	  
    	id = (String) solrDocument.getFieldValue("id");
    	url = (String) solrDocument.getFieldValue("url");
    	thumbnail = (String) solrDocument.getFieldValue("thumbnail");
    	streamId = (String) solrDocument.getFieldValue("streamId");
    	title = (String) solrDocument.getFieldValue("title");
    	description = (String) solrDocument.getFieldValue("description");
    	if(solrDocument.getFieldValue("tags") != null){
    		@SuppressWarnings("unchecked")
			List<String> listOfTags = (List<String>) solrDocument.getFieldValue("tags");
    		tags = new String[listOfTags.size()];
    		int index = 0;
    		for(String tag : listOfTags){
    			tags[index++] = tag;
    		}
    	}
    	author = (String) solrDocument.getFieldValue("author");
    	publicationTime = (Long) solrDocument.getFieldValue("publicationTime");
    	popularity = (Long) solrDocument.getFieldValue("popularity");
    	latitude = (Double) solrDocument.getFieldValue("latitude");
    	longitude = (Double) solrDocument.getFieldValue("longitude");
    	location = (String) solrDocument.getFieldValue("location");
    	if(solrDocument.getFieldValue("mentions") != null)
    		mentions = (String[]) solrDocument.getFieldValue("mentions");
    	if(solrDocument.getFieldValue("concepts") != null){
    		@SuppressWarnings("unchecked")
			List<String> listOfConcepts = (List<String>) solrDocument.getFieldValue("concepts");
    		concepts = new String[listOfConcepts.size()];
    		int index = 0;
    		for(String concept : listOfConcepts){
    			concepts[index++] = concept;
    		}
    	}
    	type = (String) solrDocument.getFieldValue("type");
    	
    	solrScore = (Float) solrDocument.getFieldValue("score");
    }
    

    public SolrMediaItem(MediaItem mediaItem) {

        id = mediaItem.getId();
        streamId = mediaItem.getStreamId();
        title = mediaItem.getTitle();
        description = mediaItem.getDescription();
        tags = mediaItem.getTags();

        if (mediaItem.getUser() != null) {
            author = mediaItem.getUser().getName();
        }
        mentions = mediaItem.getMentions();

        url = mediaItem.getUrl();
        thumbnail = mediaItem.getThumbnail();
        publicationTime = mediaItem.getPublicationTime();

        if (mediaItem.getLikes() != null) {
            popularity += mediaItem.getLikes();
        }
        if (mediaItem.getShares() != null) {
            popularity += mediaItem.getShares();
        }
        if (mediaItem.getComments() != null) {
            popularity += mediaItem.getComments();
        }
        if (mediaItem.getViews() != null) {
            popularity += mediaItem.getViews();
        }

        latitude = mediaItem.getLatitude();
        longitude = mediaItem.getLongitude();
        location = mediaItem.getLocationName();

        List<Concept> miConcepts = mediaItem.getConcepts();
        if (miConcepts != null) {
            concepts = new String[miConcepts.size()];
            for (int i = 0; i < concepts.length; i++) {
                concepts[i] = miConcepts.get(i).getConcept();
            }
        }

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
        
        mediaItem.setShares(popularity);
        
        if (latitude != null && longitude != null && location != null) {
            mediaItem.setLocation(new Location(latitude, longitude, location));
        }
        mediaItem.setType(type);

        mediaItem.setSolrScore(solrScore);
        
        List<Concept> conceptsList = new ArrayList<Concept>();

        if (concepts != null) {

            for (String concept : concepts) {
            	try {
            		Concept cpt = new Concept(concept, 0d);
            		conceptsList.add(cpt);
            	}
            	catch(Exception e) {
            		// Undefined concept type.
            	}
            }
        }
        mediaItem.setConcepts(conceptsList);

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
    @Field(value = "concepts")
    private String[] concepts;
    @Field(value = "type")
    private String type;
    
    private float solrScore = 0f;
    
    
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
    
    public void setSolrScore(Float solrScore){
    	this.solrScore = solrScore;
    }
    
    public Float getSolrScore(){
    	return this.solrScore;
    }
}

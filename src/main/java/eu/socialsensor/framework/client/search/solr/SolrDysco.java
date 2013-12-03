package eu.socialsensor.framework.client.search.solr;

import eu.socialsensor.framework.common.domain.dysco.Dysco;
import eu.socialsensor.framework.common.domain.dysco.Entity;
import eu.socialsensor.framework.common.domain.dysco.Entity.Type;
import eu.socialsensor.framework.common.domain.dysco.Ngram;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.solr.client.solrj.beans.Field;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class SolrDysco {
	
	//The id of the dysco
    @Field(value = "id")
    private String id;
    //The creation date of the dysco
    @Field(value = "creationDate")
    private Date creationDate;
    //The title of the dysco
    @Field(value = "title")
    private String title;
    //The score that shows how trending the dysco is
    @Field(value = "dyscoScore")
    private Float score = 0f;
    
    //Fields holding the information about the main context 
    //of the items that constitute the dysco
    
  	//The extracted entities from items' content 
    //all 6 refer to the 3 types of entities and their weights in the dysco
    @Field(value = "persons")
    private List<String> persons = new ArrayList<String>();
    @Field(value = "locations")
    private List<String> locations = new ArrayList<String>();
    @Field(value = "organizations")
    private List<String> organizations = new ArrayList<String>();
    @Field(value = "personsScore")
    private List<String> personsScore = new ArrayList<String>();
    @Field(value = "locationsScore")
    private List<String> locationsScore = new ArrayList<String>();
    @Field(value = "organizationsScore")
    private List<String> organizationsScore = new ArrayList<String>();
    
    //The users that contribute in social networks to dysco's topic
    @Expose
    @SerializedName(value = "contributors")
    private List<String> contributors = new ArrayList<String>();
    //The extracted keywords from items' content with their assigned weights
    @Field(value = "keywords")
    private List<String> keywords = new ArrayList<String>();
    @Field(value = "keywordsScore")
    private List<String> keywordsScore = new ArrayList<String>();
    //The extracted hashtags from items' content with their assigned weights
    @Field(value = "hashtags")
    private List<String> hashtags = new ArrayList<String>();
    @Field(value = "hashtagsScore")
    private List<String> hashtagsScore = new ArrayList<String>();
    //The additional keywords resulting from keywords expansion methods with their assigned weights
    @Field(value = "latent_keywords")
    private List<String> latent_keywords = new ArrayList<String>();
    @Field(value = "latent_keywordsScore")
    private List<String> latent_keywordsScore = new ArrayList<String>();
    
    //The query that will be used for retrieving relevant content to the Dysco from Solr
    @Field(value = "solrQuery")
    private String solrQuery;
    
    //The following need to be considered whether they are going to be omitted or not
    @Field(value = "evolution")
    private String evolution;
    @Field(value = "trending")
    private int trending;
    @Field(value = "alethiometerStatus")
    private int alethiometerStatus;
    @Field(value = "updateDate")
    private Date updateDate;
    @Field(value = "thumb")
    private String thumb;
    @Field(value = "thumbs")
    private List<String> thumbs = new ArrayList<String>();
    @Field(value = "sentiment")
    private Float sentiment;
    
    public SolrDysco() {
        id = UUID.randomUUID().toString();
    }

    public SolrDysco(Dysco dysco) {

        id = dysco.getId();
        creationDate = dysco.getCreationDate();
        title = dysco.getTitle();
        score = dysco.getScore();
        
        List<Entity> dyscoEntities = dysco.getEntities();
        for (Entity entity : dyscoEntities) {
            if (entity.getType().equals(Type.LOCATION)) {
                locationsScore.add(entity.getName() + "@@" + entity.getCont());
                locations.add(entity.getName());
            }
            if (entity.getType().equals(Type.PERSON)) {
                personsScore.add(entity.getName() + "@@" + entity.getCont());
                persons.add(entity.getName());
            }
            if (entity.getType().equals(Type.ORGANIZATION)) {
                organizationsScore.add(entity.getName() + "@@" + entity.getCont());
                organizations.add(entity.getName());
            }
        }
        
        contributors = dysco.getContributors();
        
        for(Map.Entry<String,Double> entry : dysco.getKeywords().entrySet()){
        	keywords.add(entry.getKey());
        	keywordsScore.add(entry.getKey()+"@@"+entry.getValue());
        }
        
        for(Map.Entry<String,Double> entry : dysco.getHashtags().entrySet()){
        	hashtags.add(entry.getKey());
        	hashtagsScore.add(entry.getKey()+"@@"+entry.getValue());
        }
        
        for(Map.Entry<String,Double> entry : dysco.getLatentKeywords().entrySet()){
        	latent_keywords.add(entry.getKey());
        	latent_keywordsScore.add(entry.getKey()+"@@"+entry.getValue());
        }
        
        solrQuery = dysco.getSolrQuery();
        
        //The following need to be considered whether they are going to be omitted or not
        evolution = dysco.getEvolution();
        trending = dysco.getTrending();
        alethiometerStatus = dysco.getAlethiometerStatus();
        updateDate = dysco.getUpdateDate();
        if (dysco.getThumb() != null) {
            thumb = dysco.getThumb().toString();
        }
        thumbs = dysco.getThumbs();
        //TODO add other dimensions - e.g. Sentiment etc 
    }

    public Dysco toDysco() {

        Dysco dysco = new Dysco();
        
        dysco.setId(id);
        dysco.setCreationDate(creationDate);
        dysco.setTitle(title);
        dysco.setScore(score);
        
        if (personsScore != null) {
            for (String entity : personsScore) {
                String[] splitted = entity.split("@@");
                Entity dyscoEntity = new Entity(splitted[0], new Integer(splitted[1]), Type.PERSON);
                dysco.addEntity(dyscoEntity);
            }
        }
        if (locationsScore != null) {
            for (String entity : locationsScore) {
                String[] splitted = entity.split("@@");
                Entity dyscoEntity = new Entity(splitted[0], new Integer(splitted[1]), Type.LOCATION);
                dysco.addEntity(dyscoEntity);
            }
        }
        if (organizationsScore != null) {
            for (String entity : organizationsScore) {
                String[] splitted = entity.split("@@");
                Entity dyscoEntity = new Entity(splitted[0], new Integer(splitted[1]), Type.ORGANIZATION);
                dysco.addEntity(dyscoEntity);
            }
        }
        
        dysco.setContributors(contributors);

        if(keywordsScore != null){
        	for(String keyword : keywordsScore){
        		String[] splitted = keyword.split("@@");
        		dysco.addKeyword(splitted[0], Double.parseDouble(splitted[1]));
        	}
        }
        
        if(hashtagsScore != null){
        	for(String hashtag : hashtagsScore){
        		String[] splitted = hashtag.split("@@");
        		dysco.addHashtag(splitted[0], Double.parseDouble(splitted[1]));
        	}
        }
        
        if(latent_keywordsScore != null){
        	for(String latent_keyword : latent_keywordsScore){
        		String[] splitted = latent_keyword.split("@@");
        		dysco.addHashtag(splitted[0], Double.parseDouble(splitted[1]));
        	}
        }
        
        dysco.setSolrQuery(solrQuery);
      
        //The following need to be considered whether they are going to be omitted or not
        //TODO add Dysco Dymensions
        dysco.setUpdateDate(updateDate);
        dysco.setThumbs(thumbs);
        dysco.setEvolution(evolution);
        dysco.setTrending(trending);
        dysco.setAlethiometerStatus(alethiometerStatus);
        if (thumb != null) {
            try {
                dysco.setThumb(new URL(thumb));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        
        return dysco;

    }
    
    /**
     * Returns the id of the dysco
     * @return String
     */
    public String getId() {
        return id;
    }
    /**
     * Sets the id of the dysco
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the creation date of the dysco
     * @return Date
     */
    public Date getCreationDate() {
        return creationDate;
    }
    
    /**
     * Sets the creation date of the dysco
     * @param creationDate
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    
    /**
     * Returns the title of the dysco
     * @return String
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the title of the dysco
     * @param Title
     */
    public void setTitle(String Title) {
        this.title = Title;
    }
    
    /**
     * Returns the score of the dysco 
     * @return Float
     */
    public Float getScore() {
        return score;
    }
    
    /**
     * Sets the score of the dysco
     * @param score
     */
    public void setScore(Float score) {
        this.score = score;
    }
    
    /**
     * Returns the list of names of the Entities that are Persons inside the dysco
     * @return List of String
     */
    public List<String> getPersons() {
        return persons;
    }
    /**
     * Sets the list of names of the Entities that are Persons inside the dysco
     * @param persons
     */
    public void setPersons(List<String> persons) {
        this.persons = persons;
    }
    /**
     * Returns the list of the name entities that are Persons inside the dysco
     * with their corresponding weights
     * @return List of String
     */
    public List<String> getPersonsScore() {
        return personsScore;
    }
    /**
     * Sets the name entities that are Persons inside the dysco
     * with their corresponding weights
     * @param personsScore
     */
    public void setPersonsScore(List<String> personsScore) {
        this.personsScore = personsScore;
    }
    /**
     * Returns the list of names of the Entities that are Locations inside the dysco
     * @return
     */
    public List<String> getLocations() {
        return locations;
    }
    /**
     * Sets the list of names of the Entities that are Locations inside the dysco
     * @param locations
     */
    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
    /**
     * Returns the name entities that are Locations inside the dysco
     * with their corresponding weights
     * @return
     */
    public List<String> getLocationsScore() {
        return locationsScore;
    }
    /**
     * Sets the name entities that are Locations inside the dysco
     * with their corresponding weights
     * @param locationsScore
     */
    public void setLocationsScore(List<String> locationsScore) {
        this.locationsScore = locationsScore;
    }
    /**
     * Returns the list of names of the Entities that are Organizations inside the dysco
     * @return List of String
     */
    public List<String> getOrganizations() {
        return organizations;
    }
    /**
     * Sets the list of names of the Entities that are Organizations inside the dysco
     * @param organizations
     */
    public void setOrganizations(List<String> organizations) {
        this.organizations = organizations;
    }
    /**
     * Sets the name entities that are Organizations inside the dysco
     * with their corresponding weights
     * @return
     */
    public List<String> getOrganizationsScore() {
        return organizationsScore;
    }
    /**
     * Sets the name entities that are Organizations inside the dysco
     * with their corresponding weights
     * @param organizationsScore
     */
    public void setOrganizationsScore(List<String> organizationsScore) {
        this.organizationsScore = organizationsScore;
    }
    /**
     * Returns the list of contributors for the dysco
     * @return List of String
     */
    public List<String> getContributors() {
        return contributors;
    }
    /**
     * Sets the contributors for the dysco
     * @param contributors
     */
    public void setContributors(List<String> contributors) {
        this.contributors = contributors;
    }
    /**
     * Returns the keywords of the dysco
     * @return List of String
     */
    public List<String> getKeywords() {
        return keywords;
    }
    /**
     * Sets the keywords of the dysco
     * @param keywords
     */
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    /**
     * Returns the keywords of the dysco with their corresponding
     * weights
     * @return List of String
     */
    public List<String> getKeywordsScore() {
        return keywordsScore;
    }
    /**
     * Sets the keywords of the dysco with their corresponding
     * weights
     * @param keywordsScore
     */
    public void setKeywordsScore(List<String> keywordsScore) {
        this.keywordsScore = keywordsScore;
    }
    /**
     * Returns the hashtags of the dysco
     * @return List of String
     */
    public List<String> getHashtags() {
        return hashtags;
    }
    /**
     * Sets the hashtags of the dysco
     * @param hashtags
     */
    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }
    /**
     * Returns the hashtags of the dysco with their corresponding
     * weights
     * @return List of String
     */
    public List<String> getHashtagsScore() {
        return hashtagsScore;
    }
    /**
     * Sets the hashtags of the dysco with their corresponding
     * weights
     * @param hashtagsScore
     */
    public void setHashtagsScore(List<String> hashtagsScore) {
        this.hashtagsScore = hashtagsScore;
    }
    /**
     * Returns the latent_keywords of the dysco
     * @return List of String
     */
    public List<String> getLatentKeywords() {
        return latent_keywords;
    }
    /**
     * Sets the latent_keywords of the dysco
     * @param latent_keywords
     */
    public void setLatentKeywords(List<String> latent_keywords) {
        this.latent_keywords = latent_keywords;
    }
    /**
     * Returns the query for the retrieval of relevant content to the dysco from solr
     * @return String
     */
    public String getSolrQuery(){
    	return solrQuery;
    }
    /**
     * Sets the solr query for the retrieval of relevant content
     * @param solrQuery
     */
    public void setSolrQuery(String solrQuery){
    	this.solrQuery = solrQuery;
    	
    }
    
    
    
    //The following need to be considered whether they are going to be omitted or not
    public String getEvolution() {
        return evolution;
    }
    public void setEvolution(String evolution) {
        this.evolution = evolution;
    }
    public List<String> getThumbs() {
        return thumbs;
    }

    public void setThumbs(List<String> thumbs) {
        this.thumbs = thumbs;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Float getSentiment() {
        return sentiment;
    }

    public void setSentiment(Float sentiment) {
        this.sentiment = sentiment;
    }

    public int getTrending() {
        return trending;
    }

    public void setTrending(int trending) {
        this.trending = trending;
    }

    public int getAlethiometerStatus() {
        return alethiometerStatus;
    }

    public void setAlethiometerStatus(int alethiometerStatus) {
        this.alethiometerStatus = alethiometerStatus;
    }
    
}

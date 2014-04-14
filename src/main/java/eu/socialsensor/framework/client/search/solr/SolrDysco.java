package eu.socialsensor.framework.client.search.solr;

import eu.socialsensor.framework.common.domain.Query;
import eu.socialsensor.framework.common.domain.dysco.Dysco;
import eu.socialsensor.framework.common.domain.dysco.Dysco.DyscoType;
import eu.socialsensor.framework.common.domain.dysco.Entity;
import eu.socialsensor.framework.common.domain.dysco.Entity.Type;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.beans.Field;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class SolrDysco {
	public final Logger logger = Logger.getLogger(SolrDysco.class);

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
    private Double score = 0d;
    //The type of the dysco (CUSTOM/TRENDING)
    @Field(value = "dyscoType")
    private String dyscoType;
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
    //The users that contribute in social networks to dysco's topic
    @Expose
    @SerializedName(value = "contributors")
    private List<String> contributors = new ArrayList<String>();
    //The extracted keywords from items' content with their assigned weights
    @Field(value = "keywords")
    private List<String> keywords = new ArrayList<String>();
    //The extracted hashtags from items' content with their assigned weights
    @Field(value = "hashtags")
    private List<String> hashtags = new ArrayList<String>();
    //The query that will be used for retrieving relevant content to the Dysco from Solr
    @Field(value = "solrQueryString")
    private String solrQueryString;
    @Field(value = "solrQueriesString")
    private List<String> solrQueriesString = new ArrayList<String>();
    @Field(value = "primalSolrQueriesString")
    private List<String> primalSolrQueriesString = new ArrayList<String>();
    @Field(value = "solrQueriesScore")
    private List<String> solrQueriesScore = new ArrayList<String>();
    //The variable can get values 0,1,2 and shows dysco's trending evolution. 
    @Field(value = "trending")
    private int trending;
    //The date that the dysco was last created (updated because similar dyscos existed in the past)
    @Field(value = "updateDate")
    private Date updateDate;
    
    @Field(value = "listId")
    private String listId;

    public SolrDysco() {
        id = UUID.randomUUID().toString();
    }

    public SolrDysco(Dysco dysco) {
    
        id = dysco.getId();
        creationDate = dysco.getCreationDate();
        title = dysco.getTitle();
        score = dysco.getScore();
        dyscoType = dysco.getDyscoType().toString();

        List<Entity> dyscoEntities = dysco.getEntities();
        for (Entity entity : dyscoEntities) {
            if (entity.getType().equals(Type.LOCATION)) {
                locations.add(entity.getName());
            }
            if (entity.getType().equals(Type.PERSON)) {
                persons.add(entity.getName());
            }
            if (entity.getType().equals(Type.ORGANIZATION)) {
                organizations.add(entity.getName());
            }
        }

        contributors = dysco.getContributors();

        for (Map.Entry<String, Double> entry : dysco.getKeywords().entrySet()) {
            keywords.add(entry.getKey());
        }

        for (Map.Entry<String, Double> entry : dysco.getHashtags().entrySet()) {
            hashtags.add(entry.getKey());
        }

        solrQueryString = dysco.getSolrQuery();
        
        for(Query query : dysco.getPrimalSolrQueries()){
        	//logger.info("query name: "+query.getName());
        	//logger.info("query score: "+query.getScore().toString());
        	primalSolrQueriesString.add(query.getName());
        	
        }
        
        //logger.info("DYSCO QUERIES : "+dysco.getSolrQueries().size());
        for(Query query : dysco.getSolrQueries()){
        	//logger.info("query name: "+query.getName());
        	//logger.info("query score: "+query.getScore().toString());
        	solrQueriesString.add(query.getName());
        	solrQueriesScore.add(query.getScore().toString());
        }
       

        trending = dysco.getTrending();

        updateDate = dysco.getUpdateDate();

        listId = dysco.getListId();

    }

    public Dysco toDysco() {

        Dysco dysco = new Dysco();

        dysco.setId(id);
        dysco.setCreationDate(creationDate);
        dysco.setTitle(title);
        dysco.setScore(score);

        if (dyscoType.equals("CUSTOM")) {
            dysco.setDyscoType(DyscoType.CUSTOM);
        } else {
            dysco.setDyscoType(DyscoType.TRENDING);
        }

        if (persons != null) {
            for (String person : persons) {
                Entity dyscoEntity = new Entity(person, 0.0, Type.PERSON);
                dysco.addEntity(dyscoEntity);
            }
        }
        if (locations != null) {
            for (String location : locations) {
                Entity dyscoEntity = new Entity(location, 0.0, Type.LOCATION);
                dysco.addEntity(dyscoEntity);
            }
        }
        if (organizations != null) {
            for (String organization : organizations) {
                Entity dyscoEntity = new Entity(organization, 0.0, Type.ORGANIZATION);
                dysco.addEntity(dyscoEntity);
            }
        }

        dysco.setContributors(contributors);

        if (keywords != null) {
            for (String keyword : keywords) {
                dysco.addKeyword(keyword, 0.0);
            }
        }

        if (hashtags != null) {
            for (String hashtag : hashtags) {
                dysco.addHashtag(hashtag, 0.0);
            }
        }

        dysco.setSolrQuery(solrQueryString);
        List<Query> queries = new ArrayList<Query>();
        for(int i=0;i<solrQueriesString.size();i++){
        	Query query = new Query();
        	query.setName(solrQueriesString.get(i));
        	query.setScore(Double.parseDouble(solrQueriesScore.get(i)));
        	queries.add(query);
        }
        dysco.setSolrQueries(queries);
        
        dysco.setTrending(trending);
        dysco.setUpdateDate(updateDate);
        
        dysco.setListId(listId);

        return dysco;

    }

    /**
     * Returns the id of the dysco
     *
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of the dysco
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the creation date of the dysco
     *
     * @return Date
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date of the dysco
     *
     * @param creationDate
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Returns the title of the dysco
     *
     * @return String
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the dysco
     *
     * @param Title
     */
    public void setTitle(String Title) {
        this.title = Title;
    }

    /**
     * Returns the score of the dysco
     *
     * @return Float
     */
    public Double getScore() {
        return score;
    }

    /**
     * Sets the score of the dysco
     *
     * @param score
     */
    public void setScore(Double score) {
        this.score = score;
    }

    /**
     * Returns the list of names of the Entities that are Persons inside the
     * dysco
     *
     * @return List of String
     */
    public List<String> getPersons() {
        return persons;
    }

    /**
     * Sets the list of names of the Entities that are Persons inside the dysco
     *
     * @param persons
     */
    public void setPersons(List<String> persons) {
        this.persons = persons;
    }

    /**
     * Returns the list of names of the Entities that are Locations inside the
     * dysco
     *
     * @return
     */
    public List<String> getLocations() {
        return locations;
    }

    /**
     * Sets the list of names of the Entities that are Locations inside the
     * dysco
     *
     * @param locations
     */
    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    /**
     * Returns the list of names of the Entities that are Organizations inside
     * the dysco
     *
     * @return List of String
     */
    public List<String> getOrganizations() {
        return organizations;
    }

    /**
     * Sets the list of names of the Entities that are Organizations inside the
     * dysco
     *
     * @param organizations
     */
    public void setOrganizations(List<String> organizations) {
        this.organizations = organizations;
    }

    /**
     * Returns the list of contributors for the dysco
     *
     * @return List of String
     */
    public List<String> getContributors() {
        return contributors;
    }

    /**
     * Sets the contributors for the dysco
     *
     * @param contributors
     */
    public void setContributors(List<String> contributors) {
        this.contributors = contributors;
    }

    /**
     * Returns the keywords of the dysco
     *
     * @return List of String
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * Sets the keywords of the dysco
     *
     * @param keywords
     */
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * Returns the hashtags of the dysco
     *
     * @return List of String
     */
    public List<String> getHashtags() {
        return hashtags;
    }

    /**
     * Sets the hashtags of the dysco
     *
     * @param hashtags
     */
    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    /**
     * Returns the query as a stringfor the retrieval of relevant content to the
     * dysco from solr
     *
     * @return String
     */
    public String getSolrQuery() {
        return solrQueryString;
    }

    /**
     * Sets the solr query as a string for the retrieval of relevant content
     *
     * @param solrQuery
     */
    public void setSolrQueryString(String solrQueryString) {
        this.solrQueryString = solrQueryString;

    }
    
    public List<String> getPrimalSolrQueriesString(){
    	return primalSolrQueriesString;
    }
    
    public List<String> getSolrQueriesString(){
    	return solrQueriesString;
    }
    
    public List<String> getSolrQueriesScore(){
    	return solrQueriesScore;
    }
    
    public void setPrimalSolrQueriesString(List<String> primalSolrQueriesString){
    	this.primalSolrQueriesString = primalSolrQueriesString;
    }
    
    public void setSolrQueriesString(List<String> solrQueriesString){
    	this.solrQueriesString = solrQueriesString;
    }

    public void setSolrQueriesScore(List<String> solrQueriesScore){
    	this.solrQueriesScore = solrQueriesScore;
    }

    /**
     * Returns the trending value that shows dysco's trending evolution (can be
     * 0,1,2)
     *
     * @return
     */
    public int getTrending() {
        return trending;
    }

    /**
     * Sets the trending value that shows dysco's trending evolution (can be
     * 0,1,2)
     *
     * @param trending
     */
    public void setTrending(int trending) {
        this.trending = trending;
    }

    /**
     * Returns the date that dysco was last updated.
     *
     * @return
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * Sets the date that dysco was last updated.
     *
     * @return
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * Returns the type of the dysco
     *
     * @return dyscoType
     */
    public String getDyscoType() {
        return dyscoType;
    }

    /**
     * Sets the type of the dysco (CUSTOM/TRENDING)
     *
     * @param dyscoType
     */
    public void setDyscoType(String dyscoType) {
        this.dyscoType = dyscoType;
    }
}

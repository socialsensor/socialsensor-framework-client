package eu.socialsensor.framework.client.search.solr;

import eu.socialsensor.framework.common.domain.Location;
import eu.socialsensor.framework.common.domain.Query;
import eu.socialsensor.framework.common.domain.dysco.CustomDysco;
import eu.socialsensor.framework.common.domain.dysco.Dysco;
import eu.socialsensor.framework.common.domain.dysco.Dysco.DyscoType;
import eu.socialsensor.framework.common.domain.dysco.Entity;
import eu.socialsensor.framework.common.domain.dysco.Entity.Type;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
    @Field(value = "solrQueriesScore")
    private List<String> solrQueriesScore = new ArrayList<String>();
    //The variable can get values 0,1,2 and shows dysco's trending evolution. 
    @Field(value = "trending")
    private int trending;
    //The date that the dysco was last created (updated because similar dyscos existed in the past)
    @Field(value = "updateDate")
    private Date updateDate;

    //TODO:  are we using it?
    @Field(value = "listId")
    private String listId;

    //new fields: 6 June 2014
    @Field(value = "links")
    private List<String> links = new ArrayList<String>();

    @Field(value = "itemsCount")
    private int itemsCount = 0;

    @Field(value = "rankerScore")
    private double rankerScore = 0.0d;

    //Custom Dysco fields
    @Field(value = "twitterUsers")
    private List<String> twitterUsers;

    @Field(value = "mentionedUsers")
    private List<String> mentionedUsers;

    @Field(value = "listsOfUsers")
    private List<String> listsOfUsers;

    @Field(value = "otherSocialNetworks")
    private List<String> otherSocialNetworks;

    @Field(value = "nearLocations")
    private List<String> nearLocations;

    @Field(value = "new")
    private String status;

    @Field(value = "group")
    private String group;

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

        solrQueryString = dysco.getSolrQueryString();

        //logger.info("DYSCO QUERIES : "+dysco.getSolrQueries().size());
        for (Query query : dysco.getSolrQueries()) {
            //logger.info("query name: "+query.getName());
            //logger.info("query score: "+query.getScore().toString());
            solrQueriesString.add(query.getName());
            if (query.getScore() != null) {
                solrQueriesScore.add(query.getScore().toString());
            }
        }

        trending = dysco.getTrending();

        updateDate = dysco.getUpdateDate();

        listId = dysco.getListId();

        itemsCount = dysco.getItemsCount();

        rankerScore = dysco.getRankerScore();

        group = dysco.getGroup();
        status = dysco.getStatus();

        if (dysco.getLinks() != null) {
            links = new ArrayList<String>();
            for (Entry<String, Double> e : dysco.getLinks().entrySet()) {
                links.add(e.getValue() + "@#@#" + e.getKey());
            }
        }

    }

    public SolrDysco(CustomDysco customDysco) {

        id = customDysco.getId();
        creationDate = customDysco.getCreationDate();
        title = customDysco.getTitle();
        score = customDysco.getScore();
        dyscoType = customDysco.getDyscoType().toString();
        group = customDysco.getGroup();
        status = customDysco.getStatus();

        for (Map.Entry<String, Double> entry : customDysco.getKeywords().entrySet()) {
            keywords.add(entry.getKey());
        }

        for (Map.Entry<String, Double> entry : customDysco.getHashtags().entrySet()) {
            hashtags.add(entry.getKey());
        }

        for (Query query : customDysco.getSolrQueries()) {
            solrQueriesString.add(query.getName());
            if (query.getScore() != null) {
                Double defaultScore = 10.0;
                solrQueriesScore.add(defaultScore.toString());
            }

        }

        this.twitterUsers = customDysco.getTwitterUsers();
        this.mentionedUsers = customDysco.getMentionedUsers();
        this.listsOfUsers = customDysco.getListsOfUsers();

        if (customDysco.getOtherSocialNetworks() != null) {
            otherSocialNetworks = new ArrayList<String>();
            for (Entry<String, String> e : customDysco.getOtherSocialNetworks().entrySet()) {
                otherSocialNetworks.add(e.getValue() + "#" + e.getKey());
            }
        }

        if (customDysco.getNearLocations() != null) {
            nearLocations = new ArrayList<String>();
            for (Location l : customDysco.getNearLocations()) {
                nearLocations.add(l.getLatitude() + "," + l.getLongitude() + "," + l.getRadius());
            }
        }

    }

    public Dysco toDysco() {

        Dysco dysco = new Dysco(id, creationDate);
        
        
        dysco.setGroup(group);
        dysco.setStatus(status);

        dysco.setTitle(title);
        dysco.setScore(score);

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

        dysco.setSolrQueryString(solrQueryString);
        List<Query> queries = new ArrayList<Query>();
        for (int i = 0; i < solrQueriesString.size(); i++) {
            Query query = new Query();
            query.setName(solrQueriesString.get(i));
            if (solrQueriesScore != null) {
                if (i < solrQueriesScore.size()) {
                    query.setScore(Double.parseDouble(solrQueriesScore.get(i)));
                }
            }

            queries.add(query);
        }
        dysco.setSolrQueries(queries);

        dysco.setTrending(trending);
        dysco.setUpdateDate(updateDate);

        if (links != null) {
            Map<String, Double> _links = new HashMap<String, Double>();
            for (String s : links) {
                String[] parts = s.split("@#@#");
                if (parts.length != 2) {
                    continue;
                }

                _links.put(parts[1], new Double(parts[0]));
            }
            dysco.setLinks(_links);
        }

        if (dyscoType.equals("CUSTOM")) {

            dysco.setDyscoType(DyscoType.CUSTOM);

            CustomDysco customDysco = new CustomDysco(dysco);

            customDysco.setTwitterUsers(twitterUsers);
            customDysco.setMentionedUsers(mentionedUsers);
            customDysco.setListsOfUsers(listsOfUsers);

            //to be rectified
            //customDysco.setOtherSocialNetworks(otherSocialNetworks);
            //customDysco.setNearLocations(nearLocations);
            return customDysco;

        } else {
            dysco.setDyscoType(DyscoType.TRENDING);

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

            dysco.setListId(listId);

            //new fields
            dysco.setItemsCount(itemsCount);
            dysco.setRankerScore(rankerScore);
        }

        return dysco;

    }

    public CustomDysco toCustomDysco() {

        CustomDysco dysco = new CustomDysco(id, creationDate, DyscoType.CUSTOM);

        dysco.setTitle(title);
        dysco.setScore(score);

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

        dysco.setSolrQueryString(solrQueryString);
        List<Query> queries = new ArrayList<Query>();
        for (int i = 0; i < solrQueriesString.size(); i++) {
            Query query = new Query();
            query.setName(solrQueriesString.get(i));
            //TODO this is temporary - remove this check when NaN issue is fixed
            if (solrQueriesScore.get(i).equals("NaN")) {
                query.setScore(Double.parseDouble(solrQueriesScore.get(i)));
            } else {
                query.setScore(0.6);
            }
            queries.add(query);
        }
        dysco.setSolrQueries(queries);

        dysco.setTrending(trending);
        dysco.setUpdateDate(updateDate);

        dysco.setListId(listId);

        dysco.setTwitterUsers(twitterUsers);
        dysco.setMentionedUsers(mentionedUsers);
        dysco.setListsOfUsers(listsOfUsers);

        if (otherSocialNetworks != null) {
            Map<String, String> _otherSocialNetworks = new HashMap<String, String>();
            for (String s : otherSocialNetworks) {
                String[] parts = s.split("#");
                if (parts.length != 2) {
                    continue;
                }

                _otherSocialNetworks.put(parts[1], parts[0]);
            }
            dysco.setOtherSocialNetworks(_otherSocialNetworks);
        }

        if (nearLocations != null) {
            List<Location> _nearLocations = new ArrayList<Location>();
            for (String s : nearLocations) {
                String[] parts = s.split(",");
                if (parts.length != 3) {
                    continue;
                }
                Location l = new Location(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]));
                _nearLocations.add(l);
            }
            dysco.setNearLocations(_nearLocations);
        }

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
    public String getSolrQueryString() {
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

    public List<String> getSolrQueriesString() {
        return solrQueriesString;
    }

    public List<String> getSolrQueriesScore() {
        return solrQueriesScore;
    }

    public void setSolrQueriesString(List<String> solrQueriesString) {
        this.solrQueriesString = solrQueriesString;
    }

    public void setSolrQueriesScore(List<String> solrQueriesScore) {
        this.solrQueriesScore = solrQueriesScore;
    }

    public double getRankerScore() {
        return rankerScore;
    }

    public void setRankerScore(double rankerScore) {
        this.rankerScore = rankerScore;
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

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(int itemsCount) {
        this.itemsCount = itemsCount;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public List<String> getTwitterUsers() {
        return twitterUsers;
    }

    public void setTwitterUsers(List<String> twitterUsers) {
        this.twitterUsers = twitterUsers;
    }

    public List<String> getMentionedUsers() {
        return mentionedUsers;
    }

    public void setMentionedUsers(List<String> mentionedUsers) {
        this.mentionedUsers = mentionedUsers;
    }

    public List<String> getListsOfUsers() {
        return listsOfUsers;
    }

    public void setListsOfUsers(List<String> listsOfUsers) {
        this.listsOfUsers = listsOfUsers;
    }

    public List<String> getOtherSocialNetworks() {
        return otherSocialNetworks;
    }

    public void setOtherSocialNetworks(List<String> otherSocialNetworks) {
        this.otherSocialNetworks = otherSocialNetworks;
    }

    public List<String> getNearLocations() {
        return nearLocations;
    }

    public void setNearLocations(List<String> nearLocations) {
        this.nearLocations = nearLocations;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}

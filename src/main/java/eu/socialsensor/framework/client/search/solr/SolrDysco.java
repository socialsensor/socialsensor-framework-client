package eu.socialsensor.framework.client.search.solr;

import eu.socialsensor.framework.common.domain.dysco.Dysco;
import eu.socialsensor.framework.common.domain.dysco.Entity;
import eu.socialsensor.framework.common.domain.dysco.Entity.Type;
import eu.socialsensor.framework.common.domain.dysco.Ngram;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.solr.client.solrj.beans.Field;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class SolrDysco {

    public SolrDysco() {
        id = UUID.randomUUID().toString();
    }

    public SolrDysco(Dysco dysco) {

        id = dysco.getId();
      
        creationDate = dysco.getCreationDate();
        updateDate = dysco.getUpdateDate();
        if (dysco.getThumb() != null) {
            thumb = dysco.getThumb().toString();
        }
        title = dysco.getTitle();
        score = dysco.getScore();
        thumbs = dysco.getThumbs();
        keywords = dysco.getKeywords();
        evolution = dysco.getEvolution();

        trending = dysco.getTrending();
        alethiometerStatus = dysco.getAlethiometerStatus();
        tags  = dysco.getHashtags();
        people = dysco.getPeople();

        List<Ngram> dyscoNgrams = dysco.getNgrams();

        for (Ngram ngram : dyscoNgrams) {
            ngrams.add(ngram.getTerm() + "@@" + ngram.getScore());
        }

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
        //TODO add other dimensions - e.g. Sentiment etc 
    }
    @Field(value = "id")
    private String id;
    
    @Field(value = "creationDate")
    private Date creationDate;

    @Field(value = "updateDate")
    private Date updateDate;
    
    @Field(value = "thumb")
    private String thumb;
    
    @Field(value = "title")
    private String title;
    
    @Field(value = "dyscoScore")
    private Float score = 0f;
    
    @Field(value = "sentiment")
    private Float sentiment;
    
    @Field(value = "ngrams")
    private List<String> ngrams = new ArrayList<String>();
    
    //The following 6 map the "entities" field of Dysco
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
    
    //added 29.3.2013 for storing the thumbnails of the Dysco media links
    @Field(value = "thumbs")
    private List<String> thumbs = new ArrayList<String>();
    
    @Field(value = "keywords")
    private List<String> keywords = new ArrayList<String>();
    
    @Field(value = "evolution")
    private String evolution;
    
    @Field(value = "trending")
    private int trending;
    
    @Field(value = "alethiometerStatus")
    private int alethiometerStatus;
    
    @Field(value = "tags")
    private List<String> tags = new ArrayList<String>();
 
    @Field(value="people")
    private List<String> people = new ArrayList<String>();

    public Dysco toDysco() {

        Dysco dysco = new Dysco();
        dysco.setId(id);
        dysco.setTitle(title);
        dysco.setCreationDate(creationDate);
        dysco.setUpdateDate(updateDate);
        dysco.setThumbs(thumbs);
        dysco.setEvolution(evolution);
        dysco.setTrending(trending);
        dysco.setHashtags(tags);
        dysco.setPeople(people);

        if (thumb != null) {
            try {
                dysco.setThumb(new URL(thumb));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
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

        if (ngrams != null) {
            for (String ngram : ngrams) {
            }
        }
        dysco.setKeywords(keywords);
        dysco.setAlethiometerStatus(alethiometerStatus);
        //TODO add Dysco Dymensions

        return dysco;

    }

    public String getEvolution() {
        return evolution;
    }

    public void setEvolution(String evolution) {
        this.evolution = evolution;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getPersons() {
        return persons;
    }

    public void setPersons(List<String> persons) {
        this.persons = persons;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public List<String> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<String> organizations) {
        this.organizations = organizations;
    }

    public List<String> getPersonsScore() {
        return personsScore;
    }

    public void setPersonsScore(List<String> personsScore) {
        this.personsScore = personsScore;
    }

    public List<String> getLocationsScore() {
        return locationsScore;
    }

    public void setLocationsScore(List<String> locationsScore) {
        this.locationsScore = locationsScore;
    }

    public List<String> getOrganizationsScore() {
        return organizationsScore;
    }

    public void setOrganizationsScore(List<String> organizationsScore) {
        this.organizationsScore = organizationsScore;
    }

    public List<String> getThumbs() {
        return thumbs;
    }

    public void setThumbs(List<String> thumbs) {
        this.thumbs = thumbs;
    }

    public List<String> getNgrams() {
        return ngrams;
    }

    public void setNgrams(List<String> ngrams) {
        this.ngrams = ngrams;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getPeople() {
        return people;
    }

    public void setPeople(List<String> people) {
        this.people = people;
    }
    
    
}

package eu.socialsensor.framework.client.search.solr;

import java.net.URL;

import org.apache.solr.client.solrj.beans.Field;

import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.dysco.Entity;

import java.util.Date;
import java.util.List;

/**
 * @author cmartin - c.j.martin-dancausa@rgu.ac.uk
 */
public class SolrTopicDetectionItem {

    public SolrTopicDetectionItem() {
    }

    public SolrTopicDetectionItem(Item item) {
        id = item.getId();
        title = item.getTitle();
        
        tags = item.getTags();
        
        if (item.getEntities()!=null && item.getEntities().size()!=0)
        {
        	List<Entity> e = item.getEntities();
        	entities = new String[e.size()];
        
        	for(int i=0; i<e.size(); i++) {
        		entities[i] = e.get(i).getName();
        	}
        }
        else
        	entities = null;
        
        if (item.getLinks()!=null && item.getLinks().length!=0)
        {
        	URL[] links = item.getLinks();
        	urls = new String[links.length];
        	for(int i=0; i<links.length; i++)
        		urls[i] = links[i].toString();
        }
        else
        	urls = null;
        publicationTime = item.getPublicationTime();
    }
    
    @Field(value = "id")
    private String id;
    
    @Field(value = "title")
    private String title;
    
    @Field(value = "tags")
    private String[] tags;
    
    @Field(value = "entities")
    private String[] entities;
    
    @Field(value = "urls")
    private String[] urls;
    
    @Field(value = "publicationTime")
    private Long publicationTime;
    
    @Field(value="dyscoId")
    private String dyscoId;
    
    @Field(value="creationDate")
    private Date creationDate;
    
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    
    public Long getPublicationTime() {
        return publicationTime;
    }

    public void setPublicationTime(Long publicationTime) {
        this.publicationTime = publicationTime;
    }

    public String getDyscoId() {
        return dyscoId;
    }

    public void setDyscoId(String dyscoId) {
        this.dyscoId = dyscoId;
    }
    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String[] getTags() {
        return tags;
    }
    
    public String[] getEntities() {
        return entities;
    }

    public String[] getUrls() {
        return urls;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

}

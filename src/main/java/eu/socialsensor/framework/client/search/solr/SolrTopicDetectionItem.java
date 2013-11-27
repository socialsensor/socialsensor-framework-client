package eu.socialsensor.framework.client.search.solr;

import java.net.MalformedURLException;

import org.apache.solr.client.solrj.beans.Field;

import eu.socialsensor.framework.common.domain.Item;
import java.util.Date;

/**
 *
 * @author cmartin - c.j.martin-dancausa@rgu.ac.uk
 */
public class SolrTopicDetectionItem {

    public SolrTopicDetectionItem() {
    }

    public SolrTopicDetectionItem(Item item) {
        id = item.getId();
        title = item.getTitle();

        //this is long

        publicationTime = item.getPublicationTime();
    }

    public Item toItem() throws MalformedURLException {

        Item item = new Item();

        item.setId(id);
        item.setTitle(title);

        item.setPublicationTime(publicationTime);

        return item;
    }
    @Field(value = "id")
    private String id;
    @Field(value = "title")
    private String title;
    @Field(value = "timeslotId")
    private String timeslotId;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeslotId() {
        return timeslotId;
    }

    public void setTimeslotId(String timeslotId) {
        this.timeslotId = timeslotId;
    }
}

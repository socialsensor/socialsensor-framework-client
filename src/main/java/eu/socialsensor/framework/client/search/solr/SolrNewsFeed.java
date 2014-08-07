package eu.socialsensor.framework.client.search.solr;

import org.apache.solr.client.solrj.beans.Field;

import eu.socialsensor.framework.common.domain.Item;

public class SolrNewsFeed {
	
	public SolrNewsFeed(){
		
	}
	
	public SolrNewsFeed(Item item){
		id = item.getId();
        title = item.getTitle();
        description = item.getDescription();
        publicationTime = item.getPublicationTime();
        source = item.getUrl();
        lists = item.getList();
	}
	
	public Item toItem(){
		Item item = new Item();
		
		item.setId(id);
		item.setTitle(title);
		item.setDescription(description);
		item.setPublicationTime(publicationTime);
		item.setUrl(source);
		item.setList(lists);
		
		return item;
	}
	
	@Field(value = "id")
    private String id;
    @Field(value = "source")
    private String source;
    @Field(value = "title")
    private String title;
    @Field(value = "description")
    private String description;
    @Field(value = "publicationTime")
    private long publicationTime;
    @Field(value = "lists")
    private String[] lists;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    
    public Long getPublicationTime() {
        return publicationTime;
    }

    public void setPublicationTime(Long publicationTime) {
        this.publicationTime = publicationTime;
    }
}

package eu.socialsensor.framework.client.search.solr;

import org.apache.solr.client.solrj.beans.Field;

import eu.socialsensor.framework.common.domain.Document;

public class SolrDocument {
	
	
	public SolrDocument(){
		
	}
	
	public SolrDocument(Document document) {
		 id = document.getId();
	     sourceId = document.getSourceId();
	     title = document.getTitle();
	     content = document.getContent();
	     publicationTime = document.getPublicationTime();
	     url = document.getUrl();
	     category = document.getCategory();
	}
	 
	public Document toDocument(){
		Document document = new Document();
		
		document.setId(id);
		document.setTitle(title);
		document.setContent(content);
		document.setPublicationTime(publicationTime);
		document.setUrl(url);
		document.setCategory(category);
		
		return document;
	}
	
	@Field(value = "id")
    private String id;
    @Field(value = "sourceId")
    private String sourceId;
    @Field(value = "title")
    private String title;
    @Field(value = "content")
    private String content;
    @Field(value = "publicationTime")
    private long publicationTime;
    @Field(value = "url")
    private String url;
    @Field(value = "category")
    private String category;
    
    
    
    // Getters  & Setters for the fields of this class
	 
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public long getPublicationTime() {
        return publicationTime;
    }

    public void setPublicationTime(long publicationTime) {
        this.publicationTime = publicationTime;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

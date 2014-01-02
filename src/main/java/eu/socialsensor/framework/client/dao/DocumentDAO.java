package eu.socialsensor.framework.client.dao;

import java.util.List;

import eu.socialsensor.framework.common.domain.Document;

public interface DocumentDAO {
	
	public void insertDocument(Document document);
	
	public void updateDocument(Document document);
	
	public boolean deleteDocument(String id);
	
	public Document getDocument(String id);
	
	public List<Document> getDocumentsSince(long date);
	
    boolean exists(String id);
}

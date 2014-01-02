package eu.socialsensor.framework.client.dao.impl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.socialsensor.framework.client.dao.DocumentDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.client.mongo.Selector;
import eu.socialsensor.framework.client.mongo.UpdateItem;
import eu.socialsensor.framework.common.domain.Document;

public class DocumentDAOImpl implements DocumentDAO{
	List<String> indexes = new ArrayList<String>();
	private static String db;
    private static String collection;
    private MongoHandler mongoHandler;
	
	public DocumentDAOImpl(String host, String db, String collection) {

        indexes.add("id");
        indexes.add("publicationTime");
        indexes.add("indexed");
        indexes.add("original");

        try {
            mongoHandler = new MongoHandler(host, db, collection, indexes);

        } catch (UnknownHostException ex) {
            Logger.getRootLogger().error(ex.getMessage());
        }
    }
	
	@Override
    public void insertDocument(Document document) {
        mongoHandler.insert(document);
    }
	
	@Override
    public void updateDocument(Document document) {
        UpdateItem changes = new UpdateItem();
        
        changes.setField(document.getTitle());
        changes.setField(document.getContent());
        changes.setField(document.getUrl());
        changes.setField(String.valueOf(document.getPublicationTime()));
        changes.setField(document.getCategory());

        mongoHandler.update("id", document.getId(), changes);
    }
	
	@Override
    public boolean deleteDocument(String id) {
        return mongoHandler.delete("id", id);
    }
	
	@Override
    public Document getDocument(String id) {
        String json = mongoHandler.findOne("id", id);
        Document document = Document.create(json);
        return document;
    }
	
	@Override
    public List<Document> getDocumentsSince(long date) {
        Selector query = new Selector();
        query.selectGreaterThan("publicationTime", date);
        long l = System.currentTimeMillis();
        List<String> jsonItems = mongoHandler.findMany(query, 0);
        l = System.currentTimeMillis() - l;
       
        l = System.currentTimeMillis() - l;
        List<Document> results = new ArrayList<Document>();
        for (String json : jsonItems) {
            results.add(Document.create(json));
        }
        l = System.currentTimeMillis() - l;
        
        return results;
    }
	
	@Override
    public boolean exists(String id) {
        return mongoHandler.exists("id", id);
    }

}

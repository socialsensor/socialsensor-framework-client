package eu.socialsensor.framework.client.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.socialsensor.framework.client.dao.TopicDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.client.mongo.Selector;
import eu.socialsensor.framework.common.domain.Topic;
import eu.socialsensor.framework.common.factories.ItemFactory;


public class TopicDAOImpl implements TopicDAO{
	
	List<String> indexes = new ArrayList<String>();
    private static String host;
    private static String db = "Streams";
    private static String collection = "Topics";
    private MongoHandler mongoHandler;

    public TopicDAOImpl(String host) {
        this(host, db, collection);
    }

    public TopicDAOImpl(String host, String db) {
        this(host, db, collection);
    }

    public TopicDAOImpl(String host, String db, String collection) {
    	
        indexes.add("id");

        try {
            mongoHandler = new MongoHandler(host, db, collection, indexes);
            
        } catch (Exception ex) {
            Logger.getRootLogger().error(ex.getMessage());
        }
    }
    
    @Override
    public boolean deleteDB(){
    	return mongoHandler.delete();
    }
    
    @Override
    public void updateTopic(Topic topic) {
        mongoHandler.update("id", topic.getId(), topic);
    }
    
    @Override
    public List<Topic> readTopicsByStatus() {
    	Selector query = new Selector();
    	query.select("isRead", Boolean.FALSE);
    	List<String> jsonItems = mongoHandler.findMany(query, -1);
    	
    	List<Topic> topics = new ArrayList<Topic>();
    	
		Gson gson = new GsonBuilder()
    	.excludeFieldsWithoutExposeAnnotation()
    	.create();
		
		for(String json : jsonItems){
			
			Topic topic = ItemFactory.createTopic(json);
			
			topics.add(topic);
			
		}
		return topics;
    }

}

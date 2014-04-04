package eu.socialsensor.framework.client.dao.impl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.socialsensor.framework.client.dao.FeedDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.common.domain.Feed;
import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.factories.ItemFactory;

public class FeedDAOImpl implements FeedDAO{
	
	 List<String> indexes = new ArrayList<String>();
	 private MongoHandler mongoHandler;
	 
	 public FeedDAOImpl(String host, String db, String collection) throws Exception{
		 indexes.add("id");
		 try {
	            mongoHandler = new MongoHandler(host, db, collection, indexes);
	     } catch (UnknownHostException ex) {
	            Logger.getRootLogger().error(ex.getMessage());
	     }
	 }
	 
	 @Override
	 public void insertFeed(Feed feed){
		 String id = feed.getId();
		 mongoHandler.insert(feed);
	 }
	 
	 @Override
	 public boolean deleteFeed(Feed feed){
		 return mongoHandler.delete("id", feed.getId());
	 }
	 
	 @Override
	 public Feed getFeed(String id){
		 String json = mongoHandler.findOne("id", id);
		 Feed feed = ItemFactory.createFeed(json);
		 return feed;
	 }
	 
}

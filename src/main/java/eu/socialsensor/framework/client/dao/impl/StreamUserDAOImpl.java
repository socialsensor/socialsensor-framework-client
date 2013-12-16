package eu.socialsensor.framework.client.dao.impl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.socialsensor.framework.client.dao.StreamUserDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.client.mongo.UpdateItem;
import eu.socialsensor.framework.common.domain.StreamUser;
import eu.socialsensor.framework.common.factories.ItemFactory;

public class StreamUserDAOImpl implements StreamUserDAO {

    List<String> indexes = new ArrayList<String>();
    private final static String db = "Streams";
    private final static String collection = "StreamUsers";
    private MongoHandler mongoHandler;
    
    public StreamUserDAOImpl(String host) {
    	this(host, db, collection);
    }
    
    public StreamUserDAOImpl(String host, String db, String collection) {
    	indexes.add("id");
        indexes.add("userid");
        indexes.add("username");
        try {
            mongoHandler = new MongoHandler(host, db, collection, indexes);
        } catch (UnknownHostException ex) {
            Logger.getRootLogger().error(ex.getMessage());
        }
    }

    @Override
    public void insertStreamUser(StreamUser user) {
        mongoHandler.insert(user);
    }

    @Override
    public boolean exists(String id) {
        return mongoHandler.exists("id", id);
    }
    
    @Override
    public void updateStreamUserOld(StreamUser user) {
        Logger.getRootLogger().info("updating stream user old");
        mongoHandler.updateOld("id", user.getId(), user);
    }
    
    @Override
    public void updateStreamUser(StreamUser user) {
        Logger.getRootLogger().info("updating stream user new");
        mongoHandler.update("id", user.getId(), user);
    }

    @Override
    public void updateStreamUserPopularity(StreamUser user) {
    	String description = user.getDescription();
    	if(description != null) {
    		UpdateItem changes = new UpdateItem();
    		changes.setField("description", description);
    		mongoHandler.update("id", user.getId(), changes);
    	}
    }

    @Override
    public boolean deleteStreamUser(String id) {
        return mongoHandler.delete("userid", id);
    }

    @Override
    public StreamUser getStreamUser(String id) {
        String json = mongoHandler.findOne("id", id);
        StreamUser user = ItemFactory.createUser(json);
        return user;
    }
    
     @Override
    public StreamUser getStreamUserByName(String username) {
        String json = mongoHandler.findOne("username", username);
        StreamUser user = ItemFactory.createUser(json);
        return user;
    }

	@Override
	public void updateStreamUserMentions(String userid) {
    		UpdateItem changes = new UpdateItem();
    		changes.incField("mentions", 1);
    		mongoHandler.update("id", userid, changes);
	}
    
}

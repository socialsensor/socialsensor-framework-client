package eu.socialsensor.framework.client.dao.impl;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

import eu.socialsensor.framework.client.dao.StreamUserDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.client.mongo.UpdateItem;
import eu.socialsensor.framework.common.domain.StreamUser;
import eu.socialsensor.framework.common.domain.StreamUser.Category;
import eu.socialsensor.framework.common.factories.ItemFactory;

public class StreamUserDAOImpl implements StreamUserDAO {

    List<String> indexes = new ArrayList<String>();
    private final static String host = "";
    private final static String db = "Streams";
    private final static String collection = "StreamUsers";
    private MongoHandler mongoHandler;

    Map<String, Category> experts = new HashMap<String, Category>();
    
    public StreamUserDAOImpl() {
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
    	String userid = user.getUserid();
    	Category category = experts.get(userid);
    	if(category != null) {
    		user.setCategory(category);
    	}
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
        String userid = user.getUserid();
    	Category category = experts.get(userid);
    	if(category != null) {
    		user.setCategory(category);
    	}
        mongoHandler.update("id", user.getId(), user);
    }

    @Override
    public void updateStreamUserPopularity(StreamUser user) {
    	String userid = user.getUserid();
    	Category category = experts.get(userid);
    	if(category != null) {
    		UpdateItem changes = new UpdateItem();
    		changes.setField("category", category.toString());
    		mongoHandler.update("id", user.getId(), changes);
    	}
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
        String json = mongoHandler.findOne("userid", id);
        StreamUser user = ItemFactory.createUser(json);
        return user;
    }
    
     @Override
    public StreamUser getStreamUserByName(String username) {
        String json = mongoHandler.findOne("username", username);
        StreamUser user = ItemFactory.createUser(json);
        return user;
    }
    
    public static void main(String... args) {
        
        StreamUserDAO dao = new StreamUserDAOImpl();
        StreamUser user = dao.getStreamUser("317408851529981952");
        System.out.println("done: "+user.getId());
    }
    
    public void loadExpertsList(String file, Category category) {
		try {
			StringBuffer sb = new StringBuffer();
			List<String> lines = IOUtils.readLines(new FileInputStream(file));
			for(String line : lines) {
				sb.append(line);
			}
			Map<?, ?> json = new Gson().fromJson(sb.toString(), Map.class);
			@SuppressWarnings("unchecked")
			List <Double> ids = (List<Double>) json.get("TwitterIds");
			for(Double id : ids) {
				BigDecimal big = new BigDecimal(id);
				experts.put(big.toString(), category);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}

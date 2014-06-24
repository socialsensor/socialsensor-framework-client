/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.socialsensor.framework.client.dao.impl;

import eu.socialsensor.framework.client.dao.KeywordDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.common.domain.Keyword;
import eu.socialsensor.framework.common.domain.SocialNetworkSource;
import eu.socialsensor.framework.common.factories.ItemFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 *
 * @author etzoannos
 */
public class KeywordDAOImpl implements KeywordDAO {

    List<String> indexes = new ArrayList<String>();
    private static String db = "Streams";
    private static String collection = "keywords";
    private MongoHandler mongoHandler;

    public KeywordDAOImpl(String host) {
    	this(host, db, collection);
    }
    
    public KeywordDAOImpl(String host, String db, String collection) {
        try {
            indexes.add("score");
            indexes.add("dyscoId");
            indexes.add("keyword");
            mongoHandler = new MongoHandler(host, db, collection, indexes);
            
            mongoHandler.sortBy("score", 1);
        } catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public void removeKeyword(String keyword) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("keyword", keyword);
        mongoHandler.delete(map);
    }

    @Override
	public void removeKeyword(String keyword, SocialNetworkSource sourceType) {
    	Map<String, Object> map = new HashMap<String, Object>();
        map.put("keyword", keyword);
        if(sourceType != SocialNetworkSource.All) {
        	map.put("network", sourceType);
        }
        mongoHandler.delete(map);
	}
    
    @Override
    public void insertKeyword(String keyword, double score) {
        Map<String, Object> map = new HashMap<String, Object>();
        String id = SocialNetworkSource.All+"::"+keyword;
        map.put("_id", id);
        map.put("keyword", keyword);
        map.put("score", score);
        map.put("timestamp", System.currentTimeMillis());
        if(mongoHandler.exists("_id", id))
        	mongoHandler.update("_id", id, map);
        else
        	mongoHandler.insert(map);
    }

    @Override
	public void insertKeyword(String keyword, double score, SocialNetworkSource sourceType) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	String id = sourceType+"::"+keyword;
        map.put("_id", id);
        map.put("keyword", keyword);
        map.put("score", score);
        map.put("network", sourceType);
        map.put("timestamp", System.currentTimeMillis());
        if(mongoHandler.exists("_id", id))
        	mongoHandler.update("_id", id, map);
        else
        	mongoHandler.insert(map);
	}

    @Override
	public void insertKeyword(Keyword keyword, SocialNetworkSource sourceType) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	String id = sourceType.toString()+"::"+keyword.getName();
        map.put("_id", id);
        map.put("keyword", keyword.getName());
        map.put("score", keyword.getScore());
        map.put("network", sourceType.toString());
        map.put("label", keyword.getLabel());
        map.put("timestamp", System.currentTimeMillis());
        if(mongoHandler.exists("_id", id))
        	mongoHandler.update("_id", id, map);
        else
        	mongoHandler.insert(map);
	}
    
    @Override
    public void instertDyscoKeyword(String dyscoId, String keyword, float score) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("keyword", keyword);
        map.put("score", score);
        map.put("dyscoId", dyscoId);
        mongoHandler.insert(map);
    }

	@Override
	public List<Keyword> findTopKeywords(int n) {
		List<Keyword> keywords = new ArrayList<Keyword>();
		
		List<String> res = mongoHandler.findMany(n);
		for(String json : res) {
			keywords.add(ItemFactory.createKeyword(json));
		}
		return keywords;
	}

	@Override
	public List<Keyword> findKeywords(SocialNetworkSource sourceType) {
		List<Keyword> keywords = new ArrayList<Keyword>();
		
		DBObject query = new BasicDBObject("network", sourceType.toString());
		
		List<String> res = mongoHandler.findMany(query, -1);
		for(String json : res) {
			Keyword keyword = ItemFactory.createKeyword(json);
			keywords.add(keyword);
		}
		return keywords;
	}
	
}

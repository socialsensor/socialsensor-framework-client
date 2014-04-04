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

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        } catch (UnknownHostException ex) {
            org.apache.log4j.Logger.getRootLogger().error(ex.getMessage());
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
        	map.put("source", sourceType);
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
        mongoHandler.update("_id", id, map);
    }

    @Override
	public void insertKeyword(String keyword, double score, SocialNetworkSource sourceType) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	String id = sourceType+"::"+keyword;
        map.put("_id", id);
        map.put("keyword", keyword);
        map.put("score", score);
        map.put("source", sourceType);
        map.put("timestamp", System.currentTimeMillis());
        mongoHandler.update("_id", id, map);
	}

    @Override
	public void insertKeyword(Keyword keyword, SocialNetworkSource sourceType) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	String id = sourceType.toString()+"::"+keyword;
        map.put("_id", id);
        map.put("keyword", keyword.getName());
        map.put("score", keyword.getScore());
        map.put("source", sourceType.toString());
        map.put("timestamp", System.currentTimeMillis());
        mongoHandler.update("_id", id, map);
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
	
}

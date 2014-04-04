/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.socialsensor.framework.client.dao.impl;

import eu.socialsensor.framework.client.dao.SourceDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.common.domain.SocialNetworkSource;
import eu.socialsensor.framework.common.domain.Source;
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
public class SourceDAOImpl implements SourceDAO {

    List<String> indexes = new ArrayList<String>();

    private static String db = "test";
    private static String collection = "sources";
    private MongoHandler mongoHandler;

    
    public SourceDAOImpl(String host) {
    	this(host, db, collection);
    }

    public SourceDAOImpl(String host, String db, String collection) {
        try {
            indexes.add("score");
            indexes.add("id");
            indexes.add("name");
            mongoHandler = new MongoHandler(host, db, collection, indexes);
            
            mongoHandler.sortBy("score", 1);
        } catch (Exception ex) {
            org.apache.log4j.Logger.getRootLogger().error(ex.getMessage());
        }
    }
    
    @Override
    public void removeSource(Source source) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", source.getName());
        mongoHandler.delete(map);
    }

    @Override
	public void removeSource(Source source, SocialNetworkSource sourceType) {
    	Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", source.getName());
        if(sourceType != SocialNetworkSource.All) {
        	map.put("source", sourceType);
        }
        mongoHandler.delete(map);
	}
    
    @Override
    public void insertSource(String name, float score) {
        Map<String, Object> map = new HashMap<String, Object>();
        String id = SocialNetworkSource.All+"::"+name;
        map.put("_id", id);
        map.put("name", name);
        map.put("score", score);
        map.put("timestamp", System.currentTimeMillis());
        mongoHandler.update("_id", id, map);
    }

    @Override
    public void insertSource(Source source) {
        Map<String, Object> map = new HashMap<String, Object>();
        String id = SocialNetworkSource.All+"::"+source.getName();
        map.put("_id", id);
        map.put("name", source.getName());
        map.put("score", source.getScore());
        map.put("timestamp", System.currentTimeMillis());
        mongoHandler.update("_id", id, map);
    }
    
    @Override
	public void insertSource(String name, float score, SocialNetworkSource snSource) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	String id = snSource.toString()+"::"+name;
        map.put("_id", id);
        map.put("name", name);
        map.put("score", score);
        map.put("source", snSource.toString());
        map.put("timestamp", System.currentTimeMillis());
        mongoHandler.update("_id", id, map);
	}

    @Override
	public void insertSource(Source source, SocialNetworkSource sourceType) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	String id = source.toString()+"::"+source.getName();
        map.put("_id", id);
        map.put("name", source.getName());
        map.put("score", source.getScore());
        map.put("source", source.toString());
        map.put("timestamp", System.currentTimeMillis());
        mongoHandler.update("_id", id, map);
	}
    
    @Override
    public void instertDyscoSource(String dyscoId, String name, float score) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("score", score);
        map.put("dyscoId", dyscoId);
        mongoHandler.insert(map);
    }

	@Override
	public List<Source> findTopSources(int n) {
		List<Source> sources = new ArrayList<Source>();
		
		List<String> res = mongoHandler.findMany(n);
		for(String json : res) {
			sources.add(ItemFactory.createSource(json));
		}
		return sources;
	}
	
	@Override
	public List<Source> findTopSources(int n, SocialNetworkSource sourceType) {
		List<Source> sources = new ArrayList<Source>();
		
		List<String> res = mongoHandler.findMany("source", sourceType.toString(), n);
		for(String json : res) {
			sources.add(ItemFactory.createSource(json));
		}
		return sources;
	}
	
}

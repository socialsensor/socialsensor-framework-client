package eu.socialsensor.framework.client.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.socialsensor.framework.client.dao.RssSourceDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;

public class RssSourceDAOImpl implements RssSourceDAO {

	 private MongoHandler mongoHandler;

	public RssSourceDAOImpl(String host, String db, String collection) {
	        try {
	            mongoHandler = new MongoHandler(host, db, collection, null);
	        } catch (Exception ex) {
	            org.apache.log4j.Logger.getRootLogger().error(ex.getMessage());
	        }
	    }
	 
	@Override
	public List<String> getRssSources() {
		List<String> rssSources = new ArrayList<String>();
		List<String> jsons = mongoHandler.findMany(-1);
		for(String raw : jsons) {
			DBObject rssSource = (DBObject)JSON.parse(raw);
			if(rssSource != null) {
				rssSources.add((String) rssSource.get("source"));
			}
		}
		return rssSources;
	}
	
}


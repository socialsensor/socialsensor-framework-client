package eu.socialsensor.framework.client.dao.impl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.socialsensor.framework.client.dao.MediaSharesDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.common.domain.MediaShare;
import eu.socialsensor.framework.common.factories.ItemFactory;

public class MediaSharesDAOImpl implements MediaSharesDAO {

	private List<String> indexes = new ArrayList<String>();
	
	private MongoHandler mongoHandler = null;
	
	public MediaSharesDAOImpl(String host, String db, String collection) {
        indexes.add("reference");
        indexes.add("publicationTime");

        try {
            mongoHandler = new MongoHandler(host, db, collection, indexes);
            mongoHandler.sortBy("publicationTime", MongoHandler.DESC);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ItemDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
			e.printStackTrace();
		}
    }

	
	@Override
	public void addMediaShare(String id, String originalId,
			long publicationTime, String userid) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("reference", originalId);
		map.put("publicationTime", publicationTime);
		map.put("userid", userid);
		
		mongoHandler.insert(map);
	}

	@Override
	public List<MediaShare> getMediaShares(String mediaId) {
		
		DBObject query = new BasicDBObject("id", mediaId);
		List<String> response = mongoHandler.findMany(query , -1);
		
		List<MediaShare> mediaShares = new ArrayList<MediaShare>();
		for(String json : response) {
			MediaShare ms = ItemFactory.createMediaShare(json);
			mediaShares.add(ms);
		}
		return mediaShares;
	}

}

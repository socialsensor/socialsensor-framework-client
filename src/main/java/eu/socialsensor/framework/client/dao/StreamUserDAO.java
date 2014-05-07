package eu.socialsensor.framework.client.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mongodb.DBObject;

import eu.socialsensor.framework.client.dao.ItemDAO.ItemIterator;
import eu.socialsensor.framework.client.mongo.MongoHandler.MongoIterator;
import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.StreamUser;
import eu.socialsensor.framework.common.domain.StreamUser.Category;
import eu.socialsensor.framework.common.factories.ItemFactory;

/**
 * Data Access Object for Item
 *
 * @author etzoannos
 */
public interface StreamUserDAO {

    public void insertStreamUser(StreamUser user);

    public void updateStreamUser(StreamUser user);

    public void updateStreamUserOld(StreamUser user);

    public void updateStreamUserPopularity(StreamUser user);

    public void updateStreamUserStatistics(StreamUser user);
    
    public void incStreamUserValue(String id, String field);
    
    public void incStreamUserValue(String id, String field, int value);
    
    public boolean deleteStreamUser(String id);

    public StreamUser getStreamUser(String id);
    
    public Map<String, StreamUser> getStreamUsers(List<String> ids);

    public StreamUser getStreamUserByName(String username);
    
    public boolean exists(String id);
    
    public StreamUserIterator getIterator(DBObject query);

    public class StreamUserIterator implements Iterator<StreamUser> {

		private MongoIterator it;

		public StreamUserIterator (MongoIterator it) {
    		this.it = it;
    	}
		
    	public StreamUser next() {
    		String json = it.next();
    		return ItemFactory.createUser(json);
    	}
    	
    	public boolean hasNext() {
    		return it.hasNext();
    	}

		@Override
		public void remove() {
			it.next();
		}
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.socialsensor.framework.client.dao;

import java.util.Iterator;
import java.util.List;

import com.mongodb.DBObject;

import eu.socialsensor.framework.client.mongo.UpdateItem;
import eu.socialsensor.framework.client.mongo.MongoHandler.MongoIterator;
import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.MediaItem;
import eu.socialsensor.framework.common.factories.ItemFactory;

/**
 *
 * @author etzoannos
 */
public interface MediaItemDAO {

    public void addMediaItem(MediaItem item);

    public void updateMediaItem(String id, String name, Object value);

	public void updateMediaItem(String id, UpdateItem updates);
	
    public void updateMediaItem(MediaItem item);
    
    public void updateMediaItemPopularity(MediaItem item);

    public boolean removeMediaItem(String mediaItemId);

    public MediaItem getMediaItem(String mediaItemId);

    public List<MediaItem> getLastMediaItems(int size);

    public List<MediaItem> getLastMediaItems(long fromDate);

    public List<MediaItem> getLastMediaItemsWithGeo(int size);

    public List<MediaItem> getLastMediaItemsWithGeo(long fromDate);

    public List<MediaItem> getLastNIndexedMediaItems(int size);

    public List<MediaItem> getRecentIndexedMediaItems(long fromDate);

    public boolean exists(MediaItem item);

    public boolean exists(String id);

    public void updateMediaItemDyscoId(String url, String dyscoId);

    public List<MediaItem> getMediaItemsByDysco(String dyscoId, String mediaType, int size);

    public List<MediaItem> getMediaItemsByUrls(List<String> url, String mediaType, int size);

    public List<MediaItem> getMediaItemsByDyscos(List<String> dyscoIds, String mediaType, int size);

    public List<MediaItem> getMediaItemsForItems(List<String> itemIds, String mediaType, int size);

    public List<MediaItem> getMediaItemsForUrls(List<String> urls, String mediaType, int size);

    List<MediaItem> getUnindexedItems(int max);
    
    public MediaItemIterator getIterator(DBObject query);
    
    public class MediaItemIterator implements Iterator<MediaItem> {

		private MongoIterator it;

		public MediaItemIterator (MongoIterator it) {
    		this.it = it;
    	}
		
    	public MediaItem next() {
    		String json = it.next();
    		return ItemFactory.createMediaItem(json);
    	}
    	
    	public boolean hasNext() {
    		return it.hasNext();
    	}

		@Override
		public void remove() {
			it.next();
		}
    }

	void updateMediaItemShares(String id, int shares);
    	
}

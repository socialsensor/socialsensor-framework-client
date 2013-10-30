package eu.socialsensor.framework.client.dao;

import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.Topic;

import java.util.List;

/**
 * Data Access Object for Item
 *
 * @author etzoannos
 */
public interface ItemDAO {

    public void insertItem(Item item);

    public void updateItem(Item item);
    
    public void updateTopic(Topic topic);

    public void updateItemCommentsAndPopularity(Item item);

    public boolean deleteItem(String id);

    public Item getItem(String id);
    
    public List<Item> getLatestItems(int n);
    
    public int getUserRetweets(String userName);
    
    public List<Item> getItemsSince(long date);
    
    public List<Item> getItemsInTimeslot(String timeslotId);

	boolean exists(String id);
	
	public List<Item> readItems();
	
	public List<Item> readItemsByStatus();
	
	public List<Topic> readTopicsByStatus();
    
}

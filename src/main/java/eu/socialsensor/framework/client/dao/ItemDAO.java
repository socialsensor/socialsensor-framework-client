package eu.socialsensor.framework.client.dao;

import eu.socialsensor.framework.common.domain.Item;

import java.util.List;

/**
 * Data Access Object for Item
 *
 * @author etzoannos
 */
public interface ItemDAO {

    public void insertItem(Item item);

    public void replaceItem(Item item);

    public void updateItem(Item item);

    public boolean deleteItem(String id);

    public boolean deleteDB();

    public Item getItem(String id);

    public List<Item> getLatestItems(int n);

    public List<Item> getItemsSince(long date);

    public List<Item> getItemsInTimeslot(String timeslotId);

    boolean exists(String id);

    public List<Item> readItems();

    public List<Item> readItemsByStatus();

    public List<Item> getItemsInRange(long start, long end);

    public void setIndexedStatusTrue(String itemId);

    public List<Item> getUnindexedItems(int max);
    
}

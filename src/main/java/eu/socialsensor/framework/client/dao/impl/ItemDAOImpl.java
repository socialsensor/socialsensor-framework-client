package eu.socialsensor.framework.client.dao.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import eu.socialsensor.framework.client.dao.ItemDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.client.mongo.Selector;
import eu.socialsensor.framework.client.mongo.UpdateItem;
import eu.socialsensor.framework.common.domain.DyscoRequest;
import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.factories.ItemFactory;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class ItemDAOImpl implements ItemDAO {

    List<String> indexes = new ArrayList<String>();
    private static String host = "";
    private static String db = "Streams";
    private static String collection = "Items";
    private MongoHandler mongoHandler;

    public ItemDAOImpl() {
        this(host, db, collection);
    }

    public ItemDAOImpl(String host) {
        this(host, db, collection);
    }

    public ItemDAOImpl(String host, String db) {
        this(host, db, collection);
    }

    public ItemDAOImpl(String host, String db, String collection) {
        indexes.add("id");
        indexes.add("publicationTime");
        //indexes.add("title");
        indexes.add("timeslotId");

        try {
            mongoHandler = new MongoHandler(host, db, collection, indexes);
            System.out.println("List time2: Connect");
        } catch (UnknownHostException ex) {
            Logger.getRootLogger().error(ex.getMessage());
        }
    }

    @Override
    public void insertItem(Item item) {
        mongoHandler.insert(item);
    }

    @Override
    public void updateItem(Item item) {
        mongoHandler.update("id", item.getId(), item);
    }

    @Override
    public void updateItemCommentsAndPopularity(Item item) {
        UpdateItem changes = new UpdateItem();

        String[] comments = item.getComments();
        if (comments != null && comments.length > 0) {
            changes.addValues("comments", comments);
        }

        Map<String, Integer> popularity = item.getPopularity();
        if (popularity != null && popularity.size() > 0) {
            changes.setField("popularity", popularity);
        }

        mongoHandler.update("id", item.getId(), changes);
    }

    @Override
    public boolean deleteItem(String id) {
        return mongoHandler.delete("id", id);
    }

    @Override
    public List<Item> getLatestItems(int n) {
        List<String> jsonItems = mongoHandler.findManySortedByPublicationTime(new Selector(), n);
        List<Item> results = new ArrayList<Item>();
        for (String json : jsonItems) {
            results.add(ItemFactory.create(json));
        }
        return results;
    }

    @Override
    public int getUserRetweets(String userName) {
        Pattern user = Pattern.compile("^RT @" + userName);
        return mongoHandler.findCount(user);
//        List<Item> results = new ArrayList<Item>();
//
//        for (String json : jsonItems) {
//            try {
//                results.add(ItemFactory.create(json));
//            } catch (Exception e) {
//                Logger.getRootLogger().warn("Ignore incomplete tweet.");
//            }
//        }
//        return results;
    }

    @Override
    public List<Item> getItemsSince(long date) {
        Selector query = new Selector();
        query.selectGreaterThan("publicationTime", date);
        long l = System.currentTimeMillis();
        List<String> jsonItems = mongoHandler.findMany(query, 0);
        l = System.currentTimeMillis() - l;
        System.out.println("Fetch time: " + l + " msecs");
        l = System.currentTimeMillis() - l;
        List<Item> results = new ArrayList<Item>();
        for (String json : jsonItems) {
            results.add(ItemFactory.create(json));
        }
        l = System.currentTimeMillis() - l;
        System.out.println("List time: " + l + " msecs");
        return results;
    }

    @Override
    public Item getItem(String id) {
        String json = mongoHandler.findOne("id", id);
        Item item = ItemFactory.create(json);
        return item;
    }


    @Override
    public boolean exists(String id) {
        return mongoHandler.exists("id", id);
    }


    @Override
    public List<Item> getItemsInTimeslot(String timeslotId) {
        System.out.println("DAO: get items from timeslot: " + timeslotId);
        long l = System.currentTimeMillis();

        BasicDBObject query = new BasicDBObject("timeslotId", timeslotId);
        List<String> jsonItems = mongoHandler.findMany(query, 0);
        
        List<Item> results = new ArrayList<Item>();
        
        System.out.println("DAO: find " + jsonItems.size() + " results");
        
        for (String json : jsonItems) {
            results.add(ItemFactory.create(json));
        }

        l = System.currentTimeMillis() - l;
        System.out.println("List time2: " + l + " msecs " + timeslotId + " items: " + jsonItems.size());

        return results;
    }
    
    public List<Item> readItems() {
    	List<String> jsonItems = mongoHandler.findMany(-1);
    	System.out.println("I have read "+jsonItems.size()+" jsonItems");
    	List<Item> items = new ArrayList<Item>();
    	
		Gson gson = new GsonBuilder()
    	.excludeFieldsWithoutExposeAnnotation()
    	.create();
		
		for(String json : jsonItems){
			
			Item item = ItemFactory.create(json);
			
			items.add(item);
			
		}
		return items;
    }
    
    public List<Item> readItemsByStatus(){
    	 Selector query = new Selector();
         query.select("isSearched", Boolean.FALSE);
         List<String> jsonItems = mongoHandler.findMany(query, -1);
         List<Item> items = new ArrayList<Item>();
         Gson gson = new GsonBuilder()
                 .excludeFieldsWithoutExposeAnnotation()
                 .create();

         for (String json : jsonItems) {
             Item item = gson.fromJson(json, Item.class);
             items.add(item);
         }

         return items;
    }
    
    
    public static void main(String... args) {
        ItemDAOImpl dao = new ItemDAOImpl();
        List<Item> items = dao.getItemsInTimeslot("cqd4epdouktv7");
        for (Item item: items) {
            System.out.println(item.getId());
        }
        
    }
    
    
}

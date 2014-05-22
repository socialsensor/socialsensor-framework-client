package eu.socialsensor.framework.client.dao.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.socialsensor.framework.client.dao.ItemDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.client.mongo.MongoHandler.MongoIterator;
import eu.socialsensor.framework.client.mongo.Selector;
import eu.socialsensor.framework.client.mongo.UpdateItem;
import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.factories.ItemFactory;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class ItemDAOImpl implements ItemDAO {

    List<String> indexes = new ArrayList<String>();
    private static String db = "Streams";
    private static String collection = "Items";
    private MongoHandler mongoHandler;

    public ItemDAOImpl(String host) throws Exception {
        this(host, db, collection);
    }

    public ItemDAOImpl(String host, String db) throws Exception {
        this(host, db, collection);
    }

    public ItemDAOImpl(String host, String db, String collection) throws Exception {

        indexes.add("id");
        indexes.add("publicationTime");
        indexes.add("insertionTime");

        mongoHandler = new MongoHandler(host, db, collection, indexes);

    }

    @Override
    public void insertItem(Item item) {
        mongoHandler.insert(item);
    }

    @Override
    public void replaceItem(Item item) {
        mongoHandler.update("id", item.getId(), item);
    }

    @Override
    public void updateItem(Item item) {
        UpdateItem changes = new UpdateItem();
        changes.setField("likes", item.getLikes());
        changes.setField("shares", item.getShares());
//        changes.setField("indexed", item.isIndexed());

        mongoHandler.update("id", item.getId(), changes);
    }

    @Override
    public void setIndexedStatusTrue(String itemId) {
        UpdateItem changes = new UpdateItem();
        changes.setField("indexed", Boolean.TRUE);

        mongoHandler.update("id", itemId, changes);
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

    //@Override
    //public int getUserRetweets(String userName) {
    //    Pattern user = Pattern.compile("^RT @" + userName);
    //    return mongoHandler.findCount(user);
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
    //}
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
    public List<Item> getItemsInRange(long start, long end) {

        Selector query = new Selector();
        query.selectGreaterThan("insertionTime", start);
        query.selectLessThan("insertionTime", end);
        long l = System.currentTimeMillis();
        List<String> jsonItems = mongoHandler.findManyNoSorting(query, 0);
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

    @Override
    public List<Item> getUnindexedItems(int max) {
        Selector query = new Selector();
        query.select("indexed", Boolean.FALSE);
        query.select("original", Boolean.TRUE);

        List<String> jsonItems = mongoHandler.findManyNoSorting(query, max);
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

    @Override
    public List<Item> readItems() {
        List<String> jsonItems = mongoHandler.findMany(-1);
        System.out.println("I have read " + jsonItems.size() + " jsonItems");
        List<Item> items = new ArrayList<Item>();

        for (String json : jsonItems) {

            Item item = ItemFactory.create(json);

            items.add(item);

        }
        return items;
    }

    @Override
    public List<Item> readItemsByStatus() {
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

        ItemDAO dao = null;
        try {
            dao = new ItemDAOImpl("social1.atc.gr", "Streams", "Items");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        List<Item> items = dao.getUnindexedItems(100);

        Logger.getRootLogger().info("splitting in lists");

        List<Item> list1 = new ArrayList<Item>();
        List<Item> list2 = new ArrayList<Item>();
        List<Item> list3 = new ArrayList<Item>();

        for (Item _item : items) {

            String[] lists = _item.getList();

            for (String aList : lists) {

                if (aList.equals("1")) {
                    list1.add(_item);
                } else if (aList.equals("2")) {
                    list2.add(_item);
                } else if (aList.equals("3")) {
                    list3.add(_item);
                }

            }
        }

        for (Item item : items) {

            if (item.isOriginal() == true) {
                System.out.println("fine");
            }
            if (item.isOriginal() == false) {
                System.out.println("problem!! it's false");
            }
//            System.out.println(item.getId());
//            dao.setIndexedStatusTrue(item.getId());
        }

        System.out.println("finished");

//        ItemDAO dao = new ItemDAOImpl("160.40.50.207");
//        MediaItemDAO mDao = new MediaItemDAOImpl("160.40.50.207");
//        StreamUserDAO uDao = new StreamUserDAOImpl("160.40.50.207");
//
//        long end = 1386856206000L;
//        long start = end - 5 * 60000;//1386856100000L;
//
//
//        List<Item> items = dao.getItemsInRange(start, end);
//        System.out.println(items.size() + " in " + ((end - start) / 60000.0) + " minutes");
//
//        long t = System.currentTimeMillis();
//
//        for (Item item : items) {
//            String uid = item.getUserId();
//            StreamUser streamUser = uDao.getStreamUser(uid);
//            item.setStreamUser(streamUser);
//
//            List<MediaItem> mItems = new ArrayList<MediaItem>();
//
//            List<String> mediaIds = item.getMediaIds();
//            for (String mId : mediaIds) {
//                MediaItem mItem = mDao.getMediaItem(mId);
//                mItems.add(mItem);
//            }
//            item.setMediaItems(mItems);
//        }
//        t = System.currentTimeMillis() - t;
//
//        System.out.println("Fetch users and MediaItems in " + t + " msecs");
    }

    @Override
    public ItemIterator getIterator(DBObject query
    ) {
        MongoIterator it = mongoHandler.getIterator(query);
        return new ItemIterator(it);
    }

    @Override
    public List<Item> getItems(DBObject query
    ) {
        List<Item> items = new ArrayList<Item>();
        List<String> results = mongoHandler.findMany(query, -1);

        for (String json : results) {
            Item item = ItemFactory.create(json);
            items.add(item);
        }
        return items;
    }

}

package eu.socialsensor.framework.client.seedlist;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;


public class SeedlistsManager {
	
	
	
	private static String DATABASE = "SeedlistManager";
	private static String COLLECTION = "SeedlistCollection";

	private static String SEEDLIST = "seedlist";
	private static String TYPE = "type";
	private static String STATUS = "status";
	
	private static String LINKS = "links";
	private static String MEDIA_LINKS = "media_links";

	private static String PROCESSED = "processed";
	private static String UNPROCESSED = "unprocessed";
	
	
	
	MongoClient mongo;
    DB db;
    DBCollection collection;

	public SeedlistsManager(String host) throws IOException {	
		this.mongo = new MongoClient(host, 27017);
        this.db = mongo.getDB(DATABASE);
        this.collection = db.getCollection(COLLECTION);
  
        collection.ensureIndex("type");
        collection.ensureIndex("status");
        
        Logger.getRootLogger().info("SeedlistManager is up and running...");
	}
	
	
	public void addMedialinksSeedlist(String seedlist) throws IOException {
		DBObject object = new BasicDBObject();
		object.put(SEEDLIST, seedlist);
		object.put(TYPE, MEDIA_LINKS);
		object.put(STATUS, UNPROCESSED);
        collection.insert(object);
	}
	
	public void addLinksSeedlist(String seedlist) throws IOException {
		DBObject object = new BasicDBObject();
		object.put("seedlist", seedlist);
		object.put("type", LINKS);
		object.put("status", UNPROCESSED);
        collection.insert(object);
	}
	
	public List<String> getMedialinksSeedlists() throws IOException {
		List<String> seedlists = new ArrayList<String>();
		DBObject query = new BasicDBObject();
		query.put(TYPE, MEDIA_LINKS);
		query.put(STATUS, UNPROCESSED);
		DBCursor cursor = collection.find(query);
        try {
            while (cursor.hasNext()) {
            	DBObject entry = cursor.next();
            	String seedlist = (String) entry.get(SEEDLIST);
            	if(seedlist != null)
            		seedlists.add(seedlist);
            }
        } finally {
            cursor.close();
        }
		return seedlists;
	}
	
	public List<String> getLinksSeedlists() throws IOException {
		List<String> seedlists = new ArrayList<String>();
		DBObject query = new BasicDBObject();
		query.put(TYPE, LINKS);
		query.put(STATUS, UNPROCESSED);
		DBCursor cursor = collection.find(query);
        try {
            while (cursor.hasNext()) {
            	DBObject entry = cursor.next();
            	String seedlist = (String) entry.get(SEEDLIST);
            	if(seedlist != null)
            		seedlists.add(seedlist);
            }
        } finally {
            cursor.close();
        }
		return seedlists;
	}
	
	public void removeSeedlists(List<String> seedlists) throws IOException {
		for(String seedlist : seedlists) {
			DBObject q = new BasicDBObject(SEEDLIST, seedlist);
			DBObject field = new BasicDBObject(STATUS, PROCESSED);
			DBObject o = new BasicDBObject( "$set" , field);
			
			collection.update(q, o);
		}
	}
	
	public static void main(String[] args) {

		try {
			
			if(args.length != 2) {
				System.out.println("RUN: java -jar SeedlistsManager <hostname> <type>");
				System.exit(1);
			}
			SeedlistsManager seedlistManager = new SeedlistsManager(args[0]);
			
			List<String> lists = null;
			String type = args[1];
			if(type.equals("media")) {
				lists = seedlistManager.getMedialinksSeedlists();
			}
			else if(type.equals("links")) {
				lists = seedlistManager.getLinksSeedlists();
			}
			else {
				System.out.println("Type arguments must be 'media' or 'links'");
				System.exit(1);
			}
			
			String seedlists = "";
			for(String seedlist : lists) {
				seedlists += (seedlist + " ");
			}
			System.out.println(seedlists);
				
		} catch (IOException e) {
			System.exit(1);
		}
		
	}

}

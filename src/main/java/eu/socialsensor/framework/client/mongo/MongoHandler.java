package eu.socialsensor.framework.client.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
import eu.socialsensor.framework.common.domain.JSONable;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author etzoannos
 *
 */
public class MongoHandler {

	// IMPORTANT: This should be change after the review
	private DBObject exclude = new BasicDBObject("lastUpdated", 0);
	
    DBCollection collection;
    private DBObject sortField = new BasicDBObject("_id", -1);
    private DBObject publicationTimeField = new BasicDBObject("publicationTime", -1);
    public static int ASC = 1;
    public static int DESC = -1;
    private DB db = null;
    private static Map<String, MongoClient> connections = new HashMap<String, MongoClient>();
    private static Map<String, DB> databases = new HashMap<String, DB>();

    public MongoHandler(String host, String dbName, String collectionName, List<String> indexes) throws UnknownHostException {
        this(host, dbName);
        collection = db.getCollection(collectionName);
        if (indexes != null) {
            for (String index : indexes) {
                collection.ensureIndex(index);
            }
        }
    }

    public MongoHandler(String hostname, String dbName)
            throws UnknownHostException {
        String connectionKey = hostname + "#" + dbName;

        db = databases.get(connectionKey);
        if (db == null) {
            MongoClient mongo = connections.get(hostname);
            if (mongo == null) {
                mongo = new MongoClient(hostname);
                connections.put(hostname, mongo);
            }
            db = mongo.getDB(dbName);
            databases.put(connectionKey, db);
        }


    }

    public void sortBy(String field, int order) {
        this.sortField = new BasicDBObject(field, order);
    }

    public void insert(JSONable jsonObject, String collName) {
        String json = jsonObject.toJSONString();
        DBObject object = (DBObject) JSON.parse(json);
        DBCollection coll = db.getCollection(collName);
        coll.insert(object);
    }

    public void insert(JSONable jsonObject) {
        String json = jsonObject.toJSONString();
        DBObject object = (DBObject) JSON.parse(json);
        collection.insert(object);
    }

    public void insert(Map<String, Object> map) {
        collection.insert(new BasicDBObject(map));
    }

    public boolean exists(String fieldName, String fieldValue) {
        BasicDBObject query = new BasicDBObject(fieldName, fieldValue);
        DBObject result = collection.findOne(query, new BasicDBObject("_id", 1));
        if (result == null) {
            return false;
        }
        return true;
    }

    public String findOne() {

        DBObject result = collection.findOne(new BasicDBObject(), exclude);

        return JSON.serialize(result);
    }

    public String findOne(String fieldName, String fieldValue) {
        BasicDBObject query = new BasicDBObject(fieldName, fieldValue);
        DBObject result = collection.findOne(query, exclude);
        return JSON.serialize(result);
    }

    public String findOne(Selector query) {
        DBObject object = (DBObject) JSON.parse(query.toJSONString());
        DBObject result = collection.findOne(object, exclude);
        return JSON.serialize(result);
    }

    public int findCount(Pattern fieldValue) {
        BasicDBObject query = new BasicDBObject("title", fieldValue);
        long count = collection.count(query);
        return (int) count;
    }

    public int findCountWithLimit(Pattern fieldValue, int limit) {
        BasicDBObject query = new BasicDBObject("title", fieldValue);
        long count = (int) collection.getCount(query, null, limit, 0);
        return (int) count;
    }

    public List<String> findMany(int n) {

        DBCursor cursor = collection.find(new BasicDBObject(), exclude).sort(sortField);
        List<String> jsonResults = new ArrayList<String>();
        if (n > 0) {
            cursor = cursor.limit(n);
        }
        try {
            while (cursor.hasNext()) {
                DBObject current = cursor.next();
                jsonResults.add(JSON.serialize(current));
            }
        } finally {
            cursor.close();
        }
        return jsonResults;
    }
    
    

    public List<String> findManyWithOrDeprecated(String field, List<String> values, int n) {
        String prefix = "{$or:[";

        String jsonString = "";
        int count = 1;
        for (String value : values) {


            jsonString = jsonString + "{\"" + field + "\":\"" + value + "\"}";
            if (count != values.size()) {
                jsonString = jsonString + ",";
            }
            count++;
        }
        String suffix = "]}";

        jsonString = prefix + jsonString + suffix;
        DBObject object = (DBObject) JSON.parse(jsonString);
        DBCursor cursor = collection.find(object, exclude).sort(sortField);

        if (n > 0) {
            cursor = cursor.limit(n);
        }

        List<String> jsonResults = new ArrayList<String>();
        try {
            while (cursor.hasNext()) {
                DBObject current = cursor.next();
                jsonResults.add(JSON.serialize(current));
            }
        } finally {
            cursor.close();
        }
        return jsonResults;
    }

    public List<String> findManyWithOr(String field, List<String> values, int n) {
        String prefix = "{" + field + ": {$in:[";

        String jsonString = "";
        int count = 1;
        for (String value : values) {

            jsonString = jsonString + "\"" + value + "\"";
            if (count != values.size()) {
                jsonString = jsonString + ",";
            }
            count++;
        }
        String suffix = "]}}";

        jsonString = prefix + jsonString + suffix;

        System.out.println(jsonString);
        DBObject object = (DBObject) JSON.parse(jsonString);
        DBCursor cursor = collection.find(object, exclude).sort(sortField);

        if (n > 0) {
            cursor = cursor.limit(n);
        }

        List<String> jsonResults = new ArrayList<String>();
        try {
            while (cursor.hasNext()) {
                DBObject current = cursor.next();
                jsonResults.add(JSON.serialize(current));
            }
        } finally {
            cursor.close();
        }
        return jsonResults;
    }

    public List<String> findManyWithOr(String fieldName, String fieldValue, String orField, List<String> values, int n) {
        String prefix = "{\"" + fieldName + "\":\"" + fieldValue + "\"" + ",\"" + orField + "\": {$in:[";

        String jsonString = "";
        int count = 1;
        for (String value : values) {

            jsonString = jsonString + "\"" + value + "\"";
            if (count != values.size()) {
                jsonString = jsonString + ",";
            }
            count++;
        }
        String suffix = "]}}";

        jsonString = prefix + jsonString + suffix;

        System.out.println(jsonString);
        DBObject object = (DBObject) JSON.parse(jsonString);
        DBCursor cursor = collection.find(object, exclude).sort(sortField);

        if (n > 0) {
            cursor = cursor.limit(n);
        }

        List<String> jsonResults = new ArrayList<String>();
        try {
            while (cursor.hasNext()) {
                DBObject current = cursor.next();
                jsonResults.add(JSON.serialize(current));
            }
        } finally {
            cursor.close();
        }
        return jsonResults;
    }

    public List<String> findMany(DBObject query, int n) {

        DBCursor cursor = collection.find(query, exclude).sort(sortField);
        List<String> jsonResults = new ArrayList<String>();
        if (n > 0) {
            cursor = cursor.limit(n);
        }
        try {
            while (cursor.hasNext()) {
                DBObject current = cursor.next();
                jsonResults.add(JSON.serialize(current));
            }
        } finally {
            cursor.close();
        }
        return jsonResults;
    }

    public List<String> findMany(Selector query, int n) {
        
        DBObject object = (DBObject) JSON.parse(query.toJSONString());
        DBCursor cursor = collection.find(object, exclude).sort(sortField);

        if (n > 0) {
            cursor = cursor.limit(n);
        }

        List<String> jsonResults = new ArrayList<String>();
        try {
            while (cursor.hasNext()) {
                DBObject current = cursor.next();
                jsonResults.add(JSON.serialize(current));
            }
        } finally {
            cursor.close();
        }
        return jsonResults;
    }
    
       public List<String> findManyNoSorting(Selector query, int n) {
        
        DBObject object = (DBObject) JSON.parse(query.toJSONString());
        DBCursor cursor = collection.find(object, exclude);

        if (n > 0) {
            cursor = cursor.limit(n);
        }

        List<String> jsonResults = new ArrayList<String>();
        try {
            while (cursor.hasNext()) {
                DBObject current = cursor.next();
                jsonResults.add(JSON.serialize(current));
            }
        } finally {
            cursor.close();
        }
        return jsonResults;
    }

    public List<String> findManySortedByPublicationTime(Selector query, int n) {
        DBObject object = (DBObject) JSON.parse(query.toJSONString());
        DBCursor cursor = collection.find(object, exclude).sort(publicationTimeField);

        if (n > 0) {
            cursor = cursor.limit(n);
        }

        List<String> jsonResults = new ArrayList<String>();
        try {
            while (cursor.hasNext()) {
                DBObject current = cursor.next();
                jsonResults.add(JSON.serialize(current));
            }
        } finally {
            cursor.close();
        }
        return jsonResults;
    }

    public List<String> findMany(String fieldName, Object fieldValue, int n) {

        DBObject query = new BasicDBObject(fieldName, fieldValue);
        DBCursor cursor = collection.find(query, exclude).sort(sortField);
        if (n > 0) {
            cursor = cursor.limit(n);
        }

        List<String> jsonResults = new ArrayList<String>();
        try {
            while (cursor.hasNext()) {
                DBObject current = cursor.next();
                jsonResults.add(JSON.serialize(current));
            }
        } finally {
            cursor.close();
        }
        return jsonResults;
    }

    public void clean() {
        collection.drop();
    }

    public void close() {
        Mongo mongo = db.getMongo();
        if (mongo != null) {
            mongo.close();
        }
    }

    public boolean delete(Map<?, ?> m) {
        DBObject ro = new BasicDBObject(m);
        WriteResult result = collection.remove(ro);
        if (result.getN() > 0) {
            return true;
        }
        return false;
    }

    public boolean delete(String fieldName, String fieldValue) {
        DBObject ro = new BasicDBObject(fieldName, fieldValue);
        WriteResult result = collection.remove(ro);
        if (result.getN() > 0) {
            return true;
        }
        return false;
    }

    public boolean delete(String fieldName, String fieldValue, String collName) {
        DBCollection coll = db.getCollection(collName);
        DBObject ro = new BasicDBObject(fieldName, fieldValue);
        WriteResult result = coll.remove(ro);
        if (result.getN() > 0) {
            return true;
        }
        return false;
    }
    
    public boolean delete(){
    	db.dropDatabase();
    	
    	return true;
    }

    public void update(String fieldName, String fieldValue, JSONable jsonObject) {
        BasicDBObject q = new BasicDBObject(fieldName, fieldValue);
        DBObject update = (DBObject) JSON.parse(jsonObject.toJSONString());
        collection.update(q, update, false, false);
    }

    public void update(String fieldName, String fieldValue, Map<String, Object> map) {
        BasicDBObject q = new BasicDBObject(fieldName, fieldValue);
        DBObject update = new BasicDBObject(map);
        collection.update(q, update, false, false);
    }

    public void update(String fieldName, String fieldValue, DBObject changes) {
        BasicDBObject q = new BasicDBObject(fieldName, fieldValue);
        collection.update(q, changes);
    }

    public void updateOld(String fieldName, String fieldValue, JSONable jsonObject) {
        BasicDBObject q = new BasicDBObject(fieldName, fieldValue);
        DBObject update = (DBObject) JSON.parse(jsonObject.toJSONString());
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.append("$set", update);
        collection.update(q, newDocument, false, true);
    }

    public static void main(String[] args) throws UnknownHostException {


    }
}

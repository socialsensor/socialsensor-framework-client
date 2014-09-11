package eu.socialsensor.framework.client.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import eu.socialsensor.framework.common.domain.JSONable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author etzoannos
 *
 */
public class MongoHandler {

    DBCollection collection;
    private DBObject sortField = new BasicDBObject("_id", -1);
    private DBObject publicationTimeField = new BasicDBObject("publicationTime", -1);
    public static int ASC = 1;
    public static int DESC = -1;
    private DB db = null;
    private static Map<String, MongoClient> connections = new HashMap<String, MongoClient>();
    private static Map<String, DB> databases = new HashMap<String, DB>();

    private static MongoClientOptions options = MongoClientOptions.builder()
            .writeConcern(WriteConcern.UNACKNOWLEDGED).build();

    public MongoHandler(String host, String dbName, String collectionName, List<String> indexes) throws Exception {
        this(host, dbName);
        collection = db.getCollection(collectionName);

        if (indexes != null) {
            for (String index : indexes) {
                collection.ensureIndex(index);
            }
        }
    }

    public MongoHandler(String hostname, String dbName) throws Exception {
        String connectionKey = hostname + "#" + dbName;
        db = databases.get(connectionKey);
        if (db == null) {
            MongoClient mongo = connections.get(hostname);

            if (mongo == null) {
                mongo = new MongoClient(hostname, options);
                connections.put(hostname, mongo);
            }
            db = mongo.getDB(dbName);
            databases.put(connectionKey, db);

            /*
             try{
             mongo.getConnector().getDBPortPool(mongo.getAddress()).get().ensureOpen();
            
             }
             catch(Exception e){
             System.out.println("Mongo DB at " + hostname +" is closed");
             throw e;
             }
             */
        }
    }

    /**
     * A MongoHandler for a database with enabled authentication
     *
     * @param hostname
     * @param dbName
     * @param collectionName
     * @param indexes
     * @param username
     * @param password
     * @throws Exception
     */
    public MongoHandler(String hostname, String dbName, String collectionName, List<String> indexes, String username, char[] password) throws Exception {
        String connectionKey = hostname + "#" + dbName;
        db = databases.get(connectionKey);

        if (db == null) {
            MongoClient mongo = connections.get(hostname);

            if (mongo == null) {
                mongo = new MongoClient(hostname, options);
                connections.put(hostname, mongo);
            }
            db = mongo.getDB(dbName);
            if (db.authenticate(username, password)) {
                databases.put(connectionKey, db);
            } else {
                throw new RuntimeException("Could not login to " + dbName + " database with user " + username);
            }
        }
        collection = db.getCollection(collectionName);

        if (indexes != null) {
            for (String index : indexes) {
                collection.ensureIndex(index);
            }
        }
    }

    public boolean checkConnection(String hostname) {

        MongoClient mongo = connections.get(hostname);

        if (mongo == null) {
            return false;
        }

//    	 try{
//         	mongo.getConnector().getDBPortPool(mongo.getAddress()).get().ensureOpen();
//         
//         }
//         catch(Exception e){
//         	System.out.println("Mongo DB at "+hostname+" is closed");
//         	return false;
//         }
//    	 
        return true;
    }

    public void sortBy(String field, int order) throws MongoException {
        this.sortField = new BasicDBObject(field, order);
    }

    public void insert(JSONable jsonObject, String collName) throws MongoException {
        String json = jsonObject.toJSONString();
        DBObject object = (DBObject) JSON.parse(json);
        DBCollection coll = db.getCollection(collName);
        coll.insert(object);
    }

    public void insert(JSONable jsonObject) throws MongoException {
        String json = jsonObject.toJSONString();
        DBObject object = (DBObject) JSON.parse(json);
        collection.insert(object);
    }

    public void insertJson(String json) {

        DBObject object = (DBObject) JSON.parse(json);
        collection.insert(object);
    }

    public void insert(Map<String, Object> map) throws MongoException {
        collection.insert(new BasicDBObject(map));
    }

    public boolean exists(String fieldName, String fieldValue) throws MongoException {
        BasicDBObject query = new BasicDBObject(fieldName, fieldValue);
        DBObject result = collection.findOne(query, new BasicDBObject("_id", 1));
        if (result == null) {
            return false;
        }
        return true;
    }

    public String findOne() {

        DBObject result = collection.findOne(new BasicDBObject());

        return JSON.serialize(result);
    }

    public String findOne(String fieldName, String fieldValue) throws MongoException {
        BasicDBObject query = new BasicDBObject(fieldName, fieldValue);
        DBObject result = collection.findOne(query);
        return JSON.serialize(result);
    }

    public Object findOneField(String fieldName, String fieldValue, String retField) throws MongoException {
        BasicDBObject query = new BasicDBObject(fieldName, fieldValue);
        BasicDBObject field = new BasicDBObject(retField, 1);

        DBObject result = collection.findOne(query, field);
        if (result == null) {
            return null;
        }

        return result.get(retField);
    }

    public String findOne(Selector query) throws MongoException {
        DBObject object = (DBObject) JSON.parse(query.toJSONString());
        DBObject result = collection.findOne(object);
        return JSON.serialize(result);
    }

    public int findCount(Pattern fieldValue) throws MongoException {
        BasicDBObject query = new BasicDBObject("title", fieldValue);
        long count = collection.count(query);
        return (int) count;
    }

    public int findCountWithLimit(Pattern fieldValue, int limit) throws MongoException {
        BasicDBObject query = new BasicDBObject("title", fieldValue);
        long count = (int) collection.getCount(query, null, limit, 0);
        return (int) count;
    }

    public List<String> findMany(int n) throws MongoException {

        DBCursor cursor = collection.find(new BasicDBObject()).sort(sortField);
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

    public List<String> findManyWithOrDeprecated(String field, List<String> values, int n) throws MongoException {
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
        DBCursor cursor = collection.find(object).sort(sortField);

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

    public List<String> findManyWithOr(String field, List<String> values, int n) throws MongoException {
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
        DBCursor cursor = collection.find(object).sort(sortField);

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

    public List<String> findManyWithOr(String fieldName, String fieldValue, String orField, List<String> values, int n) throws MongoException {
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
        DBCursor cursor = collection.find(object).sort(sortField);

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

    public List<String> findMany(DBObject query, int n) throws MongoException {

        DBCursor cursor = collection.find(query).sort(sortField);
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
    

    public List<String> findMany(Selector query, int n) throws MongoException {

        DBObject object = (DBObject) JSON.parse(query.toJSONString());
        DBCursor cursor = collection.find(object).sort(sortField);

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

    public List<String> findManyNoSorting(Selector query, int n) throws MongoException {

        DBObject object = (DBObject) JSON.parse(query.toJSONString());
        DBCursor cursor = collection.find(object);

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

    public List<String> findManySortedByPublicationTime(Selector query, int n) throws MongoException {
        DBObject object = (DBObject) JSON.parse(query.toJSONString());
        DBCursor cursor = collection.find(object).sort(publicationTimeField);

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
    

    public List<String> findMany(String fieldName, Object fieldValue, int n) throws MongoException {

        DBObject query = new BasicDBObject(fieldName, fieldValue);
        
        DBCursor cursor = collection.find(query).sort(sortField);
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
    
    

    public void clean() throws MongoException {
        collection.drop();
    }

    public void close() throws MongoException {
        Mongo mongo = db.getMongo();
        if (mongo != null) {
            mongo.close();
        }
    }

    public boolean delete(Map<?, ?> m) throws MongoException {
        DBObject ro = new BasicDBObject(m);
        WriteResult result = collection.remove(ro);
        if (result.getN() > 0) {
            return true;
        }
        return false;
    }

    public boolean delete(String fieldName, String fieldValue) throws MongoException {
        DBObject ro = new BasicDBObject(fieldName, fieldValue);
        WriteResult result = collection.remove(ro);
        if (result.getN() > 0) {
            return true;
        }
        return false;
    }

    public boolean delete(String fieldName, String fieldValue, String collName) throws MongoException {
        DBCollection coll = db.getCollection(collName);
        DBObject ro = new BasicDBObject(fieldName, fieldValue);
        WriteResult result = coll.remove(ro);
        if (result.getN() > 0) {
            return true;
        }
        return false;
    }

    public boolean delete() {
        db.dropDatabase();

        return true;
    }

    public void update(String fieldName, String fieldValue, JSONable jsonObject) throws MongoException {
        BasicDBObject q = new BasicDBObject(fieldName, fieldValue);
        DBObject update = (DBObject) JSON.parse(jsonObject.toJSONString());
        collection.update(q, update, false, false);
    }

    public void update(String fieldName, String fieldValue, Map<String, Object> map) throws MongoException {
        BasicDBObject q = new BasicDBObject(fieldName, fieldValue);
        DBObject update = new BasicDBObject(map);
        collection.update(q, update, false, false);
    }

    public void update(String fieldName, String fieldValue, DBObject changes) throws MongoException {
        BasicDBObject q = new BasicDBObject(fieldName, fieldValue);
        collection.update(q, changes);
    }

    public void updateOld(String fieldName, String fieldValue, JSONable jsonObject) throws MongoException {
        BasicDBObject q = new BasicDBObject(fieldName, fieldValue);
        DBObject update = (DBObject) JSON.parse(jsonObject.toJSONString());
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.append("$set", update);
        collection.update(q, newDocument, false, true);
    }

    public MongoIterator getIterator(DBObject query) {
        DBCursor cursor = collection.find(query);

        MongoIterator iterator = new MongoIterator(cursor);
        return iterator;
    }

    public MongoIterator getIterator(Selector query) {
        DBObject object = (DBObject) JSON.parse(query.toJSONString());
        DBCursor cursor = collection.find(object);

        MongoIterator iterator = new MongoIterator(cursor);
        return iterator;
    }

    public class MongoIterator implements Iterator<String> {

        private DBCursor cursor;

        private MongoIterator(DBCursor cursor) {
            this.cursor = cursor;
        }

        public String next() {
            DBObject next = cursor.next();
            return next.toString();
        }

        public boolean hasNext() {
            return cursor.hasNext();
        }

        @Override
        public void remove() {
            cursor.next();
        }
    }

    public static void main(String[] args) throws Exception {

        MongoHandler handler = new MongoHandler("xxx.xxx.xxx", "Streams", "Items", null);
        MongoIterator it = handler.getIterator(new BasicDBObject());

        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

}

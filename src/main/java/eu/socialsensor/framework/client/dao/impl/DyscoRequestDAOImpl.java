package eu.socialsensor.framework.client.dao.impl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.socialsensor.framework.client.dao.DyscoRequestDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.client.mongo.Selector;
import eu.socialsensor.framework.common.domain.DyscoRequest;
import eu.socialsensor.framework.common.factories.ItemFactory;

public class DyscoRequestDAOImpl implements DyscoRequestDAO {

    List<String> indexes = new ArrayList<String>();
    private MongoHandler mongoHandler;
    private static String host = "";
    private static String db = "Streams";
    private static String collection = "Dyscos";

    public DyscoRequestDAOImpl() {
        this(host, db, collection);
    }

    public DyscoRequestDAOImpl(String host, String db, String collection) {
        indexes.add("id");
        try {
            mongoHandler = new MongoHandler(host, db, collection, indexes);
        } catch (UnknownHostException ex) {
            Logger.getRootLogger().error(ex.getMessage());
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void insertDyscoRequest(DyscoRequest request) {
        String id = request.getId();
        mongoHandler.insert(request);
    }

    @Override
    public boolean deleteDyscoRequest(DyscoRequest request) {
        return mongoHandler.delete("id", request.getId());
    }

    @Override
    public DyscoRequest getDyscoRequest(String id) {
        String json = mongoHandler.findOne("id", id);
        DyscoRequest request = ItemFactory.createDyscoRequest(json);
        return request;
    }

    @Override
    public boolean exists(String id) {
        return mongoHandler.exists("id", id);
    }

    @Override
    public void updateRequest(Object object) {
        DyscoRequest request = (DyscoRequest) object;
        mongoHandler.update("id", request.getId(), request);
    }

    @Override
    public void readRequests(List<Object> requests) {
        List<String> jsonItems = mongoHandler.findMany(-1);

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        for (String json : jsonItems) {

            DyscoRequest request = gson.fromJson(json, DyscoRequest.class);
            requests.add(request);
        }
    }

    @Override
    public List<DyscoRequest> readRequestsByStatus() {
        Selector query = new Selector();
        query.select("status", Boolean.FALSE);
        List<String> jsonItems = mongoHandler.findMany(query, -1);
        List<DyscoRequest> requests = new ArrayList<DyscoRequest>();
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        for (String json : jsonItems) {
            DyscoRequest request = gson.fromJson(json, DyscoRequest.class);
            requests.add(request);
        }

        return requests;
    }
    
    @Override
    public List<DyscoRequest> readUnsearchedRequestsByType(String type){
    	List<DyscoRequest> requests = new ArrayList<DyscoRequest>();
    	
    	//set query
    	Selector query = new Selector();
    	query.select("searched", Boolean.FALSE);
    	query.select("dyscoType", type);
    	
    	List<String> jsonItems = mongoHandler.findMany(query, -1);
    	Gson gson = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create();

		for (String json : jsonItems) {
		    DyscoRequest request = gson.fromJson(json, DyscoRequest.class);
		    requests.add(request);
		}
		
    	return requests;
    }
    
    @Override
    public List<String> readKeywordsFromDyscos(List<String> dyscoIds) {
        List<String> keywords = new ArrayList<String>();

        for (String dyscoId : dyscoIds) {
            String json = mongoHandler.findOne("id", dyscoId);
            DyscoRequest request = ItemFactory.createDyscoRequest(json);
            if (request != null) {
                if (request.getKeywords() != null) {
                    for (String key : request.getKeywords()) {
                        if (!keywords.contains(key)) {
                            keywords.add(key);
                        }
                    }
                }
            } else {
                Logger.getRootLogger().error("Dysco not indexed by MediaSearcher");
            }
        }

        return keywords;
    }
}

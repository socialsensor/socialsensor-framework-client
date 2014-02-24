package eu.socialsensor.framework.client.search.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.util.NamedList;

import eu.socialsensor.framework.client.search.Query;
import eu.socialsensor.framework.client.search.SearchEngineResponse;

/**
 *
 * @author cmartin - c.j.martin-dancausa@rgu.ac.uk
 */
public class SolrTopicDetectionItemHandler {

    private Logger logger = Logger.getLogger(SolrTopicDetectionItemHandler.class);

    private SolrServer server;
    private static SolrTopicDetectionItemHandler INSTANCE = null;

    private SolrTopicDetectionItemHandler(String collection) {
        try { 
            server = new HttpSolrServer(collection);
        } catch (Exception e) {
            Logger.getRootLogger().info(e.getMessage());
        }
    }

    //implementing Singleton pattern
    public static SolrTopicDetectionItemHandler getInstance(String collection) {
        if (INSTANCE == null) {
            INSTANCE = new SolrTopicDetectionItemHandler(collection);
        }
        return INSTANCE;
    }

    @SuppressWarnings("finally")
	public boolean deleteItem(String itemId) {
        boolean status = false;
        try {
            server.deleteByQuery("id:" + itemId);
            UpdateResponse response = server.commit();
            int statusId = response.getStatus();
            if (statusId == 0) {
                status = true;
            }

        } catch (SolrServerException ex) {
            logger.error(ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            return status;
        }
    }

    @SuppressWarnings("finally")
	public boolean deleteItems(Query query) {
        boolean status = false;
        try {
            server.deleteByQuery(query.getQueryString());
            UpdateResponse response = server.commit();
            int statusId = response.getStatus();
            if (statusId == 0) {
                status = true;
            }

        } catch (SolrServerException ex) {
            logger.error(ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            return status;
        }
    }

//    public SearchEngineResponse<Item> findItems(SolrQuery query) {
//        return search(query);
//    }

//    public List<Item> findItemsByTimeslotId(String timeslotId) {
//
//        SolrQuery solrQuery = new SolrQuery("timeslotId:\"" + timeslotId + "\"");
//        solrQuery.setRows(100000000);
//        SearchEngineResponse<Item> response = search(solrQuery);
//        List<Item> items = response.getResults();
//        if (!items.isEmpty()) {
//            return items;
//        } else {
//            Logger.getRootLogger().info("no tweet for this timeslotId found!!");
//            return null;
//        }
//    }

    public Map<String, SolrTopicDetectionItem> findItems(long lowerBound, long upperBound) {
    	Map<String, SolrTopicDetectionItem> itemsMap=new HashMap<String, SolrTopicDetectionItem>();
    	SolrQuery solrQuery = new SolrQuery("publicationTime: ["+lowerBound+" TO "+upperBound+"}");
        solrQuery.setRows(10000000);
        SearchEngineResponse<SolrTopicDetectionItem> response = search(solrQuery);
        List<SolrTopicDetectionItem> items = response.getResults();
        if (!items.isEmpty()) {
        	for (SolrTopicDetectionItem item:items)
        		itemsMap.put(item.getId(), item);
        	return itemsMap;
        } else {
            Logger.getRootLogger().info("no tweets found!!");
            return null;
        }
    }
    
    public SolrTopicDetectionItem findItem(String itemId) {

        SolrQuery solrQuery = new SolrQuery("id:\"" + itemId + "\"");
        solrQuery.setRows(1);
        SearchEngineResponse<SolrTopicDetectionItem> response = search(solrQuery);
        List<SolrTopicDetectionItem> items = response.getResults();
        if (!items.isEmpty()) {
            return items.get(0);
        } else {
            Logger.getRootLogger().info("no tweet for this id found!!");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String,Integer> getItemTermsId(String id) {
        Map<String,Integer> terms=new HashMap<String,Integer>();
    	SolrQuery query = new SolrQuery("id:"+id);
        query.setRequestHandler("/tvrh");
        query.setParam("tv.fl", "title");
        query.setParam("f.title.tv.tf", true);
        query.setRows(2000000);
        logger.info(query.toString());
        
        QueryResponse rsp;
        try {
            rsp = server.query(query);
        } catch (SolrServerException e) {
            logger.warn("Problem with the query in getTermsItem method (SolrTopicDetectionItemHandler class) from socialsensor-framework-client module");
            e.printStackTrace();
            return null;
        }
        logger.info("Elapsed time: " + rsp.getElapsedTime());
        NamedList<NamedList<Object>> termsVectors;
        NamedList<Object> itemTermsVectors;
        if ((termsVectors = (NamedList<NamedList<Object>>) rsp.getResponse().get("termVectors")) == null) {
            logger.warn("No term vectors found...");
            return terms;
        }
        
        for (int i = 0; i < termsVectors.size(); i++) {
        	try {
        		itemTermsVectors = (NamedList<Object>) termsVectors.getVal(i);
        	} catch (Exception e) {
        		continue;
        	}
        
        	NamedList<NamedList<Object>> itemTerms;
        	Iterator<Entry<String, NamedList<Object>>> itemTermsIterator;
        
        	if ((itemTerms = (NamedList<NamedList<Object>>) itemTermsVectors.get("title")) == null) {
        		return terms;
        	}
        	itemTermsIterator = itemTerms.iterator();
        	while (itemTermsIterator.hasNext()) {
        		Entry<String,NamedList<Object>> ngram=itemTermsIterator.next();
        		terms.put(ngram.getKey(), new Integer(ngram.getValue().get("tf").toString()));
        		//System.out.println(ngram.getKey()+" -- tf-idf: "+ngram.getValue().get("tf-idf").toString());
        	}
        }
        
        return terms;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getItemTermsTimeslot(String typeTerm) {
        logger.info("Get "+typeTerm+" from current timeslot");
        Map<String, List<String>> itemsTerms = new HashMap<String, List<String>>();

        SolrQuery query = new SolrQuery("*:*");
        query.setRequestHandler("/tvrh");
        query.setParam("tv.fl", typeTerm);
        query.setParam("f."+typeTerm+".tv.tf", true);
        query.setRows(2000000);
        logger.info(query.toString());
        
        QueryResponse rsp;
        try {
            rsp = server.query(query);
        } catch (SolrServerException e) {
            logger.warn("Problem with the query in getTermsItem method (SolrTopicDetectionItemHandler class) from socialsensor-framework-client module");
            e.printStackTrace();
            return null;
        }
        logger.info("Elapsed time: " + rsp.getElapsedTime());
        NamedList<NamedList<Object>> itemsTermsVectors;
                
        if ((itemsTermsVectors = (NamedList<NamedList<Object>>) rsp.getResponse().get("termVectors")) == null) {
            logger.warn("No term vectors found...");
            return itemsTerms;
        }
        NamedList<Object> itemTermsVectors;
        NamedList<NamedList<Object>> itemTerms;
        Iterator<Entry<String, NamedList<Object>>> itemTermsIterator;
        for (int i = 0; i < itemsTermsVectors.size(); i++) {
            try {
            	itemTermsVectors = (NamedList<Object>) itemsTermsVectors.getVal(i);
            } catch (Exception e) {
            	continue;
            }

            if ((itemTerms = (NamedList<NamedList<Object>>) itemTermsVectors.get(typeTerm)) == null) {
                itemsTerms.put(itemsTermsVectors.getName(i), new ArrayList<String>());
                continue;
            }
            itemTermsIterator = itemTerms.iterator();
            List<String> terms = new ArrayList<String>();
            while (itemTermsIterator.hasNext()) {
                terms.add(itemTermsIterator.next().getKey());
                //System.out.println(ngram.getKey()+" -- tf-idf: "+ngram.getValue().get("tf-idf").toString());
            }
            itemsTerms.put(itemsTermsVectors.getName(i), terms);
        }
        return itemsTerms;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getItemTermsTimeslot(String typeTerm, long lowerBound, long upperBound) {
        logger.info("Get "+typeTerm+" from current timeslot");
        Map<String, List<String>> itemsTerms = new HashMap<String, List<String>>();

        SolrQuery query = new SolrQuery("publicationTime: ["+lowerBound+" TO "+upperBound+"}");
        query.setRequestHandler("/tvrh");
        query.setParam("tv.fl", typeTerm);
        query.setParam("f."+typeTerm+".tv.tf", true);
        query.setRows(2000000);
        logger.info(query.toString());
        
        QueryResponse rsp;
        try {
            rsp = server.query(query);
        } catch (SolrServerException e) {
            logger.warn("Problem with the query in getTermsItem method (SolrTopicDetectionItemHandler class) from socialsensor-framework-client module");
            e.printStackTrace();
            return null;
        }
        logger.info("Elapsed time: " + rsp.getElapsedTime());
        NamedList<NamedList<Object>> itemsTermsVectors;
                
        if ((itemsTermsVectors = (NamedList<NamedList<Object>>) rsp.getResponse().get("termVectors")) == null) {
            logger.warn("No term vectors found...");
            return itemsTerms;
        }
        NamedList<Object> itemTermsVectors;
        NamedList<NamedList<Object>> itemTerms;
        Iterator<Entry<String, NamedList<Object>>> itemTermsIterator;
        for (int i = 0; i < itemsTermsVectors.size(); i++) {
            try {
            	itemTermsVectors = (NamedList<Object>) itemsTermsVectors.getVal(i);
            } catch (Exception e) {
            	continue;
            }

            if ((itemTerms = (NamedList<NamedList<Object>>) itemTermsVectors.get(typeTerm)) == null) {
                itemsTerms.put(itemsTermsVectors.getName(i), new ArrayList<String>());
                continue;
            }
            itemTermsIterator = itemTerms.iterator();
            List<String> terms = new ArrayList<String>();
            while (itemTermsIterator.hasNext()) {
                terms.add(itemTermsIterator.next().getKey());
                //System.out.println(ngram.getKey()+" -- tf-idf: "+ngram.getValue().get("tf-idf").toString());
            }
            itemsTerms.put(itemsTermsVectors.getName(i), terms);
        }
        return itemsTerms;
    }
    
//    @SuppressWarnings("unchecked")
//    public Map<String, Integer> getAllDyscoKeywords(String dyscoId) {
//        logger.info("Getting terms from dysco: " + dyscoId);
//
//        int cont;
//
//        // Getting term vectors from Solr
//        Map<String, Integer> terms = new HashMap<String, Integer>();
//
//        SolrQuery query = new SolrQuery("dyscoId:\"" + dyscoId + "\"");
//        query.setRequestHandler("/tvrh");
//        query.setParam("tv.fl", "text");
//        query.setParam("f.text.tv.tf", true);
//        query.setRows(100000);
//
//        QueryResponse rsp;
//        try {
//            rsp = server.query(query);
//        } catch (SolrServerException e) {
//            e.printStackTrace();
//            return null;
//        }
//        logger.info("Elapsed time: " + rsp.getElapsedTime());
//        NamedList<NamedList<Object>> termVectors;
//        if ((termVectors = (NamedList<NamedList<Object>>) rsp.getResponse().get("termVectors")) == null) {
//            logger.warn("No term vectors found...");
//            return terms;
//        }
//        NamedList<Object> termVectorsItemId;
//        NamedList<NamedList<Object>> ngrams;
//        Iterator<Entry<String, NamedList<Object>>> ngramsIterator;
//        Entry<String, NamedList<Object>> ngram;
//
//        for (int i = 0; i < termVectors.size(); i++) {
//            if (!termVectors.getName(i).contains("Twitter")) {
//                continue;
//            }
//
//            termVectorsItemId = termVectors.getVal(i);
//
//            if ((ngrams = (NamedList<NamedList<Object>>) termVectorsItemId.get("text")) == null) {
//                logger.warn("No text property found for item: " + termVectors.getName(i));
//                return terms;
//            }
//            ngramsIterator = ngrams.iterator();
//            while (ngramsIterator.hasNext()) {
//                ngram = ngramsIterator.next();
//                cont = Integer.parseInt(ngram.getValue().get("tf").toString());
//                if (terms.containsKey(ngram.getKey())) {
//                    cont += terms.get(ngram.getKey());
//                }
//
//                terms.put(ngram.getKey(), cont);
//            }
//        }
//        return terms;
//    }

    private SearchEngineResponse<SolrTopicDetectionItem> search(SolrQuery query) {
    	SearchEngineResponse<SolrTopicDetectionItem> response = new SearchEngineResponse<SolrTopicDetectionItem>();
////        query.setRows(450);
////        query.setFacet(true);
////        query.addFacetField("sentiment");
////        query.setFacetLimit(4);
//
////        query.set(FacetParams.FACET_DATE, "creationDate");
////        query.set(FacetParams.FACET_DATE_START, "NOW/DAY-5YEARS");
////        query.set(FacetParams.FACET_DATE_END, "NOW/DAY");
////        query.set(FacetParams.FACET_DATE_GAP, "+1YEAR");
//
        QueryResponse rsp;

        System.out.println("query:  " + query.toString());
        try {
            rsp = server.query(query);
        } catch (SolrServerException e) {
            logger.info(e.getMessage());
            return null;
        }

        response.setNumFound(rsp.getResults().getNumFound());

        List<SolrTopicDetectionItem> solrTopicDetectionItems = rsp.getBeans(SolrTopicDetectionItem.class);
        if (solrTopicDetectionItems != null) {
            Logger.getRootLogger().info("got: " + solrTopicDetectionItems.size() + " topicDetectionItems from Solr");
        }

        response.setResults(solrTopicDetectionItems);

        return response;
}

//    public void updateItemFields(String id, String dyscoId, Date creationDate) throws SolrServerException, IOException {
//
//        SolrInputDocument doc = new SolrInputDocument();
//        doc.addField("id", id);
//        HashMap<String, Object> dyscoIdMap = new HashMap<String, Object>();  //need better api for this???
//        dyscoIdMap.put("set", dyscoId);
//        doc.addField("dyscoId", dyscoIdMap);
//        HashMap<String, Object> dateMap = new HashMap<String, Object>();  //need better api for this???
//        dateMap.put("set", creationDate);
//        doc.addField("creationDate", dateMap);
//        server.add(doc);
//        server.commit();
//    }

//    public void updateItems(List<Item> items, Date creationDate, String dyscoId) throws SolrServerException, IOException {
//
//        List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
//        for (Item item : items) {
//
//            SolrInputDocument doc = new SolrInputDocument();
//            doc.addField("id", item.getId());
//            HashMap<String, Object> dyscoIdMap = new HashMap<String, Object>();  //need better api for this???
//            dyscoIdMap.put("set", dyscoId);
//            doc.addField("dyscoId", dyscoIdMap);
//            HashMap<String, Object> dateMap = new HashMap<String, Object>();  //need better api for this???
//            dateMap.put("set", creationDate);
//            doc.addField("creationDate", dateMap);
//            docs.add(doc);
//        }
//
//        if (docs.size() > 0) {
//            Logger.getRootLogger().info("is going to index: " + docs.size() + " documents to Solr");
//            server.add(docs);
//            server.commit();
//        }
//    }
}

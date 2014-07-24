package eu.socialsensor.framework.client.search.solr;

import eu.socialsensor.framework.client.search.Bucket;
import eu.socialsensor.framework.client.search.Facet;
import eu.socialsensor.framework.client.search.Query;
import eu.socialsensor.framework.client.search.SearchEngineResponse;
import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.MediaItem;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 *
 * @author etzoannos
 */
public class SolrMediaItemHandler {

    SolrServer server;
    private static Map<String, SolrMediaItemHandler> INSTANCES = new HashMap<String, SolrMediaItemHandler>();
    private static int commitPeriod = 10000;

    // Private constructor prevents instantiation from other classes
    private SolrMediaItemHandler(String collection) throws Exception{
       
            server = new HttpSolrServer(collection);
            server.ping();
            //Logger.getRootLogger().info("going to create SolrServer: " + ConfigReader.getSolrHome() + "/DyscoMediaItems");
            //server = new HttpSolrServer( ConfigReader.getSolrHome() + "/DyscoMediaItems");
      
    }
    
    public void checkServerStatus() throws Exception {
    	server.ping();
    }

    // implementing Singleton pattern
    public static SolrMediaItemHandler getInstance(String collection) throws Exception {
        SolrMediaItemHandler INSTANCE = INSTANCES.get(collection);
        if (INSTANCE == null) {
            INSTANCE = new SolrMediaItemHandler(collection);
            
            INSTANCES.put(collection, INSTANCE);
        }
        return INSTANCE;
    }

    @SuppressWarnings("finally")
    public boolean insertMediaItem(MediaItem item) {

        boolean status = true;
        try {

            SolrMediaItem solrItem = new SolrMediaItem(item);

            server.addBean(solrItem, commitPeriod);

            //UpdateResponse response = server.commit();
            //int statusId = response.getStatus();
            //if (statusId == 0) {
            //    status = true;
            //}

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getRootLogger().error(ex.getMessage());
            status = false;
        } finally {
            return status;
        }
    }

    @SuppressWarnings("finally")
    public boolean insertMediaItems(List<MediaItem> mediaItems) {

        boolean status = true;
        try {
            List<SolrMediaItem> solrMediaItems = new ArrayList<SolrMediaItem>();
            for (MediaItem mediaItem : mediaItems) {
                SolrMediaItem solrMediaItem = new SolrMediaItem(mediaItem);
                solrMediaItems.add(solrMediaItem);
            }

            server.addBeans(solrMediaItems, commitPeriod);

        } catch (SolrServerException ex) {
            Logger.getRootLogger().error(ex.getMessage());
            status = false;
        } catch (IOException ex) {
            Logger.getRootLogger().error(ex.getMessage());
            status = false;
        } finally {
            return status;
        }

    }

    public SearchEngineResponse<MediaItem> addFilterAndSearchItems(Query query,
            String fq) {
        SolrQuery solrQuery = new SolrQuery(query.getQueryString());
        solrQuery.addFilterQuery(fq);
        return search(solrQuery);
    }

    public SearchEngineResponse<MediaItem> removeFilterAndSearchItems(
            Query query, String fq) {
        SolrQuery solrQuery = new SolrQuery(query.getQueryString());
        return removeFilterAndSearch(solrQuery, fq);
    }

    @SuppressWarnings("finally")
    public boolean deleteMediaItem(String mediaItemId) {
        mediaItemId.replaceFirst("::", "%%");
        boolean status = false;
        try {
            server.deleteByQuery("id:" + mediaItemId);
            UpdateResponse response = server.commit();
            int statusId = response.getStatus();
            if (statusId == 0) {
                status = true;
            }

        } catch (SolrServerException ex) {
            Logger.getRootLogger().error(ex.getMessage());
        } catch (IOException ex) {
            Logger.getRootLogger().error(ex.getMessage());
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
            Logger.getRootLogger().error(ex.getMessage());
        } catch (IOException ex) {
            Logger.getRootLogger().error(ex.getMessage());
        } finally {
            return status;
        }
    }

    public boolean isIndexed(String id) {

        SolrQuery solrQuery = new SolrQuery("id:" + id);
        QueryResponse rsp;
		try {
			rsp = server.query(solrQuery);
			long nunFound = rsp.getResults().getNumFound();
			if (nunFound > 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
    }
    
    public MediaItem findLatestItem(){
    	SolrQuery solrQuery = new SolrQuery("*:*");
    	solrQuery.addSortField("publicationTime", SolrQuery.ORDER.desc);
    	solrQuery.setRows(1);
    	
    	 SearchEngineResponse<MediaItem> response = search(solrQuery);
    	 
         List<MediaItem> items = response.getResults();
         if (!items.isEmpty()) {
             return items.get(0);
         } else {
             Logger.getRootLogger().info("no solr found!!");
             return null;
         }
    }

    
    public SearchEngineResponse<MediaItem> findItems(SolrQuery query) {
        return search(query);
    }
    
    public Map<MediaItem,Float> findMediaItemsWithScore(String query){
    	Map<MediaItem,Float> mitemsByScore = new HashMap<MediaItem,Float>();
    	
    	SolrQuery solrQuery = new SolrQuery(query);
    	solrQuery.setFields("id","title","description","publicationTime","score");
		solrQuery.addSortField("score", ORDER.desc);
		
        QueryResponse rsp = null;
       
        
        try {
            rsp = server.query(solrQuery);
        } catch (SolrServerException e) {
            e.printStackTrace();
            Logger.getRootLogger().info(e.getMessage());
            
        }
        System.out.println("Found "+rsp.getResults().getNumFound()+" results");
        List<SolrDocument> retrievedItems = rsp.getResults();
        
        for(SolrDocument sDoc : retrievedItems){
        	Collection<String> fieldNames = sDoc.getFieldNames();
        	Float score = (Float) sDoc.getFieldValue("score");
        	String title = (String) sDoc.getFieldValue("title");
        	String description = (String) sDoc.getFieldValue("description");
        	String id = (String) sDoc.getFieldValue("id");
        	Long publicationTime = (Long) sDoc.getFieldValue("publicationTime");
        	
        	System.out.println("Solr Document #"+id);
        	System.out.println("Solr Document Title : "+title);
        	System.out.println("Solr Document Score : "+description);
        	System.out.println("Solr Document Score : "+score);
        	
        	System.out.println();
        	MediaItem mitem = new MediaItem();
        	mitem.setId(id);
        	mitem.setTitle(title);
        	mitem.setDescription(description);
        	mitem.setPublicationTime(publicationTime);
        	
        	mitemsByScore.put(mitem, score);
        }
        
        return mitemsByScore;
    }

    public SearchEngineResponse<MediaItem> findItemsWithSocialSearch(SolrQuery query) {
        //query.setRequestHandler("/socialsearch");
        query.set("qt","/socialsearch");
        return search(query);
    }

    public List<MediaItem> findAllMediaItemsByKeywords(List<String> keywords, String type, int size) {

        List<MediaItem> mediaItems = new ArrayList<MediaItem>(size);
        boolean first = true;

        for (String key : keywords) {
            System.out.println("key : " + key);
        }

        String query = "(";

        if (keywords.size() == 1) {
            if (keywords.get(0).split(" ").length > 1) {
                query += "feedKeywordsString:\"" + keywords.get(0) + "\"";
            } else {
                query += "feedKeywords:" + keywords.get(0);
            }
        } else {
            List<String> wordEntities = new ArrayList<String>();
            List<String> simpleWords = new ArrayList<String>();

            query += "feedKeywordsString:(";

            //split keywords into two categories
            for (int i = 0; i < keywords.size(); i++) {
                if (keywords.get(i).split(" ").length > 1) {
                    wordEntities.add(keywords.get(i));
                } else {
                    simpleWords.add(keywords.get(i));
                }
            }
            //feedKeywordsString matches words that are entities (names,organizations,locations)
            for (int i = 0; i < wordEntities.size(); i++) {
                if (!first) {
                    query += " OR ";
                }
                query += "\"" + wordEntities.get(i) + "\"";

                int j = i + 1;
                //else for all other keywords create combinations ((key_1ANDkey_2) OR (key_1ANDkey_3) OR ..)
                while (j < wordEntities.size()) {

                    query += " OR ";

                    String oneQuery = "(\"" + wordEntities.get(i) + "\" AND \"" + wordEntities.get(j) + "\")";

                    query += oneQuery;

                    int k = j + 1;
                    while (k < wordEntities.size()) {
                        query += " OR ";

                        String secQuery = "(\"" + wordEntities.get(i) + "\" AND \"" + wordEntities.get(j) + "\" AND \"" + wordEntities.get(k) + "\")";

                        query += secQuery;

                        k++;
                    }

                    j++;
                }

                first = false;
            }
            if (first && simpleWords.size() > 0) {
                query = "(feedKeywords:(";
            } else if (simpleWords.size() > 0) {
                first = true;
                query += ") OR feedKeywords:(";
            }
            for (int i = 0; i < simpleWords.size(); i++) {
                int j = i + 1;
                // for all other keywords create combinations ((key_1ANDkey_2) OR (key_1ANDkey_3) OR ..)
                while (j < simpleWords.size()) {
                    if (!first) {
                        query += " OR ";
                    }

                    String oneQuery = "(" + simpleWords.get(i) + " AND " + simpleWords.get(j) + ")";

                    query += oneQuery;

                    int k = j + 1;
                    while (k < simpleWords.size()) {
                        query += " OR ";

                        String secQuery = "(" + simpleWords.get(i) + " AND " + simpleWords.get(j) + " AND " + simpleWords.get(k) + ")";

                        query += secQuery;

                        k++;
                    }

                    j++;

                    first = false;
                }
                j = 0;
                while (j < wordEntities.size()) {
                    if (!first) {
                        query += " OR ";
                    }

                    String oneQuery = "(" + simpleWords.get(i) + " AND " + wordEntities.get(j) + ")";

                    query += oneQuery;

                    int k = j + 1;
                    while (k < wordEntities.size()) {
                        query += " OR ";

                        String secQuery = "(" + simpleWords.get(i) + " AND " + wordEntities.get(j) + " AND " + wordEntities.get(k) + ")";

                        query += secQuery;

                        k++;
                    }
                    k = i + 1;
                    while (k < simpleWords.size()) {
                        query += " OR ";

                        String secQuery = "(" + simpleWords.get(i) + " AND " + wordEntities.get(j) + " AND " + simpleWords.get(k) + ")";

                        query += secQuery;

                        k++;
                    }

                    j++;

                    first = false;
                }

            }
            query += ")";
        }
        /*
         //OLD VERSION
         String query = "feedKeywords:(";
         //If only one keyword query with that
         if(keywords.size() == 1){
         query += keywords.get(0);
         }
         else{
         for(int i=0;i<keywords.size();i++){
         //If keyword is a name (two words) make it a stand-alone term for query
         if(keywords.get(i).split(" ").length >1){
         if (!first) {
         query += " OR ";
         }
	        		
         query += "("+keywords.get(i)+")";
         first = false;
         }
	        	
         int j=i+1;
         //else for all other keywords create combinations ((key_1ANDkey_2) OR (key_1ANDkey_3) OR ..)
         while(j<keywords.size()){
	        		
         if (!first) {
         query += " OR ";
         }
	
         String oneQuery = "("+keywords.get(i)+" AND "+keywords.get(j)+")";
	        		
         query += oneQuery;
	        		
         first = false;
         j++;
         }
	        	
         }
         }*/


        //Set to the query the type of media item we want to be retrieved from solr (image - video)
        query += ") AND type : " + type;

        //escape "/" character in Solr Query
        query = query.replace("/", "\\/");

        SolrQuery solrQuery = new SolrQuery(query);
        Logger.getRootLogger().info("query: " + query);
        solrQuery.setRows(size);
        SearchEngineResponse<MediaItem> response = search(solrQuery);

        if (response != null) {
            List<MediaItem> results = response.getResults();
            Set<String> urls = new HashSet<String>();
            for (MediaItem mi : results) {
                if (!urls.contains(mi.getUrl()) && !mi.getThumbnail().contains("sddefault") && !mi.getUrl().contains("photo_unavailable")) {
                    mediaItems.add(mi);
                    urls.add(mi.getUrl());
                }
            }
        }
        return mediaItems;
    }

    public SearchEngineResponse<MediaItem> findAllDyscoItemsLightByTime(
            String dyscoId) {
        SolrQuery solrQuery = new SolrQuery("dyscoId:" + dyscoId);
        solrQuery.addSortField("publicationTime", SolrQuery.ORDER.asc);
        solrQuery.setRows(200);
        return search(solrQuery);
    }

    public SearchEngineResponse<MediaItem> findAllDyscoItems(String dyscoId) {
        SolrQuery solrQuery = new SolrQuery("dyscoId:" + dyscoId);
        solrQuery.setRows(200);
        return search(solrQuery);
    }

    private SearchEngineResponse<MediaItem> removeFilterAndSearch(
            SolrQuery query, String fq) {

        query.removeFilterQuery(fq);
        return search(query);
    }

    public MediaItem getSolrMediaItem(String id) {

        SolrQuery solrQuery = new SolrQuery("id:" + id);
        SearchEngineResponse<MediaItem> mi = search(solrQuery);

        List<MediaItem> results = mi.getResults();

        if (results == null || results.size() == 0) {
            return null;
        }

        MediaItem mediaItem = results.get(0);
        mediaItem.setId(id);
        return mediaItem;

    }

    private SearchEngineResponse<MediaItem> search(SolrQuery query) {

        SearchEngineResponse<MediaItem> response = new SearchEngineResponse<MediaItem>();
        QueryResponse rsp;
        

        query.setFields("* score");
        try {
            rsp = server.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
            Logger.getRootLogger().info(e.getMessage());
            return null;
        }

        
        response.setNumFound(rsp.getResults().getNumFound());
       
        List<SolrMediaItem> solrItems = new ArrayList<SolrMediaItem>();
        
        SolrDocumentList docs = rsp.getResults();
        for(SolrDocument doc : docs){
        	SolrMediaItem solrMediaItem = new SolrMediaItem(doc);
        	solrItems.add(solrMediaItem);
        }
      
        if (solrItems != null) {
            Logger.getRootLogger().info("got: " + solrItems.size() + " media items from Solr - total results: " + response.getNumFound());
        }
        
        List<MediaItem> mediaItems = new ArrayList<MediaItem>();
        for (SolrMediaItem solrMediaItem : solrItems) {
            try {
                MediaItem mediaItem = solrMediaItem.toMediaItem();
                String id = mediaItem.getId();
               
                mediaItem.setId(id);

                mediaItems.add(mediaItem);
            } catch (MalformedURLException ex) {
                Logger.getRootLogger().error(ex.getMessage());
            }
        }

        response.setResults(mediaItems);
        List<Facet> facets = new ArrayList<Facet>();
        List<FacetField> solrFacetList = rsp.getFacetFields();
        FacetField solrFacet;


        if (solrFacetList != null) {

            //populate all non-zero facets

            for (int i = 0; i < solrFacetList.size(); i++) {

                Facet facet = new Facet(); //initialize for Arcomem JSF UI
                List<Bucket> buckets = new ArrayList<Bucket>();
                solrFacet = solrFacetList.get(i); //get the ones returned from Solr
                List<FacetField.Count> values = solrFacet.getValues();
                String solrFacetName = solrFacet.getName();
                boolean validFacet = false;

                //populate Valid Facets
                for (int j = 0; j < solrFacet.getValueCount(); j++) {

                    Bucket bucket = new Bucket();
                    long bucketCount = values.get(j).getCount();
//                    if ((bucketCount > 0) && (bucketCount != solrItems.size())) { //bucket is neither non-zero length nor the whole set 
                    validFacet = true; //facet contains at least one non-zero length bucket
                    bucket.setCount(bucketCount);
                    bucket.setName(values.get(j).getName());
                    bucket.setQuery(values.get(j).getAsFilterQuery());
                    bucket.setFacet(solrFacetName);
                    buckets.add(bucket);
//                    }
                }
                if (validFacet) { //add the facet only if it is contains at least one non-zero length - excludes the whole set result
                    facet.setBuckets(buckets);
                    facet.setName(solrFacetName);
                    facets.add(facet);
                }
            }

            Collections.sort(facets, new Comparator<Facet>() { //anonymous inner class used for sorting
                @Override
                public int compare(Facet f1, Facet f2) {

                    String value1 = f1.getName();
                    String value2 = f2.getName();

                    if (value1.compareTo(value2) > 0) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        }
        response.setFacets(facets);

        return response;
    }

    public void forceCommitPending() {

        try {

            server.commit();
        } catch (SolrServerException ex) {
            ex.printStackTrace();
            Logger.getRootLogger().error(ex.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getRootLogger().error(ex.getMessage());
        }
    }
    
    
}
package eu.socialsensor.framework.client.search.solr;


import eu.socialsensor.framework.client.search.Query;
import eu.socialsensor.framework.client.search.SearchEngineResponse;
import eu.socialsensor.framework.common.domain.MediaItem;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;


/**
 *
 * @author etzoannos
 */
public class SolrMediaItemHandler {

    
    SolrServer server;
    private static Map<String, SolrMediaItemHandler> INSTANCES = new HashMap<String, SolrMediaItemHandler>();

    // Private constructor prevents instantiation from other classes
    private SolrMediaItemHandler(String collection) {
        try {
            server = new HttpSolrServer(collection);
            //Logger.getRootLogger().info("going to create SolrServer: " + ConfigReader.getSolrHome() + "/DyscoMediaItems");
        	//server = new HttpSolrServer( ConfigReader.getSolrHome() + "/DyscoMediaItems");
        } catch (Exception e) {
            Logger.getRootLogger().info(e.getMessage());
        }
    }

    // implementing Singleton pattern
    public static SolrMediaItemHandler getInstance(String collection) {
    	SolrMediaItemHandler INSTANCE = INSTANCES.get(collection);
        if (INSTANCE == null) {
            INSTANCE = new SolrMediaItemHandler(collection);
            INSTANCES.put(collection, INSTANCE);
        }
        return INSTANCE;
    }

    @SuppressWarnings("finally")
    public boolean insertMediaItem(MediaItem item) {

        boolean status = false;
        try {
            String id = item.getId();
            item.setId(id.replaceAll("::", "%%"));

            SolrMediaItem solrItem = new SolrMediaItem(item);

            server.addBean(solrItem);

            UpdateResponse response = server.commit();
            int statusId = response.getStatus();
            if (statusId == 0) {
                status = true;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getRootLogger().error(ex.getMessage());
        } finally {
            return status;
        }
    }

    @SuppressWarnings("finally")
    public boolean insertMediaItems(List<MediaItem> mediaItems) {

        boolean status = false;
        try {
            List<SolrMediaItem> solrMediaItems = new ArrayList<SolrMediaItem>();
            for (MediaItem mediaItem : mediaItems) {
                SolrMediaItem solrMediaItem = new SolrMediaItem(mediaItem);
                solrMediaItems.add(solrMediaItem);
            }

            server.addBeans(solrMediaItems);

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

    public SearchEngineResponse<MediaItem> findItems(SolrQuery query) {
        return search(query);
    }

    public List<MediaItem> findAllMediaItemsByKeywords(List<String> keywords, String type, int size) {

        List<MediaItem> mediaItems = new ArrayList<MediaItem>(size);
        boolean first = true;
        
        for(String key : keywords)
        	System.out.println("key : "+key);
        
        String query ="(";
        
        if(keywords.size() == 1){
        	if(keywords.get(0).split(" ").length>1)
        		query+="feedKeywordsString:\""+keywords.get(0)+"\"";
        	else
        		query+="feedKeywords:"+keywords.get(0);
        }
        else{
        	List<String> wordEntities = new ArrayList<String>();
        	List<String> simpleWords = new ArrayList<String>();
        	
        	query+="feedKeywordsString:(";
        	
        	//split keywords into two categories
        	for(int i=0;i<keywords.size();i++){
        		if(keywords.get(i).split(" ").length>1){
        			wordEntities.add(keywords.get(i));
        		}
        		else{
        			simpleWords.add(keywords.get(i));
        		}
        	}
        	//feedKeywordsString matches words that are entities (names,organizations,locations)
        	for(int i=0;i<wordEntities.size();i++){
        		if (!first) {
                    query += " OR ";
                }
        		query += "\""+wordEntities.get(i)+"\"";
        		
        		int j=i+1;
        		//else for all other keywords create combinations ((key_1ANDkey_2) OR (key_1ANDkey_3) OR ..)
	        	while(j<wordEntities.size()){

	                query += " OR ";
	    
	        		String oneQuery = "(\""+wordEntities.get(i)+"\" AND \""+wordEntities.get(j)+"\")";
	        		
	        		query += oneQuery;
	        		
	        		int k=j+1;
	        		while(k<wordEntities.size()){
	        			query += " OR ";
	        			    
	 	        		String secQuery = "(\""+wordEntities.get(i)+"\" AND \""+wordEntities.get(j)+"\" AND \""+wordEntities.get(k)+"\")";
	 	        		
	 	        		query += secQuery;
	 	        		
	 	        		k++;
	        		}
	        	
	        		j++;
	        	}
	        	
	        	first = false;
        	}
        	if(first && simpleWords.size()>0)
        		query = "(feedKeywords:(";
        	else if(simpleWords.size()>0){
        		first = true;
        		query+=") OR feedKeywords:(";
        	}
        	for(int i=0;i<simpleWords.size();i++){
        		int j=i+1;
        		// for all other keywords create combinations ((key_1ANDkey_2) OR (key_1ANDkey_3) OR ..)
	        	while(j<simpleWords.size()){
	        		if (!first) 
	        			query += " OR ";
	    
	        		String oneQuery = "("+simpleWords.get(i)+" AND "+simpleWords.get(j)+")";
	        		
	        		query += oneQuery;
	        		
	        		int k=j+1;
	        		while(k<simpleWords.size()){
	        			query += " OR ";
	        			    
	 	        		String secQuery = "("+simpleWords.get(i)+" AND "+simpleWords.get(j)+" AND "+simpleWords.get(k)+")";
	 	        		
	 	        		query += secQuery;
	 	        		
	 	        		k++;
	        		}
	        	
	        		j++;
	        		
	        		first = false;
	        	}
	        	j=0;
	        	while(j<wordEntities.size()){
	        		if (!first) 
	        			query += " OR ";
	    
	        		String oneQuery = "("+simpleWords.get(i)+" AND "+wordEntities.get(j)+")";
	        		
	        		query += oneQuery;
	        		
	        		int k=j+1;
	        		while(k<wordEntities.size()){
	        			query += " OR ";
	        			    
	 	        		String secQuery = "("+simpleWords.get(i)+" AND "+wordEntities.get(j)+" AND "+wordEntities.get(k)+")";
	 	        		
	 	        		query += secQuery;
	 	        		
	 	        		k++;
	        		}
	        		k=i+1;
	        		while(k<simpleWords.size()){
	        			query += " OR ";
	        			    
	 	        		String secQuery = "("+simpleWords.get(i)+" AND "+wordEntities.get(j)+" AND "+simpleWords.get(k)+")";
	 	        		
	 	        		query += secQuery;
	 	        		
	 	        		k++;
	        		}
	        	
	        		j++;
	        		
	        		first = false;
	        	}
        		
        	}
        	query+=")";
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
        query = query.replace("/","\\/");

        SolrQuery solrQuery = new SolrQuery(query);
        Logger.getRootLogger().info("query: " + query);
        solrQuery.setRows(size);
        SearchEngineResponse<MediaItem> response = search(solrQuery);
        
        if(response != null){
	        List<MediaItem> results = response.getResults();
	        Set<String> urls = new HashSet<String>();
	        for(MediaItem mi : results) {
	        	if(!urls.contains(mi.getUrl()) && !mi.getThumbnail().contains("sddefault") && !mi.getUrl().contains("photo_unavailable")) {
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

        if (results==null || results.size() == 0) {
            return null;
        }

        MediaItem mediaItem = results.get(0);
        mediaItem.setId(id);
        return mediaItem;

    }

    private SearchEngineResponse<MediaItem> search(SolrQuery query) {

        SearchEngineResponse<MediaItem> response = new SearchEngineResponse<MediaItem>();
        QueryResponse rsp;

        try {
            rsp = server.query(query);
        } catch (SolrServerException e) {
        	e.printStackTrace();
            Logger.getRootLogger().info(e.getMessage());
            return null;
        }


        List<SolrMediaItem> solrItems = rsp.getBeans(SolrMediaItem.class);
  

        List<MediaItem> mediaItems = new ArrayList<MediaItem>();
        for (SolrMediaItem solrMediaItem : solrItems) {
            try {
            	MediaItem mediaItem = solrMediaItem.toMediaItem();
            	String id = mediaItem.getId();
            	id = id.replaceAll("%%", "::");
            	mediaItem.setId(id);
            	
                mediaItems.add(mediaItem);
            } catch (MalformedURLException ex) {
                Logger.getRootLogger().error(ex.getMessage());
            }
        }

        response.setResults(mediaItems);
         
        return response;
    }
}
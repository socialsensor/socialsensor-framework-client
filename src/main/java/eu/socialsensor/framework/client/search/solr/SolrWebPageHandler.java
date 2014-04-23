package eu.socialsensor.framework.client.search.solr;


import eu.socialsensor.framework.client.dao.WebPageDAO;
import eu.socialsensor.framework.client.dao.impl.WebPageDAOImpl;
import eu.socialsensor.framework.client.mongo.Selector;
import eu.socialsensor.framework.client.search.Query;
import eu.socialsensor.framework.client.search.SearchEngineResponse;
import eu.socialsensor.framework.common.domain.WebPage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class SolrWebPageHandler {

    
    SolrServer server;
    private static Map<String, SolrWebPageHandler> INSTANCES = new HashMap<String, SolrWebPageHandler>();

    private static int commitPeriod = 3000;
    
    // Private constructor prevents instantiation from other classes
    private SolrWebPageHandler(String collection) {
        try {
            server = new HttpSolrServer(collection);
        } catch (Exception e) {
            Logger.getRootLogger().info(e.getMessage());
        }
    }

    // implementing Singleton pattern
    public static SolrWebPageHandler getInstance(String collection) {
    	SolrWebPageHandler INSTANCE = INSTANCES.get(collection);
        if (INSTANCE == null) {
            INSTANCE = new SolrWebPageHandler(collection);
            INSTANCES.put(collection, INSTANCE);
        }
        return INSTANCE;
    }

    @SuppressWarnings("finally")
    public boolean insertWebPage(WebPage webPage) {

        boolean status = true;
        try {

            SolrWebPage solrWebPage = new SolrWebPage(webPage);

            server.addBean(solrWebPage, commitPeriod);

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getRootLogger().error(ex.getMessage());
            status = false;
        } finally {
            return status;
        }
    }

    @SuppressWarnings("finally")
    public boolean insertWebPages(List<WebPage> webPages) {

        boolean status = true;
        try {
            List<SolrWebPage> solrWebPages = new ArrayList<SolrWebPage>();
            for (WebPage webPage : webPages) {
            	SolrWebPage solrWebPage = new SolrWebPage(webPage);
            	solrWebPages.add(solrWebPage);
            }

            server.addBeans(solrWebPages, commitPeriod);

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

    public SearchEngineResponse<WebPage> addFilterAndSearchItems(Query query, String fq) {
        SolrQuery solrQuery = new SolrQuery(query.getQueryString());
        solrQuery.addFilterQuery(fq);
        return search(solrQuery);
    }

    public SearchEngineResponse<WebPage> removeFilterAndSearchItems(
            Query query, String fq) {
        SolrQuery solrQuery = new SolrQuery(query.getQueryString());
        return removeFilterAndSearch(solrQuery, fq);
    }

    @SuppressWarnings("finally")
    public boolean deleteWebPage(String url) {
        boolean status = false;
        try {
            server.deleteByQuery("url:" + url);
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

    public SearchEngineResponse<WebPage> findItems(SolrQuery query) {
        return search(query);
    }

    /*
    public List<WebPage> findAllWebPagesByKeywords(List<String> keywords, String type, int size) {

        List<WebPage> webPages = new ArrayList<WebPage>(size);
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
     */
    

    private SearchEngineResponse<WebPage> removeFilterAndSearch(
            SolrQuery query, String fq) {

        query.removeFilterQuery(fq);
        return search(query);
    }

    public WebPage getSolrWebPage(String url) {

        SolrQuery solrQuery = new SolrQuery("url:" + url);
        SearchEngineResponse<WebPage> mi = search(solrQuery);

        List<WebPage> results = mi.getResults();

        if (results==null || results.size() == 0) {
            return null;
        }

        WebPage webPage = results.get(0);
        return webPage;
    }

    private SearchEngineResponse<WebPage> search(SolrQuery query) {

        SearchEngineResponse<WebPage> response = new SearchEngineResponse<WebPage>();
        QueryResponse rsp;

        try {
            rsp = server.query(query);
        } catch (SolrServerException e) {
        	e.printStackTrace();
            Logger.getRootLogger().info(e.getMessage());
            return null;
        }


        List<SolrWebPage> solrWebPages = rsp.getBeans(SolrWebPage.class);
  

        List<WebPage> webPages = new ArrayList<WebPage>();
        for (SolrWebPage solrWebPage : solrWebPages) {
            try {
            	WebPage webPage = solrWebPage.toWebPage();            	
            	webPages.add(webPage);
            } catch (MalformedURLException ex) {
                Logger.getRootLogger().error(ex.getMessage());
            }
        }

        response.setResults(webPages);
         
        return response;
    }
    
    public static void main(String...args) {
    	
    	SolrWebPageHandler solr = SolrWebPageHandler.getInstance("http://xxx.xxx.xxx.xxx:8080/solr/WebPages");
    	
    	WebPageDAO dao = null;
		try {
			dao = new WebPageDAOImpl("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Selector query = new Selector();
    	query.select("status", "proccessed");
    	
		List<WebPage> webPages = dao.getWebPages(query , -1);
    	
    	for(WebPage webPage : webPages) {
    		 solr.insertWebPage(webPage);
    	}
    }
    
}
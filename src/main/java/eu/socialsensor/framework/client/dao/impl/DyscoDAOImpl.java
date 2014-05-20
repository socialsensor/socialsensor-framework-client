package eu.socialsensor.framework.client.dao.impl;

import eu.socialsensor.framework.client.dao.DyscoDAO;
import eu.socialsensor.framework.client.dao.MediaItemDAO;
import eu.socialsensor.framework.client.dao.WebPageDAO;
import eu.socialsensor.framework.client.search.Query;
import eu.socialsensor.framework.client.search.SearchEngineHandler;
import eu.socialsensor.framework.client.search.SearchEngineResponse;
import eu.socialsensor.framework.client.search.solr.SolrDyscoHandler;
import eu.socialsensor.framework.client.search.solr.SolrHandler;
import eu.socialsensor.framework.client.search.solr.SolrItemHandler;
import eu.socialsensor.framework.client.search.solr.SolrMediaItemHandler;
import eu.socialsensor.framework.client.search.solr.SolrWebPageHandler;
import eu.socialsensor.framework.client.search.visual.JsonResultSet;
import eu.socialsensor.framework.client.search.visual.JsonResultSet.JsonResult;
import eu.socialsensor.framework.client.search.visual.VisualIndexHandler;
import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.MediaItem;
import eu.socialsensor.framework.common.domain.WebPage;
import eu.socialsensor.framework.common.domain.dimension.Dimension;
import eu.socialsensor.framework.common.domain.dysco.Dysco;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class DyscoDAOImpl implements DyscoDAO {

    SearchEngineHandler searchEngineHandler;

    private MediaItemDAO mediaItemDAO;
    private WebPageDAO webPageDAO;

    private SolrItemHandler solrItemHandler;
    private SolrMediaItemHandler solrMediaItemHandler;
    private SolrWebPageHandler solrWebPageHandler;

    private VisualIndexHandler visualIndexHandler;

    public DyscoDAOImpl(String mongoHost, String webPageDB, String webPageColl, String mediaItemsDB, String mediaItemsColl,
            String solrDyscoCollection, String solrItemCollection, String solrMediaItemCollection, String solrWebPageCollection,
            String visualIndexService, String visualIndexCollection)
            throws Exception {

    		searchEngineHandler = new SolrHandler(solrDyscoCollection, solrItemCollection);

        try {
            mediaItemDAO = new MediaItemDAOImpl(mongoHost, mediaItemsDB, mediaItemsColl);
            webPageDAO = new WebPageDAOImpl(mongoHost, webPageDB, webPageColl);

            solrItemHandler = SolrItemHandler.getInstance(solrItemCollection);
            solrMediaItemHandler = SolrMediaItemHandler.getInstance(solrMediaItemCollection);
            solrWebPageHandler = SolrWebPageHandler.getInstance(solrWebPageCollection);

            visualIndexHandler = new VisualIndexHandler(visualIndexService, visualIndexCollection);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean insertDysco(Dysco dysco) {
        return searchEngineHandler.insertDysco(dysco);
    }

    @Override
    public boolean updateDysco(Dysco dysco) {

        return searchEngineHandler.updateDysco(dysco);

    }

    @Override
    public boolean destroyDysco(String id) {
        // TODO: check if this is actually string or long - try to unify it
        return searchEngineHandler.deleteDysco(id);
    }

    @Override
    public List<Item> findDyscoItems(String id) {

        SearchEngineResponse<Item> response = searchEngineHandler
                .findAllDyscoItems(id);
        List<Item> items = response.getResults();
        return items;
    }

    @Override
    public SearchEngineResponse findNDyscoItems(String id, int size) {
        SearchEngineResponse<Item> response = searchEngineHandler
                .findNDyscoItems(id, size);
        return response;
    }

    @Override
    public List<Item> findSortedDyscoItems(String id, String fieldToSort,
            ORDER order, int rows, boolean original) {

        SearchEngineResponse<Item> response = searchEngineHandler
                .findSortedItems(id, fieldToSort, order, rows, original);
        List<Item> items = response.getResults();

        return items;
    }

    @Override
    public List<Item> findSortedDyscoItemsByQuery(Query query, String fieldToSort,
            ORDER order, int rows, boolean original) {

        SearchEngineResponse<Item> response = searchEngineHandler
                .findSortedItems(query, fieldToSort, order, rows, original);
        List<Item> items = response.getResults();

        return items;
    }

    @Override
    public Dysco findDysco(String id) {
        return searchEngineHandler.findDysco(id);
    }

    @Override
    public SearchEngineResponse<Dysco> findDyscosLight(Query query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Dysco findDyscoLight(String id) {
        return searchEngineHandler.findDysco(id);
    }

    @Override
    public SearchEngineResponse<Item> findLatestItems(int count) {
        return searchEngineHandler.findLatestItems(count);
    }

    @Override
    public List<Dysco> findDyscoByTitle(String title) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Dysco> findDyscoByContainingItem(Item item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Dysco> findDyscoByDimension(Dimension dim) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Dysco> findCommunityRelatedDyscos(Dysco queryDysco) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Dysco> findContentRelatedDyscos(Dysco queryDysco) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SearchEngineResponse<Item> findItems(Query query) {
        return searchEngineHandler.findItems(query);
    }

    @Override
    public boolean updateDyscoWithoutItems(Dysco dysco) {
        return searchEngineHandler.updateDyscoWithoutItems(dysco);
    }

    @Override
    public SearchEngineResponse findNDyscoItems(String id, int size, boolean original) {

        SearchEngineResponse<Item> response = searchEngineHandler
                .findNDyscoItems(id, size, original);
        return response;
    }

    @Override
    public List<Item> findTotalItems(String _query) {

        List<Item> _totalItems;
        SolrQuery totalQuery = new SolrQuery(_query);
        //TODO: searchVar... maybe this should be -1 to get all
        totalQuery.setRows(1000);
        //TODO: see if we could minimize the fields returned for totalItems: 
        //links are needed for sure, maybe also sentiment which is used as facet
        //totalQuery.setFields("links","sentiment");
        _totalItems = solrItemHandler.findItems(totalQuery).getResults();

        return _totalItems;

    }

    @Override
    public List<Item> findTotalItems(List<String> dyscoIdsOfGroup) {

        List<Item> _totalItems;

        //getting items of the whole dysco group 
        //formulating the "find all items of dysco group" query
        String totalItemsQuery;

        //this means it's a trending Dysco
        int count = 0;
        String itemsOfGroupQuery = "dyscoId:(";

        for (String dyscoIdToSearch : dyscoIdsOfGroup) {

            if (count == 0) {
                itemsOfGroupQuery = itemsOfGroupQuery + dyscoIdToSearch;
            } else {
                itemsOfGroupQuery = itemsOfGroupQuery + " OR " + dyscoIdToSearch;
            }

            count++;
        }

        itemsOfGroupQuery = itemsOfGroupQuery + ")";

        totalItemsQuery = itemsOfGroupQuery;

        SolrQuery totalQuery = new SolrQuery(totalItemsQuery);
        //TODO: searchVar... maybe this should be -1 to get all
        totalQuery.setRows(1000);
        //TODO: see if we could minimize the fields returned for totalItems: 
        //links are needed for sure, maybe also sentiment which is used as facet
        totalQuery.setFields("links", "sentiment");

        _totalItems = solrItemHandler.findItems(totalQuery).getResults();

        return _totalItems;

    }

    @Override
    //TODO: I think we can get this directly from Solr
    public List<String> findTotalUrls(List<Item> totalItems) {

        //convert HashSet to ArrayList
        Set<String> totalItemsUrls = new HashSet<String>();

        for (Item totalItem : totalItems) {
            URL[] totalItemLinks = totalItem.getLinks();
            if (totalItemLinks != null) {
                for (int i = 0; i < totalItemLinks.length; i++) {
                    totalItemsUrls.add(totalItemLinks[i].toString());
                }
            }

        }
        List totalUrlsToSearch = new ArrayList<String>(totalItemsUrls);

        return totalUrlsToSearch;

    }

    @Override
    public List<Dysco> findRelatedTopics(Dysco dysco) {

        List<Dysco> _relatedTopics = new ArrayList<Dysco>();
//        if ((dysco.getDyscoGroup() != null) && (!dysco.getDyscoGroup().equals(""))) {
//
////          uncomment the following line for getting only the "deprecated" Dyscos 
////          String relatedDyscosQuery = "dyscoGroup:" + dysco.getDyscoGroup() + " AND evolution:old";
//
//            String relatedDyscosQuery = "dyscoGroup:" + dysco.getDyscoGroup();
//
//            SolrQuery _solrQuery = new SolrQuery(relatedDyscosQuery);
//            _solrQuery.setFields("id", "title", "creationDate");
//            _solrQuery.addSortField("creationDate", SolrQuery.ORDER.desc);
//            _solrQuery.setRows(4);
//
//            _relatedTopics = handler.findDyscosLight(_solrQuery).getResults();
//
//            List<Dysco> tempTopics = new ArrayList<Dysco>();
//
//            //remove itself since it's included in the results (think of uncommenting the line above)
//            for (Dysco relatedTopic : _relatedTopics) {
//                if (!dysco.getId().equals(relatedTopic.getId())) {
//                    tempTopics.add(relatedTopic);
//                }
//            }
//            _relatedTopics = tempTopics;
//
//        }
        return _relatedTopics;
    }

    @Override
    public SearchEngineResponse<Item> findItems(String query, List<String> filters, String orderBy, int size) {

        return collectItems(query, filters, orderBy, size);

    }

    @Override
    public SearchEngineResponse<Item> findItems(Dysco dysco, List<String> filters, String orderBy, int size) {

        List<eu.socialsensor.framework.common.domain.Query> queries = dysco.getSolrQueries();
        if (queries.isEmpty()) {
            queries = dysco.getPrimalSolrQueries(); //temporary
        }
        
        return collectItems(queries, filters, orderBy, size);

    }

    @Override
    public SearchEngineResponse<MediaItem> findVideos(String query, List<String> filters, String orderBy, int size) {

        return collectMediaItems(query, "video", filters, orderBy, size);

    }

    @Override
    public SearchEngineResponse<MediaItem> findVideos(Dysco dysco, List<String> filters, String orderBy, int size) {

        List<eu.socialsensor.framework.common.domain.Query> queries = dysco.getSolrQueries();
        if (queries.isEmpty()) {
            queries = dysco.getPrimalSolrQueries(); //temporary
        }
        return collectMediaItems(queries, "video", filters, orderBy, size);

    }

    @Override
    public SearchEngineResponse<MediaItem> findImages(String query, List<String> filters, String orderBy, int size) {

        return collectMediaItems(query, "image", filters, orderBy, size);

    }

    @Override
    public SearchEngineResponse<MediaItem> findImages(Dysco dysco, List<String> filters, String orderBy, int size) {

        List<eu.socialsensor.framework.common.domain.Query> queries = dysco.getSolrQueries();
        if (queries.isEmpty()) {
            queries = dysco.getPrimalSolrQueries(); //temporary
        }
   
        return collectMediaItems(queries, "image", filters, orderBy, size);

    }

    @Override
    public List<WebPage> findHealines(Dysco dysco, int size) {

        Logger.getRootLogger().info("============ Web Pages Retrieval =============");

        List<WebPage> webPages = new ArrayList<WebPage>();
        List<eu.socialsensor.framework.common.domain.Query> queries = dysco.getSolrQueries();
        if (queries == null || queries.isEmpty()) {
            return webPages;
        }

        // Retrieve web pages from solr index
        Set<String> uniqueUrls = new HashSet<String>();
        Set<String> expandedUrls = new HashSet<String>();
        
        boolean first = true;
        String allQueriesToOne = "";
        for (eu.socialsensor.framework.common.domain.Query query : queries) {
        	if(query.getScore() != null){
        		if(query.getScore() > 0.5){
        			if(first){
	            		allQueriesToOne += "("+query.getName()+")";
	            		first = false;
	            	}
	            	else
	            		allQueriesToOne += " OR ("+query.getName()+")";
	        	} 
        	}
        	else{
        		if(first){
            		allQueriesToOne += "("+query.getName()+")";
            		first = false;
            	}
            	else
            		allQueriesToOne += " OR ("+query.getName()+")";
        	}
        }
        
        String queryForRequest = "((title : (" + allQueriesToOne + ")) OR (description:(" + allQueriesToOne + ")))";
        
        SolrQuery solrQuery = new SolrQuery(queryForRequest);
        solrQuery.setRows(size);
        solrQuery.addSortField("score", ORDER.desc);
        solrQuery.addSortField("date", ORDER.desc);

        Logger.getRootLogger().info("Query : " + queryForRequest);
        SearchEngineResponse<WebPage> response = solrWebPageHandler.findItems(solrQuery);
        if (response != null) {
            List<WebPage> results = response.getResults();
            for (WebPage webPage : results) {
                String url = webPage.getUrl();
                String expandedUrl = webPage.getExpandedUrl();
                if (!expandedUrls.contains(expandedUrl) && !uniqueUrls.contains(url)) {
                    int shares = webPageDAO.getWebPageShares(url);
                    webPage.setShares(shares);

                    webPages.add(webPage);
                    uniqueUrls.add(url);
                    expandedUrls.add(expandedUrl);
                }
            }
        }
        Logger.getRootLogger().info(webPages.size() + " web pages retrieved. Re-rank by popularity (#shares)");
        Collections.sort(webPages, new Comparator<WebPage>() {
            public int compare(WebPage wp1, WebPage wp2) {
                if (wp1.getShares() == wp2.getShares()) {
                    if (wp1.getDate().before(wp2.getDate())) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else {
                    return wp1.getShares() < wp2.getShares() ? 1 : -1;
                }
            }
        });

        return webPages.subList(0, Math.min(webPages.size(), size));
    }

    @Override
    public List<WebPage> findHealines(String query, int size) {

        Logger.getRootLogger().info("============ Web Pages Retrieval =============");

        List<WebPage> webPages = new ArrayList<WebPage>();

        // Retrieve web pages from solr index
        Set<String> uniqueUrls = new HashSet<String>();
        Set<String> expandedUrls = new HashSet<String>();

        String queryForRequest = "(title : (" + query + ")) OR (text:(" + query + "))";

        SolrQuery solrQuery = new SolrQuery(queryForRequest);
        solrQuery.setRows(size);
        solrQuery.addSortField("score", ORDER.desc);
        solrQuery.addSortField("date", ORDER.desc);

        Logger.getRootLogger().info("Query : " + query);
        SearchEngineResponse<WebPage> response = solrWebPageHandler.findItems(solrQuery);
        if (response != null) {
            List<WebPage> results = response.getResults();
            for (WebPage webPage : results) {
                String url = webPage.getUrl();
                String expandedUrl = webPage.getExpandedUrl();
                if (!expandedUrls.contains(expandedUrl) && !uniqueUrls.contains(url)) {
                    int shares = webPageDAO.getWebPageShares(url);
                    webPage.setShares(shares);

                    webPages.add(webPage);
                    uniqueUrls.add(url);
                    expandedUrls.add(expandedUrl);
                }
            }
        }

        Logger.getRootLogger().info(webPages.size() + " web pages retrieved. Re-rank by popularity (#shares)");
        Collections.sort(webPages, new Comparator<WebPage>() {
            public int compare(WebPage wp1, WebPage wp2) {
                if (wp1.getShares() == wp2.getShares()) {
                    if (wp1.getDate().before(wp2.getDate())) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else {
                    return wp1.getShares() < wp2.getShares() ? 1 : -1;
                }
            }
        });

        return webPages.subList(0, Math.min(webPages.size(), size));
    }

    @Override
    public List<MediaItem> getMediaItemHistory(String mediaItemId) {
        List<MediaItem> mItems = new ArrayList<MediaItem>();

        Logger.getRootLogger().info("Get visually similar media items for " + mediaItemId);

        int page = 1;
        Set<String> ids = new HashSet<String>();
        while (true) {
            JsonResultSet similar = visualIndexHandler.getSimilarImages(mediaItemId, page++, 100);
            if (similar != null && similar.getResults() != null && similar.getResults().size() > 0) {
                List<JsonResult> results = similar.getResults();
                for (JsonResult result : results) {
                    String mId = result.getId();
                    if (!ids.contains(mId)) {
                        MediaItem mediaItem = mediaItemDAO.getMediaItem(mId);
                        if (mediaItem != null) {
                            mItems.add(mediaItem);
                        }
                    }
                }
            } else {
                break;
            }
        }

        Logger.getRootLogger().info(mItems.size() + "media items retrieved. Re-rank by publication time");
        Collections.sort(mItems, new Comparator<MediaItem>() {
            public int compare(MediaItem mi1, MediaItem mi2) {
                if (mi1.getPublicationTime() < mi2.getPublicationTime()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        return mItems;
    }

    private SearchEngineResponse<Item> collectItems(String query, List<String> filters, String orderBy, int size) {
     
        List<Item> items = new ArrayList<Item>();

        SearchEngineResponse<Item> response = new SearchEngineResponse<Item>();

        if (query.equals("")) {
            return response;
        }

        //Retrieve multimedia content that is stored in solr
        if (!query.contains("title") && !query.contains("description")) {
            query = "((title : " + query + ") OR (description:" + query + ") OR (tags:" + query + "))";
        }

        //Set source filters in case they exist exist
        for (String filter : filters) {
            query += " AND " + filter;
        }

        SolrQuery solrQuery = new SolrQuery(query);

        solrQuery.setRows(200);

        if (orderBy != null) {
            solrQuery.setSortField(orderBy, ORDER.desc);
        } else {
            solrQuery.setSortField("score", ORDER.desc);
        }

        Logger.getRootLogger().info("Solr Query : " + query);

        response = solrItemHandler.findItems(solrQuery);
        if (response != null) {
            List<Item> results = response.getResults();

            for (Item it : results) {
               
                items.add(it);
            
                if ((items.size() >= size)) {
                    break;
                }

            }
        }

        response.setResults(items);

        return response;
    }

    private SearchEngineResponse<Item> collectItems(List<eu.socialsensor.framework.common.domain.Query> queries, List<String> filters, String orderBy, int size) {
        boolean first = true;

        List<Item> items = new ArrayList<Item>();

        SearchEngineResponse<Item> response = new SearchEngineResponse<Item>();

        if (queries.isEmpty()) {
            return response;
        }

        //Retrieve multimedia content that is stored in solr
        String allQueriesToOne = "";
        for (eu.socialsensor.framework.common.domain.Query query : queries) {
        	if(query.getScore() != null){
        		if(query.getScore() > 0.5){
        			if(first){
	            		allQueriesToOne += "("+query.getName()+")";
	            		first = false;
	            	}
	            	else
	            		allQueriesToOne += " OR ("+query.getName()+")";
	        	} 
        	}
        	else{
        		if(first){
            		allQueriesToOne += "("+query.getName()+")";
            		first = false;
            	}
            	else
            		allQueriesToOne += " OR ("+query.getName()+")";
        	}
        }
        String queryForRequest = "((title : (" + allQueriesToOne + ")) OR (description:(" + allQueriesToOne + ")))";

        //Set source filters in case they exist exist
        for (String filter : filters) {
            queryForRequest += " AND " + filter;
        }

        SolrQuery solrQuery = new SolrQuery(queryForRequest);

        solrQuery.setRows(200);

        if (orderBy != null) {
            solrQuery.setSortField(orderBy, ORDER.desc);
        } else {
            solrQuery.setSortField("score", ORDER.desc);
        }

        Logger.getRootLogger().info("Solr Query: " + queryForRequest);

        response = solrItemHandler.findItems(solrQuery);
        if (response != null) {
            List<Item> results = response.getResults();

            for (Item it : results) {

                items.add(it);
               
                if (items.size() >= size) {
                    break;
                }

            }
        }
        
        response.setResults(items);
        return response;
    }

    private SearchEngineResponse<MediaItem> collectMediaItems(String query, String type, List<String> filters, String orderBy, int size) {

        List<MediaItem> mediaItems = new LinkedList<MediaItem>();

        SearchEngineResponse<MediaItem> response = new SearchEngineResponse<MediaItem>();

        Map<Double, MediaItem> scoredMediaItems = new TreeMap<Double, MediaItem>(Collections.reverseOrder());

        if (query.equals("")) {
            return response;
        }

        //Retrieve multimedia content that is stored in solr
        if (!query.contains("title") && !query.contains("description")) {
            query = "((title : " + query + ") OR (description:" + query + ") OR (tags:" + query + "))";
        }

        //Set filters in case they exist exist
        for (String filter : filters) {
            query += " AND " + filter;
        }

        query += " AND (type : " + type + ")";

        SolrQuery solrQuery = new SolrQuery(query);

        solrQuery.setRows(200);

        if (orderBy != null) {
            solrQuery.setSortField(orderBy, ORDER.desc);
        } else {
            solrQuery.setSortField("score", ORDER.desc);
        }
        Logger.getRootLogger().info("Solr Query : " + query);

        response = solrMediaItemHandler.findItems(solrQuery);
        if (response != null) {
            List<MediaItem> results = response.getResults();
            Set<String> urls = new HashSet<String>();
            for (MediaItem mi : results) {

                if (!urls.contains(mi.getUrl())) {
                    
                    mediaItems.add(mi);
                   
                    urls.add(mi.getUrl());
                }

                if ((mediaItems.size() >= size)) {
                    break;
                }

            }
        }

        response.setResults(mediaItems);
        return response;
    }

    private SearchEngineResponse<MediaItem> collectMediaItems(List<eu.socialsensor.framework.common.domain.Query> queries, String type, List<String> filters, String orderBy, int size) {
    	boolean first = true;
    	
        List<MediaItem> mediaItems = new ArrayList<MediaItem>();

        SearchEngineResponse<MediaItem> response = new SearchEngineResponse<MediaItem>();

        if (queries.isEmpty()) {
            return response;
        }
       
    	//Retrieve multimedia content that is stored in solr
        String allQueriesToOne = "";
        for (eu.socialsensor.framework.common.domain.Query query : queries) {
        	if(query.getScore() != null){
        		if(query.getScore() > 0.5){
        			if(first){
	            		allQueriesToOne += "("+query.getName()+")";
	            		first = false;
	            	}
	            	else
	            		allQueriesToOne += " OR ("+query.getName()+")";
	        	} 
        	}
        	else{
        		if(first){
            		allQueriesToOne += "("+query.getName()+")";
            		first = false;
            	}
            	else
            		allQueriesToOne += " OR ("+query.getName()+")";
        	}
        }
        
        String queryForRequest = "((title : (" + allQueriesToOne + ")) OR (description:(" + allQueriesToOne + ")))";
        
        
        //Set filters in case they exist exist
        for (String filter : filters) {
            queryForRequest += " AND " + filter;
        }

        queryForRequest += " AND (type : " + type + ")";

        SolrQuery solrQuery = new SolrQuery(queryForRequest);
        Logger.getRootLogger().info("Solr Query: " + queryForRequest);
      
        solrQuery.setRows(200);
        //solrQuery.addFilterQuery("publicationTime:["+86400000+" TO *]");
        if (orderBy != null) {
            solrQuery.setSortField(orderBy, ORDER.desc);
        } else {
            solrQuery.setSortField("score", ORDER.desc);
        }     

        response = solrMediaItemHandler.findItems(solrQuery);
        if (response != null) {
            List<MediaItem> results = response.getResults();
            Set<String> urls = new HashSet<String>();
            for (MediaItem mi : results) {

                if (!urls.contains(mi.getUrl())) {
                   
                    mediaItems.add(mi);
                   
                    urls.add(mi.getUrl());
                }

                if ((mediaItems.size() >= size)) {
                    break;
                }

            }
        }
      
        response.setResults(mediaItems);
        return response;
    }

    public List<MediaItem> requestThumbnails(Dysco dysco, int size) {
        return null;
    }

    public static void main(String[] args) {
    	
    	
    }
}

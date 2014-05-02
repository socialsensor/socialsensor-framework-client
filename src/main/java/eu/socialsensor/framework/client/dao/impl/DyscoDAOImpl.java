package eu.socialsensor.framework.client.dao.impl;

import eu.socialsensor.framework.client.dao.DyscoDAO;
import eu.socialsensor.framework.client.dao.WebPageDAO;
import eu.socialsensor.framework.client.search.Query;
import eu.socialsensor.framework.client.search.SearchEngineHandler;
import eu.socialsensor.framework.client.search.SearchEngineResponse;
import eu.socialsensor.framework.client.search.solr.SolrHandler;
import eu.socialsensor.framework.client.search.solr.SolrItemHandler;
import eu.socialsensor.framework.client.search.solr.SolrMediaItemHandler;
import eu.socialsensor.framework.client.search.solr.SolrWebPageHandler;
import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.MediaItem;
import eu.socialsensor.framework.common.domain.RankingValue;
import eu.socialsensor.framework.common.domain.SocialNetworkSource;
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
import java.util.Queue;
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
    
    //private MediaItemDAO mediaItemDAO;
    private WebPageDAO webPageDAO;
    //private DyscoRequestDAO dyscoRequestDAO;
    
    private SolrItemHandler solrItemHandler;
    //private SolrDyscoHandler solrDyscoHandler;
    private SolrMediaItemHandler solrMediaItemHandler;
    private SolrWebPageHandler solrWebPageHandler;

    public DyscoDAOImpl(String mongoHost, String webPageDB, 
    		String solrDyscoCollection, String solrItemCollection, String solrMediaItemCollection, String solrWebPageCollection) 
    				throws Exception {
    	searchEngineHandler = new SolrHandler(solrDyscoCollection, solrItemCollection);
    	
    	try {
    		//mediaItemDAO = new MediaItemDAOImpl(mongoHost,"Streams","MediaItems");
    		webPageDAO = new WebPageDAOImpl(mongoHost,webPageDB,"WebPages");
        	//dyscoRequestDAO = new DyscoRequestDAOImpl(mongoHost,"Streams","Dyscos");
        	
			solrItemHandler = SolrItemHandler.getInstance(solrItemCollection);
			//solrDyscoHandler = SolrDyscoHandler.getInstance(solrDyscoCollection);
	    	solrMediaItemHandler = SolrMediaItemHandler.getInstance(solrMediaItemCollection);
	    	solrWebPageHandler = SolrWebPageHandler.getInstance(solrWebPageCollection);
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
        totalQuery.setFields("links","sentiment");
 
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
    public List<Item> findItems(String query, SocialNetworkSource source, RankingValue orderBy, int size){
    	List<Item> items = new ArrayList<Item>();
    	
    	items.addAll(collectItems(query,source,orderBy,size));
    	
    	return items;
    }
    
    @Override
    public List<Item> findItems(Dysco dysco, SocialNetworkSource source, RankingValue orderBy, int size){
    	List<Item> items = new ArrayList<Item>();
    	List<eu.socialsensor.framework.common.domain.Query> queries = dysco.getSolrQueries();
    	if(queries.isEmpty())
    		queries = dysco.getPrimalSolrQueries(); //temporary
    	items.addAll(collectItems(queries,source,orderBy,size));
    	
    	return items;
    }
    
   
    @Override
    public List<MediaItem> findVideos(String query, SocialNetworkSource source, RankingValue orderBy, int size){
    	List<MediaItem> mediaItems = new ArrayList<MediaItem>();
    	
    	mediaItems.addAll(collectMediaItems(query,"video",source,orderBy,size));
    	return mediaItems;	
    	
    }
    
    @Override
    public List<MediaItem> findVideos(Dysco dysco, SocialNetworkSource source, RankingValue orderBy, int size) {
    	List<MediaItem> mediaItems = new ArrayList<MediaItem>();
    	
    	String query = dysco.getSolrQueryString();
    	List<eu.socialsensor.framework.common.domain.Query> queries = dysco.getSolrQueries();
    	if(queries.isEmpty())
    		queries = dysco.getPrimalSolrQueries(); //temporary
    	mediaItems.addAll(collectMediaItems(queries,"video",source,orderBy,size));
    	return mediaItems;
    }
    
    
    @Override
    public List<MediaItem> findImages(String query, SocialNetworkSource source, RankingValue orderBy, int size){
    	List<MediaItem> mediaItems = new ArrayList<MediaItem>();
    	
    	mediaItems.addAll(collectMediaItems(query,"image",source,orderBy,size));
    	return mediaItems;	
    	
    }

    @Override
    public List<MediaItem> findImages(Dysco dysco, SocialNetworkSource source, RankingValue orderBy, int size) {
    	List<MediaItem> mediaItems = new ArrayList<MediaItem>();
    	
    	
    	List<eu.socialsensor.framework.common.domain.Query> queries = dysco.getSolrQueries();
    	if(queries.isEmpty())
    		queries = dysco.getPrimalSolrQueries(); //temporary
    	mediaItems.addAll(collectMediaItems(queries,"image",source,orderBy,size));
    	return mediaItems;
    }
    
    @Override
	public List<MediaItem> findImagesByLocation(Dysco dysco,SocialNetworkSource source, RankingValue orderBy, String location, int size){
    	List<MediaItem> mediaItems = new ArrayList<MediaItem>();
    	
    	return mediaItems;
    }
    
    @Override
	public List<MediaItem> findImagesByLocation(String query,SocialNetworkSource source, RankingValue orderBy, String location, int size){
    	List<MediaItem> mediaItems = new ArrayList<MediaItem>();
    	
    	return mediaItems;
    }
    
    @Override
	public List<MediaItem> findImagesByConcept(String query,SocialNetworkSource source, RankingValue orderBy, String concept, int size){
    	List<MediaItem> mediaItems = new ArrayList<MediaItem>();
    	
    	return mediaItems;
    }
    
    @Override
	public List<MediaItem> findImagesByConcept(Dysco dysco,SocialNetworkSource source, RankingValue orderBy, String concept, int size){
    	List<MediaItem> mediaItems = new ArrayList<MediaItem>();
    	
    	return mediaItems;
    }

    @Override
    public List<WebPage> findHealines(Dysco dysco, int size) {
    	
    	Logger.getRootLogger().info("============ Web Pages Retrieval =============");
    	
    	List<WebPage> webPages = new ArrayList<WebPage>();
   
    	List<eu.socialsensor.framework.common.domain.Query> queries = dysco.getSolrQueries(); 	
    	if(queries==null || queries.isEmpty()) {
    		return webPages;
    	}
    	
    	//Retrieve web pages that is stored in solr
    	Set<String> uniqueUrls = new HashSet<String>();
    	for(eu.socialsensor.framework.common.domain.Query query : queries) {
    		String queryForRequest = "(title : ("+query.getName()+")) OR (text:("+query.getName()+"))";
    		
    		SolrQuery solrQuery = new SolrQuery(queryForRequest);
    		solrQuery.setRows(20);
        	solrQuery.addSortField("score", ORDER.desc);
        	solrQuery.addSortField("date", ORDER.desc);
        	
        	Logger.getRootLogger().info("Query : " + query);
        	SearchEngineResponse<WebPage> response = solrWebPageHandler.findItems(solrQuery);
        	if(response != null) {
        		List<WebPage> results = response.getResults();
    	        for(WebPage webPage : results) {
    	        	String url = webPage.getUrl();
    	        	String expandedUrl = webPage.getExpandedUrl(); 	
    		        if(!uniqueUrls.contains(expandedUrl)) {
    		        	WebPage updatedWP = webPageDAO.getWebPage(url);
    		        	webPage.setShares(updatedWP.getShares());
    		        	
    		        	webPages.add(webPage);
    		        	uniqueUrls.add(expandedUrl);
    		        }
    	        }    
        	}
    	}
    	
    	Logger.getRootLogger().info(webPages.size() + " web pages retrieved. Re-rank...");
    	Collections.sort(webPages, new Comparator<WebPage>() {
            public int compare(WebPage wp1, WebPage wp2) {
                if (wp1.getShares() == wp2.getShares()) {
                    return 0;
                } else {
                    return wp1.getShares()<=wp2.getShares()?1:-1; 
                }
            }
        });
    	
    	return webPages.subList(0, Math.min(webPages.size(), size));
    }
    
  
    private Queue<Item> collectItems(String query, SocialNetworkSource source, RankingValue orderBy, int size){
    	boolean defaultOperation = false;
    	double aggregatedScore = 0;
    	Queue<Item> items = new LinkedList<Item>();
    	
    	Map<Double,Item> scoredItems = new TreeMap<Double,Item>(Collections.reverseOrder());
    	
    	if(query.equals(""))
    		return items;
    
    	//Retrieve multimedia content that is stored in solr
    	
    	if(!query.contains("title") && !query.contains("description"))
    		query = "((title : "+query+") OR (description:"+query+") OR (tags:"+query+"))";
    
    	//Set source filters in case they exist exist
    	
		if(source.equals(SocialNetworkSource.Twitter))
			query += " AND (streamId:Twitter)";
		if(source.equals(SocialNetworkSource.Facebook))
			query += " AND (streamId:Facebook)";
    	
    	SolrQuery solrQuery = new SolrQuery(query);
    	
    	solrQuery.setRows(200);
    	
    	if(orderBy.equals(RankingValue.Relevance))
    		solrQuery.setSortField("score", ORDER.desc);
    	if(orderBy.equals(RankingValue.Recency))
    		solrQuery.setSortField("publicationTime", ORDER.desc);
    	if(orderBy.equals(RankingValue.Popularity))
    		solrQuery.setSortField("popularity", ORDER.desc);
    	if(orderBy.equals(RankingValue.Default)){
    		solrQuery.setSortField("score", ORDER.desc);
    		defaultOperation = true;
    	}
    	Logger.getRootLogger().info("Solr Query : " + query);
    	
    	SearchEngineResponse<Item> response = solrItemHandler.findItems(solrQuery);
    	if(response != null){
    		List<Item> results = response.getResults();
    		
	        for(Item it : results) {
	
		        	
        		if(defaultOperation){
        			aggregatedScore++;
        			System.out.println("Storing item : "+it.getId()+" with score : "+aggregatedScore);
        			scoredItems.put(aggregatedScore, it);
        		}
        		else
        			items.add(it);
		        	
		        	if((items.size() >= size) || scoredItems.size() >= size)
		        		break;
	        	
	        }
    	}
    	
    	//rank media items with default method
    	if(defaultOperation){
    		Map<Double,Item> rankedItems = new TreeMap<Double,Item>(Collections.reverseOrder());
    		for(Map.Entry<Double, Item> entry : scoredItems.entrySet()){
    			Double res = entry.getKey() * (entry.getValue().getPublicationTime()/1000000);
    			rankedItems.put(res, entry.getValue());
    		}
    		
    		for(Item it : rankedItems.values())
    			items.add(it);
    	}
    	return items;
    }
    
    
    private Queue<Item> collectItems(List<eu.socialsensor.framework.common.domain.Query> queries,SocialNetworkSource source, RankingValue orderBy,int size){
    	boolean defaultOperation = false;
    	double aggregatedScore = 0;
    	Queue<Item> items = new LinkedList<Item>();
    	
    	Map<Double,Item> scoredItems = new TreeMap<Double,Item>(Collections.reverseOrder());
    	
    	if(queries.isEmpty())
    		return items;
    
    	//Retrieve multimedia content that is stored in solr
    	for(eu.socialsensor.framework.common.domain.Query query : queries){
    		String queryForRequest = "((title : ("+query.getName()+")) OR (description:("+query.getName()+")) OR (tags:("+query.getName()+")))";
    		
    		//Set source filters in case they exist exist
        	if(!source.equals(SocialNetworkSource.All)){
        		if(source.equals(SocialNetworkSource.Twitter))
        			queryForRequest += " AND (streamId:Twitter)";
        		if(source.equals(SocialNetworkSource.Facebook))
        			queryForRequest += " AND (streamId:Facebook)";
        		if(source.equals(SocialNetworkSource.Flickr))
        			queryForRequest += " AND (streamId:Flickr)";
        		if(source.equals(SocialNetworkSource.GooglePlus))
        			queryForRequest += " AND (streamId:GooglePlus)";
        		if(source.equals(SocialNetworkSource.Tumblr))
        			queryForRequest += " AND (streamId:Tumblr)";
        		if(source.equals(SocialNetworkSource.Instagram))
        			queryForRequest += " AND (streamId:Instagram)";
        		if(source.equals(SocialNetworkSource.Youtube))
        			queryForRequest += " AND (streamId:Youtube)";
        	}
        	
        	
        	SolrQuery solrQuery = new SolrQuery(queryForRequest);
        	
        	solrQuery.setRows(200);
        	
        	if(orderBy.equals(RankingValue.Relevance))
        		solrQuery.setSortField("score", ORDER.desc);
        	if(orderBy.equals(RankingValue.Recency))
        		solrQuery.setSortField("publicationTime", ORDER.desc);
        	if(orderBy.equals(RankingValue.Popularity))
        		solrQuery.setSortField("popularity", ORDER.desc);
        	if(orderBy.equals(RankingValue.Default)){
        		solrQuery.setSortField("score", ORDER.desc);
        		defaultOperation = true;
        	}
        	
        	
        	Logger.getRootLogger().info("Solr Query: " + queryForRequest);
        	
        	SearchEngineResponse<Item> response = solrItemHandler.findItems(solrQuery);
        	if(response != null){
        		List<Item> results = response.getResults();
        		
    	        for(Item it : results) {
    
	        		if(defaultOperation){
	        			aggregatedScore++;
	        			double score = 1.0;
	        			if(query.getScore()!=null)
	        				score = query.getScore();
	        			scoredItems.put(aggregatedScore * score, it);
	        		}
	        		else
	        			items.add(it);
		        	
		        		
		        	
		        	
		        	if(items.size() >= size || scoredItems.size() >=size)
		        		break;
    	        	
    	        }
        	}
    	}
    	
    	
    	//rank media items with default method
    	if(defaultOperation){
    		Map<Double,Item> rankedItems = new TreeMap<Double,Item>(Collections.reverseOrder());
    		for(Map.Entry<Double, Item> entry : scoredItems.entrySet()){
    			Double res = entry.getKey() * (entry.getValue().getPublicationTime()/100000);
    			
    			rankedItems.put(res, entry.getValue());
    		}
    		
    		for(Item it : rankedItems.values())
    			items.add(it);
    	}
    	
    	return items;
    }
    
   
    private Queue<MediaItem> collectMediaItems(String query, String type, SocialNetworkSource source, RankingValue orderBy, int size){
    	boolean defaultOperation = false;
    	double aggregatedScore = 0;
    	Queue<MediaItem> mediaItems = new LinkedList<MediaItem>();
    	
    	Map<Double,MediaItem> scoredMediaItems = new TreeMap<Double,MediaItem>(Collections.reverseOrder());
    	
    	if(query.equals(""))
    		return mediaItems;
    
    	//Retrieve multimedia content that is stored in solr
    	
    	if(!query.contains("title") && !query.contains("description"))
    		query = "((title : "+query+") OR (description:"+query+") OR (tags:"+query+"))";
    
    	//Set source filters in case they exist exist
    	if(!source.equals(SocialNetworkSource.All)){
    		if(source.equals(SocialNetworkSource.Twitter))
    			query += " AND (streamId:Twitter)";
    		if(source.equals(SocialNetworkSource.Facebook))
    			query += " AND (streamId:Facebook)";
    		if(source.equals(SocialNetworkSource.Flickr))
    			query += " AND (streamId:Flickr)";
    		if(source.equals(SocialNetworkSource.GooglePlus))
    			query += " AND (streamId:GooglePlus)";
    		if(source.equals(SocialNetworkSource.Tumblr))
    			query += " AND (streamId:Tumblr)";
    		if(source.equals(SocialNetworkSource.Instagram))
    			query += " AND (streamId:Instagram)";
    		if(source.equals(SocialNetworkSource.Youtube))
    			query += " AND (streamId:Youtube)";
    	}
    	
    	query += " AND (type : "+type+")";
    	
    	SolrQuery solrQuery = new SolrQuery(query);
    	
    	solrQuery.setRows(200);
    	
    	if(orderBy.equals(RankingValue.Relevance))
    		solrQuery.setSortField("score", ORDER.desc);
    	if(orderBy.equals(RankingValue.Recency))
    		solrQuery.setSortField("publicationTime", ORDER.desc);
    	if(orderBy.equals(RankingValue.Popularity))
    		solrQuery.setSortField("popularity", ORDER.desc);
    	if(orderBy.equals(RankingValue.Default)){
    		solrQuery.setSortField("score", ORDER.desc);
    		defaultOperation = true;
    	}
    	Logger.getRootLogger().info("Solr Query : " + query);
    	
    	SearchEngineResponse<MediaItem> response = solrMediaItemHandler.findItems(solrQuery);
    	if(response != null){
    		List<MediaItem> results = response.getResults();
    		Set<String> urls = new HashSet<String>();
	        for(MediaItem mi : results) {
	        	
	        	
		        	if(!urls.contains(mi.getUrl())) {
		        		if(defaultOperation){
		        			aggregatedScore++;
		        			scoredMediaItems.put(aggregatedScore, mi);
		        		}
		        		else
		        			mediaItems.add(mi);
		        	
		        		urls.add(mi.getUrl());
		        	}
		        	
		        	if((mediaItems.size() >= size) || scoredMediaItems.size() >= size)
		        		break;
	        	
	        }
    	}
    	
    	//rank media items with default method
    	if(defaultOperation){
    		Map<Double,MediaItem> rankedMediaItems = new TreeMap<Double,MediaItem>(Collections.reverseOrder());
    		for(Map.Entry<Double, MediaItem> entry : scoredMediaItems.entrySet()){
    			Double res = entry.getKey() * (entry.getValue().getPublicationTime()/1000000) *(entry.getValue().getLikes()
    					+ entry.getValue().getViews() + entry.getValue().getShares() + entry.getValue().getComments()+1);
    			
    			rankedMediaItems.put(res, entry.getValue());
    		}
    		
    		for(MediaItem mi : rankedMediaItems.values())
    			mediaItems.add(mi);
    	}
    	return mediaItems;
    }
    
    private Queue<MediaItem> collectMediaItems(List<eu.socialsensor.framework.common.domain.Query> queries, String type,SocialNetworkSource source, RankingValue orderBy,int size){
    	boolean defaultOperation = false;
    	double aggregatedScore = 0;
    	Queue<MediaItem> mediaItems = new LinkedList<MediaItem>();
    	
    	Map<Double,MediaItem> scoredMediaItems = new TreeMap<Double,MediaItem>(Collections.reverseOrder());
    	
    	if(queries.isEmpty())
    		return mediaItems;
    
    	//Retrieve multimedia content that is stored in solr
    	for(eu.socialsensor.framework.common.domain.Query query : queries){
    		String queryForRequest = "((title : ("+query.getName()+")) OR (description:("+query.getName()+")) OR (tags:("+query.getName()+")))";
    		
    		//Set source filters in case they exist exist
        	if(!source.equals(SocialNetworkSource.All)){
        		if(source.equals(SocialNetworkSource.Twitter))
        			queryForRequest += " AND (streamId:Twitter)";
        		if(source.equals(SocialNetworkSource.Facebook))
        			queryForRequest += " AND (streamId:Facebook)";
        		if(source.equals(SocialNetworkSource.Flickr))
        			queryForRequest += " AND (streamId:Flickr)";
        		if(source.equals(SocialNetworkSource.GooglePlus))
        			queryForRequest += " AND (streamId:GooglePlus)";
        		if(source.equals(SocialNetworkSource.Tumblr))
        			queryForRequest += " AND (streamId:Tumblr)";
        		if(source.equals(SocialNetworkSource.Instagram))
        			queryForRequest += " AND (streamId:Instagram)";
        		if(source.equals(SocialNetworkSource.Youtube))
        			queryForRequest += " AND (streamId:Youtube)";
        	}
        	
        	
        	queryForRequest += " AND (type : "+type+")";
        	
        	SolrQuery solrQuery = new SolrQuery(queryForRequest);
        	
        	solrQuery.setRows(200);
        	
        	if(orderBy.equals(RankingValue.Relevance))
        		solrQuery.setSortField("score", ORDER.desc);
        	if(orderBy.equals(RankingValue.Recency))
        		solrQuery.setSortField("publicationTime", ORDER.desc);
        	if(orderBy.equals(RankingValue.Popularity))
        		solrQuery.setSortField("popularity", ORDER.desc);
        	if(orderBy.equals(RankingValue.Default)){
        		solrQuery.setSortField("score", ORDER.desc);
        		defaultOperation = true;
        	}
        	
        	
        	Logger.getRootLogger().info("Solr Query: " + queryForRequest);
        	
        	SearchEngineResponse<MediaItem> response = solrMediaItemHandler.findItems(solrQuery);
        	if(response != null){
        		List<MediaItem> results = response.getResults();
        		Set<String> urls = new HashSet<String>();
    	        for(MediaItem mi : results) {
    	        	
    	        	//if(mi.getType().equals(type)) {
    		        	if(!urls.contains(mi.getUrl())) {
    		        		if(defaultOperation){
    		        			aggregatedScore++;
    		        			double score = 1.0;
    		        			if(query.getScore()!=null)
    		        				score = query.getScore();
    		        			scoredMediaItems.put(aggregatedScore * score, mi);
    		        		}
    		        		else
    		        			mediaItems.add(mi);
    		        	
    		        		urls.add(mi.getUrl());
    		        	}
    		        	
    		        	if((mediaItems.size() >= size) || scoredMediaItems.size() >= size)
    		        		break;
    	        	//}
    	        }
        	}
    	}
    	
    	
    	//rank media items with default method
    	if(defaultOperation){
    		Map<Double,MediaItem> rankedMediaItems = new TreeMap<Double,MediaItem>(Collections.reverseOrder());
    		for(Map.Entry<Double, MediaItem> entry : scoredMediaItems.entrySet()){
    			Double res = entry.getKey() * (entry.getValue().getPublicationTime()/100000) *(entry.getValue().getLikes()
    					+ entry.getValue().getViews() + entry.getValue().getShares() + entry.getValue().getComments()+1);
    			
    			rankedMediaItems.put(res, entry.getValue());
    		}
    		
    		for(MediaItem mi : rankedMediaItems.values())
    			mediaItems.add(mi);
    	}
    	
    	return mediaItems;
    }
    
    
    public List<MediaItem> requestThumbnails(Dysco dysco, int size) {
    	return null;
    }
   
    public static void main(String[] args) {
    	
    }
}

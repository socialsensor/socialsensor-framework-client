package eu.socialsensor.framework.client.dao.impl;

import eu.socialsensor.framework.client.dao.DyscoDAO;
import eu.socialsensor.framework.client.dao.DyscoRequestDAO;
import eu.socialsensor.framework.client.dao.MediaItemDAO;
import eu.socialsensor.framework.client.search.Query;
import eu.socialsensor.framework.client.search.SearchEngineHandler;
import eu.socialsensor.framework.client.search.SearchEngineResponse;
import eu.socialsensor.framework.client.search.solr.SolrDyscoHandler;
import eu.socialsensor.framework.client.search.solr.SolrHandler;
import eu.socialsensor.framework.client.search.solr.SolrItemHandler;
import eu.socialsensor.framework.client.search.solr.SolrMediaItemHandler;
import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.MediaItem;
import eu.socialsensor.framework.common.domain.dimension.Dimension;
import eu.socialsensor.framework.common.domain.dysco.Dysco;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class DyscoDAOImpl implements DyscoDAO {

    SearchEngineHandler searchEngineHandler = new SolrHandler();
    private static final MediaItemDAO mediaItemDAO = new MediaItemDAOImpl("social1.atc.gr","Streams","MediaItems");
    private static final DyscoRequestDAO dyscoRequestDAO = new DyscoRequestDAOImpl("social1.atc.gr","Streams","Dyscos");
    private static final SolrItemHandler solrItemHandler = SolrItemHandler.getInstance();
    private static final SolrDyscoHandler handler = SolrDyscoHandler.getInstance();
    private static final SolrMediaItemHandler solrMediaItemHandler = SolrMediaItemHandler.getInstance("");

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
        if ((dysco.getDyscoGroup() != null) && (!dysco.getDyscoGroup().equals(""))) {

//          uncomment the following line for getting only the "deprecated" Dyscos 
//          String relatedDyscosQuery = "dyscoGroup:" + dysco.getDyscoGroup() + " AND evolution:old";

            String relatedDyscosQuery = "dyscoGroup:" + dysco.getDyscoGroup();

            SolrQuery _solrQuery = new SolrQuery(relatedDyscosQuery);
            _solrQuery.setFields("id", "title", "creationDate");
            _solrQuery.addSortField("creationDate", SolrQuery.ORDER.desc);
            _solrQuery.setRows(4);

            _relatedTopics = handler.findDyscosLight(_solrQuery).getResults();

            List<Dysco> tempTopics = new ArrayList<Dysco>();

            //remove itself since it's included in the results (think of uncommenting the line above)
            for (Dysco relatedTopic : _relatedTopics) {
                if (!dysco.getId().equals(relatedTopic.getId())) {
                    tempTopics.add(relatedTopic);
                }
            }
            _relatedTopics = tempTopics;

        }
        return _relatedTopics;
    }

    @Override
    public List<MediaItem> findVideos(List<String> dyscoIds, List<String> urls, int size) {

        List<MediaItem> _videos = new ArrayList<MediaItem>();
        List<MediaItem> videosFromSearch = new ArrayList<MediaItem>();

        if (urls!=null && urls.size()>0) {
            _videos = mediaItemDAO.getMediaItemsForUrls(urls, "video", size);
            for (MediaItem video : _videos) {
                video.setSource(2);
            }
        }
        Logger.getRootLogger().info("found videos from urls: " + _videos.size());
        
      //finding videos if the already fetched videos are less than MAX_VIDEOS
        if (dyscoIds != null && _videos.size() < size) {
        	int remaining = size - _videos.size();
        	//videosFromSearch = mediaItemDAO.getMediaItemsByDyscos(dyscoIds, "video", remaining);
            
            Logger.getRootLogger().info("videos to search: " + remaining);
            List<String> feedKeywords = dyscoRequestDAO.readKeywordsFromDyscos(dyscoIds);
            if (feedKeywords != null) {
                if (feedKeywords.size() > 0) {
                	videosFromSearch = solrMediaItemHandler.findAllMediaItemsByKeywords(feedKeywords, "video", remaining);
                }
            }
            Logger.getRootLogger().info("found videos from search: " + videosFromSearch.size());

            for (MediaItem video : videosFromSearch) {
                video.setSource(1);
            }

            _videos.addAll(videosFromSearch);
        }

        return _videos;
    }

    @Override
    // method to be called from outside (works only for trending dyscos)
    public List<MediaItem> findImages(Dysco _dysco, int size) {

        List<MediaItem> _images = new ArrayList<MediaItem>();
        List<Dysco> relatedTopics = findRelatedTopics(_dysco);

        List<String> _dyscoIdsOfGroup = new ArrayList<String>();

        for (Dysco relatedTopic : relatedTopics) {
            _dyscoIdsOfGroup.add(relatedTopic.getId());
        }

        //add "this" Dysco
        _dyscoIdsOfGroup.add(_dysco.getId());

        //TODO: the following is not very good practice - to set null
        if (_dyscoIdsOfGroup != null) {

            List<Item> totalItems = findTotalItems(_dyscoIdsOfGroup);
            List<String> totalUrlsToSearch = findTotalUrls(totalItems);

            _images = findImages(_dyscoIdsOfGroup, totalUrlsToSearch, size);

        }
        return _images;
    }

    @Override
    public List<MediaItem> findImages(List<String> dyscoIds, List<String> urls, int size) {

        //TODO: maybe it's worth changing order to make it more efficient
        List<MediaItem> _images = new ArrayList<MediaItem>();
        List<MediaItem> imagesFromSearch = new ArrayList<MediaItem>();
        
        if(dyscoIds != null && dyscoIds.size()>0) {
        	Set<String> mediaLinks = new HashSet<String>();
    		List<Item> items = findTotalItems(dyscoIds);
    	
    		if(items!=null && items.size()>0) {
    			for(Item item : items) {
    				List<MediaItem> temp = item.getMediaItems();
    				if(temp!=null) {
    					for(MediaItem mItem : temp) {
    						mediaLinks.add(mItem.getUrl());
    					}
    				}
    			}

    			List<MediaItem> mi = mediaItemDAO.getMediaItemsByUrls(new ArrayList<String>(mediaLinks) , "image", size);
    			_images.addAll(mi);	
    			for (MediaItem image : _images) {
            		image.setSource(2);
        		}
    			Logger.getRootLogger().info("found embedded images: " + _images.size());
    		}
        }
        
        if (urls!=null && urls.size()>0 && _images.size() < size) { 
        	int remaining = size - _images.size();
            _images.addAll(mediaItemDAO.getMediaItemsForUrls(urls, "image", remaining));
            for (MediaItem image : _images) {
                image.setSource(2);
            }
        }
        Logger.getRootLogger().info("found images from URLs: " + _images.size());   
        
        
      //finding images if the already fetched images are less than MAX_IMAGES
        if (dyscoIds != null && _images.size() < size) {
        	int remaining = size - _images.size();
        	 Logger.getRootLogger().info("remaining Images to search: " + remaining);
            List<String> feedKeywords = dyscoRequestDAO.readKeywordsFromDyscos(dyscoIds);
            if (feedKeywords != null) {
                if (feedKeywords.size() > 0) {
                	imagesFromSearch = solrMediaItemHandler.findAllMediaItemsByKeywords(feedKeywords, "image", remaining);
                }
            }
            
            if(imagesFromSearch != null){
            	Logger.getRootLogger().info("found images from search: " + imagesFromSearch.size());

                for (MediaItem image : imagesFromSearch) {
                	MediaItem temp = mediaItemDAO.getMediaItem(image.getId());
                	if(temp != null ) {
                		if(temp.getWidth()==null || temp.getWidth()>150) {
                			image.setSource(1);
                			_images.add(image);
                		}
                	}
                }
            }
            
            //_images.addAll(imagesFromSearch);
        }
        return _images;
    }

    public static void main(String[] args) {
        DyscoDAO dyscoDAO = new DyscoDAOImpl();

    	List<String> dyscoIds = new ArrayList<String>();
    	dyscoIds.add("a3366aba-03cd-4ec7-abb5-0f2476c4c423");
    	List<MediaItem> imagesFromSearch = null;
    	List<String> feedKeywords = dyscoRequestDAO.readKeywordsFromDyscos(dyscoIds);
		 if (feedKeywords != null) {
	         if (feedKeywords.size() > 0) {
	         	imagesFromSearch = solrMediaItemHandler.findAllMediaItemsByKeywords(feedKeywords, "image", 100);
	         }
	     }
		 
		 if(imagesFromSearch != null)
			 Logger.getRootLogger().info("found images from search: " + imagesFromSearch.size());
         
        
    }
}

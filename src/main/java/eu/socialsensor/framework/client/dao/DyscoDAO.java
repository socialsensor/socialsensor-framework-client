package eu.socialsensor.framework.client.dao;

import eu.socialsensor.framework.client.search.Query;
import eu.socialsensor.framework.client.search.SearchEngineResponse;
import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.MediaItem;
import eu.socialsensor.framework.common.domain.dysco.Dysco;
import eu.socialsensor.framework.common.domain.dimension.Dimension;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.solr.client.solrj.SolrQuery.ORDER;

/**
 * Data Access Object for Dysco
 *
 * @author etzoannos
 */
public interface DyscoDAO {

    /**
     *
     * @param dysco The DySCO object to store
     * @return Returns the result of the storing procedure
     */
    public boolean insertDysco(Dysco dysco);

    /**
     *
     * @param dysco The DySCO object to edit
     * @return Returns the result of the editing procedure
     */
    public boolean updateDysco(Dysco dysco);

    /**
     *
     * @param dysco The DySCO object to destroy
     * @return Returns the result of the deletion
     */
    public boolean destroyDysco(String id);

    /**
     *
     * @param id The unique identifier of a DySCO Object
     * @return Returns the corresponding DySCO with all its Items
     */
    public Dysco findDysco(String id);

    /**
     *
     * @param id The unique identifier of a DySCO Object
     * @return Returns the corresponding lightweight DySCO (without Items)
     */
    public Dysco findDyscoLight(String id);

    /**
     *
     * @param id The unique identifier of a DySCO Object
     * @return Returns the corresponding DySCO Items
     */
    public List<Item> findDyscoItems(String id);

    /**
     *
     * @param title The title of the DySCO to search
     * @return Returns a list of DySCO objects that match the query
     */
    public List<Dysco> findDyscoByTitle(String title);

    /**
     *
     * @param item An item to use as search parameter
     * @return Returns a list of DySCO objects containing the given item
     */
    public List<Dysco> findDyscoByContainingItem(Item item);

    /**
     *
     * @param dim The dimension to use as search parameter
     * @return Returns a list of DySCO objects matching the parameter
     */
    public List<Dysco> findDyscoByDimension(Dimension dim);

    /**
     *
     * @return Returns A list of related DySCOs based on their community content
     */
    public List<Dysco> findCommunityRelatedDyscos(Dysco queryDysco);

    /**
     *
     * @return A list of related DySCOs based on their content
     */
    public List<Dysco> findContentRelatedDyscos(Dysco queryDysco);

    /**
     * @param count the number of Items to return
     * @return A list of the latest Items
     */
    public SearchEngineResponse<Item> findLatestItems(int count);

    SearchEngineResponse<Dysco> findDyscosLight(Query query);

    SearchEngineResponse<Item> findItems(Query query);

    public boolean updateDyscoWithoutItems(Dysco dysco);

    List<Item> findSortedDyscoItems(String id, String fieldToSort, ORDER order,
            int rows, boolean original);

    SearchEngineResponse findNDyscoItems(String id, int size);

    SearchEngineResponse findNDyscoItems(String id, int size, boolean original);

    public List<Item> findSortedDyscoItemsByQuery(Query query, String fieldToSort,
            ORDER order, int rows, boolean original);

	List<Item> findTotalItems(String _query);

	List<Item> findTotalItems(List<String> dyscoIdsOfGroup);

	List<String> findTotalUrls(List<Item> totalItems);

	List<Dysco> findRelatedTopics(Dysco dysco);
	
	//The following regard multimedia content retrieval
	
	/**
	 * Retrieve multimedia content that are videos based on dysco's 
	 * information
	 * @param dysco
	 * @param size
	 * @return
	 */
	List<MediaItem> findVideos(Dysco dysco, int size);
	/**
	 * Retrieve multimedia content that are videos based on a solr
	 * query
	 * @param query
	 * @param size
	 * @return
	 */
	List<MediaItem> findVideos(String query, int size);
	/**
	 * Retrieve multimedia content that corresponds to videos 
	 * prioritized by network based on a solr query 
	 * @param query
	 * @param networkPriorities
	 * @param size
	 * @return
	 */
	List<MediaItem> findVideos(String query,Map<String,Integer> networkPriorities,int size);
	/**
	 * Retrieve multimedia content that corresponds to videos 
	 * prioritized by network based on dysco's information
	 * @param dysco
	 * @param networkPriorities
	 * @param size
	 * @return
	 */
	List<MediaItem> findVideos(Dysco dysco,Map<String,Integer> networkPriorities,int size);
	/**
	 * Retrieve multimedia content tha is images based on dysco's
	 * information
	 * @param dysco
	 * @param size
	 * @return
	 */
	List<MediaItem> findImages(Dysco dysco, int size);
	/**
	 * Retrieve multimedia content that is images based on a solr 
	 * query
	 * @param query
	 * @param size
	 * @return
	 */
	List<MediaItem> findImages(String query, int size);
	/**
	 * Retrieve multimedia content that corresponds to images 
	 * prioritized by network based on a solr query 
	 * @param query
	 * @param networkPriorities
	 * @param size
	 * @return
	 */
	List<MediaItem> findImages(String query,Map<String,Integer> networkPriorities, int size);
	/**
	 * Retrieve multimedia content that corresponds to images 
	 * prioritized by network based on dysco's information
	 * @param dysco
	 * @param networkPriorities
	 * @param size
	 * @return
	 */
	List<MediaItem> findImages(Dysco dysco,Map<String,Integer> networkPriorities, int size);
}

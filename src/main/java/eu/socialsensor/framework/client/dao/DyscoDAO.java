package eu.socialsensor.framework.client.dao;

import eu.socialsensor.framework.client.search.Query;
import eu.socialsensor.framework.client.search.SearchEngineResponse;
import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.MediaItem;
import eu.socialsensor.framework.common.domain.dysco.Dysco;
import eu.socialsensor.framework.common.domain.dimension.Dimension;

import java.util.List;

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

	List<MediaItem> findVideos(List<String> dyscoIds, List<String> urls, int size);

	List<MediaItem> findImages(Dysco _dysco, int size);

	List<MediaItem> findImages(List<String> dyscoIds, List<String> urls, int size);
    
}
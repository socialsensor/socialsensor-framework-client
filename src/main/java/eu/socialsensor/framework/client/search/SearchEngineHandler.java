package eu.socialsensor.framework.client.search;

import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.dysco.Dysco;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author etzoannos
 */
public interface SearchEngineHandler {

    // methods for Items
    public boolean insertItem(Item item, String dyscoId);

    public boolean insertItems(List<Item> items, String dyscoId);

    public SearchEngineResponse<Item> findItems(Query query);

    public SearchEngineResponse<Item> findAllDyscoItems(String dyscoId);

    public SearchEngineResponse<Item> addFilterAndSearchItems(Query query,
            String fq);

    public SearchEngineResponse<Item> removeFilterAndSearchItems(Query query,
            String fq);

    public boolean deleteItem(String itemId);

    public boolean insertDysco(Dysco dysco);

    public boolean deleteDysco(String dyscoId);

    public boolean updateDysco(Dysco dysco);

    public Dysco findDysco(String id);

    public Dysco findDyscoLight(String id);

    public SearchEngineResponse<Dysco> addFilterAndSearchDyscosLight(
            Query query, String fq);

    public SearchEngineResponse<Dysco> removeFilterAndSearchDyscosLight(
            Query query, String fq);

    public SearchEngineResponse<Item> findLatestItems(int count);

    public SearchEngineResponse<Dysco> findDyscosLight(String query,
            String timeframe, int count);

    public SearchEngineResponse<Item> findMostRetweetedItemsLastHour();

    public boolean updateDyscoWithoutItems(Dysco dysco);

    SearchEngineResponse<Item> findSortedItems(String dyscoId, String fieldToSort,
            ORDER order, int rows, boolean original);

    public SearchEngineResponse<Item> findSortedItems(Query query,
            String fieldToSort, ORDER order, int rows, boolean original);

    SearchEngineResponse<Item> findNDyscoItems(String dyscoId, int size);

    SearchEngineResponse<Item> findNDyscoItems(String dyscoId, int size, boolean original);

    /**
     * 
     * @param size result size
     * @param filterQuery filter query
     * @return 
     */
    SearchEngineResponse<Item> findNMostRetweetedItems(int size, String filterQuery);

    SearchEngineResponse<Item> findNItems(Query query, int size);
    
    long findItemsLastHourSize();
}

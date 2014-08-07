package eu.socialsensor.framework.client.search.solr;

import eu.socialsensor.framework.client.search.Query;
import eu.socialsensor.framework.client.search.SearchEngineHandler;
import eu.socialsensor.framework.client.search.SearchEngineResponse;
import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.dysco.Dysco;
import eu.socialsensor.framework.common.domain.dysco.Dysco.DyscoType;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author etzoannos
 */
public class SolrHandler implements SearchEngineHandler {

    private final static Logger LOGGER = Logger.getLogger(SolrHandler.class.getName());

    private final SolrDyscoHandler solrDyscoHandler;
    private final SolrItemHandler solrItemHandler;

    public SolrHandler(String dyscosCollection, String itemsCollection) throws Exception {
        solrDyscoHandler = SolrDyscoHandler.getInstance(dyscosCollection);
        solrItemHandler = SolrItemHandler.getInstance(itemsCollection);
    }

    @Override
    public boolean insertItem(Item item, String dyscoId) {

        return solrItemHandler.insertItem(item);
    }

    @Override
    public boolean insertItems(List<Item> items, String dyscoId) {

        return solrItemHandler.insertItems(items);
    }

    @Override
    public SearchEngineResponse<Item> addFilterAndSearchItems(Query query,
            String fq) {

        return solrItemHandler.addFilterAndSearchItems(query, fq);
    }

    @Override
    public SearchEngineResponse<Item> removeFilterAndSearchItems(Query query,
            String fq) {

        return solrItemHandler.removeFilterAndSearchItems(query, fq);
    }

    @Override
    public boolean deleteItem(String itemId) {

        return solrItemHandler.deleteItem(itemId);
    }

    @Override
    public boolean updateDyscoWithoutItems(Dysco dysco) {

        LOGGER.log(Level.INFO, "updating Dysco: {0}", dysco.getId());
        return solrDyscoHandler.insertDysco(dysco);
    }

    @Override
    public SearchEngineResponse<Item> findItems(Query query) {

        SolrQuery solrQuery = new SolrQuery(query.getQueryString());
        solrQuery.setRows(1000);
        solrQuery.addSortField("publicationTime", ORDER.desc);

        return solrItemHandler.findItems(solrQuery);
    }

    @Override
    public SearchEngineResponse<Item> findNItems(Query query, int size) {

        SolrQuery solrQuery = new SolrQuery(query.getQueryString());
        solrQuery.setRows(size);
        solrQuery.addSortField("publicationTime", ORDER.desc);

        return solrItemHandler.findItems(solrQuery);
    }

    @Override
    public SearchEngineResponse<Item> findLatestItems(int count) {
        SolrQuery query = new SolrQuery("*:*");
        query.setRows(count);
        query.addSortField("publicationTime", ORDER.desc);
        return solrItemHandler.findItems(query);
    }

    @Override
    public SearchEngineResponse<Item> findMostRetweetedItemsLastHour() {
        SolrQuery query = new SolrQuery("*:*");
        // query.addFilterQuery("publicationTime:[NOW-1HOUR TO NOW]");
        query.setRows(50);
        query.addSortField("publicationTime", ORDER.desc);
        LOGGER.log(Level.INFO, null, query.toString());

        return solrItemHandler.findItems(query);
    }

    /**
     *
     * @param size result size
     * @param filterQuery filter query
     * @return
     */
    @Override
    public SearchEngineResponse<Item> findNMostRetweetedItems(int size, String filterQuery) {
        SolrQuery query = new SolrQuery("*:*");
        if ((filterQuery != null) && (!"".equals(filterQuery))) {
            query.addFilterQuery(filterQuery);
        }

        query.setRows(size);
        query.addSortField("retweetsCount", ORDER.desc);

        return solrItemHandler.findItems(query);
    }

    public SearchEngineResponse<Item> findNMostRetweetedVerifiedItems(int size, String filterQuery) {
        SolrQuery query = new SolrQuery("category:official");
        if ((filterQuery != null) && (!"".equals(filterQuery))) {
            query.addFilterQuery(filterQuery);
        }

        query.setRows(size);
        query.addSortField("retweetsCount", ORDER.desc);

        return solrItemHandler.findItems(query);
    }

    @Override
    public long findItemsLastHourSize() {

        SolrQuery query = new SolrQuery("*:*");
        query.addSortField("publicationTime", ORDER.desc);
        query.setRows(0);
        LOGGER.log(Level.INFO, null, query.toString());
        try {
            return solrItemHandler.server.query(query).getResults().getNumFound();
        } catch (SolrServerException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public SearchEngineResponse<Item> findSortedItems(String dyscoId,
            String fieldToSort, ORDER order, int rows, boolean original) {
        SolrQuery solrQuery;
        if (original) {
            solrQuery = new SolrQuery("dyscoId:" + dyscoId + " AND original:true");

        } else {
            solrQuery = new SolrQuery("dyscoId:" + dyscoId);
        }
        solrQuery.setRows(rows);
        solrQuery.addSortField(fieldToSort, order);

        return solrItemHandler.findItems(solrQuery);
    }

    @Override
    public SearchEngineResponse<Item> findSortedItems(Query query,
            String fieldToSort, ORDER order, int rows, boolean original) {
        SolrQuery solrQuery;
        if (original) {
            solrQuery = new SolrQuery(query.toString() + " AND original:true");

        } else {
            solrQuery = new SolrQuery(query.toString());
        }
        solrQuery.setRows(rows);
        solrQuery.addSortField(fieldToSort, order);

        return solrItemHandler.findItems(solrQuery);
    }

    @Override
    public SearchEngineResponse<Item> findAllDyscoItems(String dyscoId) {

        return solrItemHandler.findAllDyscoItems(dyscoId);
    }

    @Override
    public SearchEngineResponse<Item> findNDyscoItems(String dyscoId, int size) {

        return solrItemHandler.findNDyscoItems(dyscoId, size);
    }

    @Override
    public boolean insertDysco(Dysco dysco) {

        // insert items to Solr Items collection
        boolean status2 = false;
        LOGGER.log(Level.INFO, "inserting {0} dysco items to Solr", dysco.getItems().size());

        if (dysco.getDyscoType().equals(DyscoType.CUSTOM)) {
            status2 = true;
        }
        if (dysco.getItems().size() > 0) {
            status2 = solrItemHandler.insertItems(dysco.getItems());
            LOGGER.log(Level.INFO, "status {0}", status2);
        }

        boolean status1 = false;

        // insert Dysco to Solr Dysco collection
        if (status2) {
            LOGGER.log(Level.INFO, "Inserting dysco to Solr {0}", dysco.getId());
            status1 = solrDyscoHandler.insertDysco(dysco);
            LOGGER.log(Level.INFO, "status {0}", status1);
        }

        return (status1);
    }

    @Override
    public Dysco findDysco(String id) {

        Dysco dysco = solrDyscoHandler.findDyscoLight(id);
        return dysco;
    }

    @Override
    public SearchEngineResponse<Dysco> findDyscosLight(String query, String timeframe, int count) {

        query = query + " AND (creationDate:[NOW-" + timeframe + " TO NOW])";

        SolrQuery solrQuery = new SolrQuery(query);
//        solrQuery.addFilterQuery("creationDate:[NOW-" + timeframe + " TO NOW]");

        solrQuery.setRows(count);
        solrQuery.setFacet(true);
        solrQuery.setFacetLimit(5);
        solrQuery.addSortField("dyscoScore", SolrQuery.ORDER.desc);
        solrQuery.addFacetField("persons");
        solrQuery.addFacetField("organizations");
        return solrDyscoHandler.findDyscosLight(solrQuery);
    }

    public SearchEngineResponse<Dysco> findDyscosLight(String query, String timeframe, String listId, int count) {

        query = query + " AND (creationDate:[NOW-" + timeframe + " TO NOW])";

        if (!listId.equals("*")) {
            query = query + " AND (listId:" + listId + ")";
        }
        SolrQuery solrQuery = new SolrQuery(query);

        System.out.println("solrQuery for Dysco: " + solrQuery);

        solrQuery.setRows(count);
        solrQuery.setFacet(true);
        solrQuery.setFacetLimit(5);
        solrQuery.addSortField("rankerScore", SolrQuery.ORDER.desc);
        solrQuery.addFacetField("persons");
        solrQuery.addFacetField("organizations");
        return solrDyscoHandler.findDyscosLight(solrQuery);
    }

    public SearchEngineResponse<Dysco> findDyscosLight(String query, String timeframe, String listId, int count, double a) {

        query = query + " AND (creationDate:[NOW-" + timeframe + " TO NOW])";

        if (!listId.equals("*")) {
            query = query + " AND (listId:" + listId + ")";
        }

        SolrQuery solrQuery = new SolrQuery(query);

        System.out.println("solrQuery for Dysco: " + solrQuery);

        solrQuery.setRows(count);
        solrQuery.setFacet(true);
        solrQuery.setFacetLimit(5);

        String sortBy = "sum(product(normalizedDyscoScore," + a + "),product(normalizedRankerScore," + (1 - a) + "))";
        System.out.println("SortBy: " + sortBy);
        solrQuery.addSortField(sortBy, ORDER.desc);

        solrQuery.addFacetField("persons");
        solrQuery.addFacetField("organizations");
        return solrDyscoHandler.findDyscosLight(solrQuery);
    }

    @Override
    public Dysco findDyscoLight(String id) {
        Dysco dysco = solrDyscoHandler.findDyscoLight(id);
        return dysco;
    }

    @Override
    public boolean deleteDysco(String dyscoId) {

        solrDyscoHandler.removeDysco(dyscoId);
//        Query query = new Query("dyscoId:" + dyscoId);
        return true;

    }

    @Override
    public boolean updateDysco(Dysco dysco) {

        boolean status;
        // first delete all items referring to this Dysco from the items
        // collection
        status = deleteDysco(dysco.getId());
        if (status) {
            // modify the Dysco collection
            solrDyscoHandler.insertDysco(dysco);
            // modify the items collection
            status = insertDysco(dysco);
        }
        return status;
    }

    @Override
    public SearchEngineResponse<Dysco> addFilterAndSearchDyscosLight(
            Query query, String fq) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SearchEngineResponse<Dysco> removeFilterAndSearchDyscosLight(
            Query query, String fq) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void main(String... args) {

    }

    @Override
    public SearchEngineResponse<Item> findNDyscoItems(String dyscoId, int size, boolean original) {
        return solrItemHandler.findNDyscoItems(dyscoId, size, original);
    }
}

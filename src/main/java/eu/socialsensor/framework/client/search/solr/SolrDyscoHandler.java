package eu.socialsensor.framework.client.search.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;

import com.google.gson.Gson;

import eu.socialsensor.framework.client.search.Bucket;
import eu.socialsensor.framework.client.search.Facet;
import eu.socialsensor.framework.client.search.SearchEngineResponse;
import eu.socialsensor.framework.common.domain.dysco.Dysco;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class SolrDyscoHandler {
	public final Logger logger = Logger.getLogger(SolrDyscoHandler.class);

    SolrServer server;
    private static final Map<String, SolrDyscoHandler> INSTANCES = new HashMap<String, SolrDyscoHandler>();

    // Private constructor prevents instantiation from other classes
    private SolrDyscoHandler(String collection) {
        try {

//                Logger.getRootLogger().info("going to create SolrServer: " + ConfigReader.getSolrHome() + "/dyscos");
        	server = new HttpSolrServer( collection );
           
        } catch (Exception e) {
        	
            Logger.getRootLogger().info(e.getMessage());
        }
    }
    //implementing Singleton pattern

    public static SolrDyscoHandler getInstance(String collection) {
    	SolrDyscoHandler INSTANCE = INSTANCES.get(collection);
    	if(INSTANCE == null) {
    		INSTANCE = new SolrDyscoHandler(collection);
    		INSTANCES.put(collection, INSTANCE);
    	}
        return INSTANCE;
    }

    public boolean insertDysco(Dysco dysco) {

        boolean status = false;
        try {
        	
            SolrDysco solrDysco = new SolrDysco(dysco);
            
            server.addBean(solrDysco);
           
            UpdateResponse response = server.commit();
            int statusId = response.getStatus();
            if (statusId == 0) {
                status = true;
            }

        } catch (SolrServerException ex) {
            Logger.getRootLogger().error(ex.getMessage());
        } catch (IOException ex) {
            Logger.getRootLogger().error(ex.getMessage());
        } catch (Exception ex) {
            Logger.getRootLogger().error(ex.getMessage());
        } finally {
            return status;
        }
    }

    public boolean removeDysco(String dyscoId) {

        boolean status = false;
        try {
        	
            server.deleteById(dyscoId);
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
    
     public SolrDysco findSolrDyscoLight(String dyscoId) {

        SolrQuery solrQuery = new SolrQuery("id:" + dyscoId);
        SearchEngineResponse<SolrDysco> response = findSolrDyscosLight(solrQuery);
       
        List<SolrDysco> dyscos = response.getResults();
        SolrDysco dysco = null;
        if (dyscos != null) {
            if (dyscos.size() > 0) {
                dysco = dyscos.get(0);
            }
        }
        return dysco;
    }

    public Dysco findDyscoLight(String dyscoId) {

        SolrQuery solrQuery = new SolrQuery("id:" + dyscoId);
        SearchEngineResponse<Dysco> response = findDyscosLight(solrQuery);
       
        List<Dysco> dyscos = response.getResults();
        Dysco dysco = null;
        if (dyscos != null) {
            if (dyscos.size() > 0) {
                dysco = dyscos.get(0);
            }
        }
        return dysco;
    }
    
    public String findDyscosTrendline(String entity, String entityValue) {


        System.out.println("finding dyscos trendline");
        SolrQuery query = new SolrQuery(entity + ":" + entityValue);
        query.setFields("creationDate", "id");

        query.addSortField("creationDate", SolrQuery.ORDER.asc);

        QueryResponse rsp;
        System.out.println("searching: " + query.toString());
        try {
            rsp = server.query(query);
        } catch (SolrServerException e) {
            Logger.getRootLogger().info(e.getMessage());
            return null;
        }

        List<SolrDysco> solrDyscos = rsp.getBeans(SolrDysco.class);
        if (solrDyscos != null) {
            Logger.getRootLogger().info("got: " + solrDyscos.size() + " dyscos from Solr");
        }

        List<Dysco> dyscos = new ArrayList<Dysco>();
        for (SolrDysco dysco : solrDyscos) {
            dyscos.add(dysco.toDysco());
        }

        Gson gson = new Gson();
        ArrayList<TrendlineSpot> spots = new ArrayList<TrendlineSpot>();
        for (Dysco dysco : dyscos) {
            spots.add(new TrendlineSpot(dysco.getCreationDate().getTime(), 30));
        }
        return gson.toJson(spots);

    }
    
    public SearchEngineResponse<SolrDysco> findSolrDyscosLight(SolrQuery query) {
        
        SearchEngineResponse<SolrDysco> response = new SearchEngineResponse<SolrDysco>();

        QueryResponse rsp;
        //System.out.println("searching: " + query.toString());
        try {
            rsp = server.query(query);
        } catch (SolrServerException e) {
            Logger.getRootLogger().info(e.getMessage());
            return null;
        }

        List<SolrDysco> resultList = rsp.getBeans(SolrDysco.class);
        if (resultList != null) {
           // Logger.getRootLogger().info("got: " + resultList.size() + " dyscos from Solr");
        }

        List<SolrDysco> dyscos = new ArrayList<SolrDysco>();
        for (SolrDysco dysco : resultList) {
            dyscos.add(dysco);
        }

        response.setResults(dyscos);
        
        return response;
    }
    

    public SearchEngineResponse<Dysco> findDyscosLight(SolrQuery query) {
        
        SearchEngineResponse<Dysco> response = new SearchEngineResponse<Dysco>();

        QueryResponse rsp;
        //System.out.println("searching: " + query.toString());
        try {
            rsp = server.query(query);
        } catch (SolrServerException e) {
            Logger.getRootLogger().info(e.getMessage());
            return null;
        }

        List<SolrDysco> resultList = rsp.getBeans(SolrDysco.class);
        if (resultList != null) {
           // Logger.getRootLogger().info("got: " + resultList.size() + " dyscos from Solr");
        }

        List<Dysco> dyscos = new ArrayList<Dysco>();
        for (SolrDysco dysco : resultList) {
            dyscos.add(dysco.toDysco());
        }

        response.setResults(dyscos);
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
                    //if ((bucketCount > 0) && (bucketCount != dyscos.size())) { //bucket is neither non-zero length nor the whole set 
                    if (bucketCount > 0) { //bucket is non-zero length 
                        validFacet = true; //facet contains at least one non-zero length bucket
                        bucket.setCount(bucketCount);
                        bucket.setName(values.get(j).getName());
                        bucket.setQuery(values.get(j).getAsFilterQuery());
                        bucket.setFacet(solrFacetName);
                        buckets.add(bucket);
                    }
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

    public static void main(String... args) {

    }
}

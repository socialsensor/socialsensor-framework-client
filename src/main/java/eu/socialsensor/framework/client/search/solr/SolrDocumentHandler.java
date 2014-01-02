package eu.socialsensor.framework.client.search.solr;

import java.io.IOException;
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

import eu.socialsensor.framework.client.search.Query;
import eu.socialsensor.framework.client.search.SearchEngineResponse;
import eu.socialsensor.framework.common.domain.Document;


public class SolrDocumentHandler {
	private Logger logger = Logger.getLogger(SolrDocumentHandler.class);
	
	SolrServer server;
    private static Map<String, SolrDocumentHandler> INSTANCES = new HashMap<String, SolrDocumentHandler>();
    private static int commitPeriod = 1000;
    
    private SolrDocumentHandler(String collection) {
        try {
//            ConfigReader configReader = new ConfigReader();
//            String url = configReader.getSolrHTTP();    
            server = new HttpSolrServer(collection);

            //Logger.getRootLogger().info("going to create SolrServer: " + ConfigReader.getSolrHome() + "/DyscoMediaItems");
            //server = new HttpSolrServer( ConfigReader.getSolrHome() + "/DyscoMediaItems");

        } catch (Exception e) {
            Logger.getRootLogger().info(e.getMessage());
        }
    }
    
    public static SolrDocumentHandler getInstance(String collection) {
    	SolrDocumentHandler INSTANCE = INSTANCES.get(collection);
        if (INSTANCE == null) {
            INSTANCE = new SolrDocumentHandler(collection);
            INSTANCES.put(collection, INSTANCE);
        }
        return INSTANCE;
    }
    
    public boolean insertDocument(Document document) {

        boolean status = true;
        try {
            SolrDocument solrDocument = new SolrDocument(document);

            server.addBean(solrDocument, commitPeriod);
            //UpdateResponse response = server.commit();
            //int statusId = response.getStatus();
            //if (statusId == 0) {
            //    status = true;
            //}

        } catch (SolrServerException ex) {
            logger.error(ex.getMessage());
            status = false;
        } catch (Exception ex) {
            ex.printStackTrace();
            status = false;
        } finally {
            return status;
        }
    }
    
    public boolean insertDocuments(List<Document> documents) {

        boolean status = true;
        try {
            List<SolrDocument> solrDocuments = new ArrayList<SolrDocument>();
            for (Document document : documents) {
            	SolrDocument solrDocument = new SolrDocument(document);
                solrDocuments.add(solrDocument);
            }

            server.addBeans(solrDocuments, commitPeriod);

//            UpdateResponse response = server.commit();
//            int statusId = response.getStatus();
//            if (statusId == 0) {
//                status = true;
//            }

        } catch (SolrServerException ex) {
            logger.error(ex.getMessage());
            status = false;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            status = false;
        } finally {
            return status;
        }

    }
    
    public void forceCommitPending() {

        try {

            server.commit();
        } catch (SolrServerException ex) {
            logger.error(ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    public SearchEngineResponse<Document> addFilterAndSearchDocuments(Query query, String fq) {

        SolrQuery solrQuery = new SolrQuery(query.getQueryString());
        solrQuery.addFilterQuery(fq);
        return search(solrQuery);
    }

    public boolean deleteDocument(String id) {
        boolean status = false;
        try {
            server.deleteByQuery("id:" + id);
            UpdateResponse response = server.commit();
            int statusId = response.getStatus();
            if (statusId == 0) {
                status = true;
            }

        } catch (SolrServerException ex) {
            logger.error(ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            return status;
        }
    }

    public boolean deleteDocuments(Query query) {
        boolean status = false;
        try {
            server.deleteByQuery(query.getQueryString());
            UpdateResponse response = server.commit();
            int statusId = response.getStatus();
            if (statusId == 0) {
                status = true;
            }

        } catch (SolrServerException ex) {
            logger.error(ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            return status;
        }
    }
    
    
    public SearchEngineResponse<Document> findDocuments(SolrQuery query) {

        return search(query);
    }
    
    public Document findItem(String id) {

        SolrQuery solrQuery = new SolrQuery("id:\"" + id + "\"");
        solrQuery.setRows(1);
        SearchEngineResponse<Document> response = search(solrQuery);
        List<Document> documents = response.getResults();
        if (!documents.isEmpty()) {
            return documents.get(0);
        } else {
        	logger.error("No document with this id in Storage");
            return null;
        }
    }
    
    private SearchEngineResponse<Document> search(SolrQuery query) {

        SearchEngineResponse<Document> response = new SearchEngineResponse<Document>();
        QueryResponse rsp;

        try {
            rsp = server.query(query);
        } catch (SolrServerException e) {
            e.printStackTrace();
            Logger.getRootLogger().info(e.getMessage());
            return null;
        }


        List<SolrDocument> solrDocuments = rsp.getBeans(SolrDocument.class);


        List<Document> documents = new ArrayList<Document>();
        for (SolrDocument solrDocument : solrDocuments) {
            Document document = solrDocument.toDocument();
			String id = document.getId();
         
			documents.add(document);
        }

        response.setResults(documents);

        return response;
    }

    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

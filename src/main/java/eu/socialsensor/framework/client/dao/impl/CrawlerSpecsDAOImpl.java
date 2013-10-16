package eu.socialsensor.framework.client.dao.impl;

import eu.socialsensor.framework.client.dao.CrawlerSpecsDAO;
import eu.socialsensor.framework.client.dao.KeywordDAO;
import eu.socialsensor.framework.client.dao.LocationDAO;
import eu.socialsensor.framework.client.dao.SourceDAO;
import eu.socialsensor.framework.client.search.SearchEngineHandler;
import eu.socialsensor.framework.client.search.SearchEngineResponse;
import eu.socialsensor.framework.client.search.solr.SolrHandler;
import eu.socialsensor.framework.common.domain.Keyword;
import eu.socialsensor.framework.common.domain.Location;
import eu.socialsensor.framework.common.domain.Source;
import eu.socialsensor.framework.common.domain.dysco.Dysco;

import java.util.ArrayList;
import java.util.List;

/**
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class CrawlerSpecsDAOImpl implements CrawlerSpecsDAO {
	
	
	public static void main(String[] args) {
		String host = null;
		String db = "testDB";
		
		CrawlerSpecsDAO dao = new CrawlerSpecsDAOImpl(host, db, "keywords", "Feeds", "locations");
		
		List<Keyword> keywords = new ArrayList<Keyword>();
		
		keywords.add(new Keyword("champions league", 0.79f));
		dao.setKeywords(keywords, Source.Type.Twitter);
		
	}
	
	KeywordDAO keywordDAO;
	LocationDAO locationDAO;
	SourceDAO sourceDAO;

	public CrawlerSpecsDAOImpl(String host, String db, 
			String keywords, String users, String locations) {
		keywordDAO = new KeywordDAOImpl(host, db, keywords);
		locationDAO = new LocationDAOImpl(host, db, locations);
		sourceDAO = new SourceDAOImpl(host, db, users);
	}
	
	public CrawlerSpecsDAOImpl(String host, String db) {
		this(host, db, "keywords", "users", "locations");
	}
	
	public CrawlerSpecsDAOImpl() {
		this("", "CrawlerSpecs", "keywords", "users", "locations");
	}
	
    @Override
    public List<Keyword> getTopKeywords(int count, Source.Type sourceType) {
    	return keywordDAO.findTopKeywords(count);
    }
    
    @Override
    public List<Source> getTopSources(int count) {
    	return sourceDAO.findTopSources(count);
    }
    
    @Override
    public List<Source> getTopSources(int count, Source.Type sourceType) {
    	return sourceDAO.findTopSources(count);
    }

    @Override
    public List<Dysco> getTopDyscos(int count) {
        SearchEngineHandler solrHandler = new SolrHandler();
        SearchEngineResponse<Dysco> searchResponse =  solrHandler.findDyscosLight("*:*", "1DAY", 10);
        List<Dysco> dyscosLight = searchResponse.getResults();
        return dyscosLight;
    }
    
    

    @Override
    public List<Source.Type> getSources() {
        return null;
    }

	@Override
	public void setKeywords(List<Keyword> keywords, Source.Type sourceType) {
		for(Keyword keyword : keywords) {
			keywordDAO.insertKeyword(keyword.getName(), keyword.getScore(), sourceType);
		}
		
	}

	@Override
	public void removeKeywords(List<Keyword> keywords, Source.Type sourceType) {
		for(Keyword keyword : keywords) {
			keywordDAO.removeKeyword(keyword.getName(), sourceType);
		}
	}
	
	@Override
	public void setSources(List<Source> sources, Source.Type sourceType) {
		for(Source source : sources) {
			sourceDAO.insertSource(source, sourceType);
		}
	}

	@Override
	public void removeSources(List<Source> sources, Source.Type sourceType) {
		for(Source source : sources) {
			sourceDAO.removeSource(source, sourceType);
		}
	}
	
	@Override
	public void setLocations(List<Location> locations, Source.Type sourceType) {
		for(Location location : locations) {
			locationDAO.insertLocation(location, sourceType);
		}
	}

	@Override
	public void removeLocations(List<Location> locations, Source.Type sourceType) {
		for(Location location : locations) {
			locationDAO.removeLocation(location,  sourceType);
		}
	}
	
}

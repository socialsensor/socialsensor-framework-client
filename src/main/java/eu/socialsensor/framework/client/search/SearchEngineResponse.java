
package eu.socialsensor.framework.client.search;

import eu.socialsensor.framework.client.search.solr.TrendlineSpot;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */

public class SearchEngineResponse<T> {
    
    private List<T> results = new ArrayList<T>();
    private List<Facet> facets = new ArrayList<Facet>();
    private List<TrendlineSpot> spots = new ArrayList<TrendlineSpot>();
    private long numFound = 0;
    
    public SearchEngineResponse(){
    	
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public List<Facet> getFacets() {
        return facets;
    }

    public void setFacets(List<Facet> facets) {
        this.facets = facets;
    }

    public long getNumFound() {
        return numFound;
    }

    public void setNumFound(long numFound) {
        this.numFound = numFound;
    }

    public List<TrendlineSpot> getSpots() {
        return spots;
    }

    public void setSpots(List<TrendlineSpot> spots) {
        this.spots = spots;
    }
    
    

}

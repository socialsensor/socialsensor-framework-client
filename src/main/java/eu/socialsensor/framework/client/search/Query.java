package eu.socialsensor.framework.client.search;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class Query {

    public Query() {
    }
    
    public Query(String query) {
        queryString = query;
    }
    
    private String queryString = "";

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
    
    @Override
    public String toString() {
        return queryString;
    }
}

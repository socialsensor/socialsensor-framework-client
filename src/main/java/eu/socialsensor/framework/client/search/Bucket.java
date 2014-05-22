package eu.socialsensor.framework.client.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 *
 * @author etzoannos
 */

public class Bucket implements Serializable{
    @Expose
    @SerializedName(value = "name")
    String name;
    @Expose
    @SerializedName(value = "count")
    long count;
    @Expose
    @SerializedName(value = "query")
    String query;
    @Expose
    @SerializedName(value = "facet")
    String facet;

    public Bucket() {
    }
        
    public Bucket(String name, long count, String query, String facet) {
        this.name = name;
        this.count = count;
        this.query = query;
        this.facet = facet;
    }

    public String getFacet() {
        return facet;
    }

    public void setFacet(String facet) {
        this.facet = facet;
    }
    
    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    
    
}
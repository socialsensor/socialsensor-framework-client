
package eu.socialsensor.framework.client.search;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */

public class Facet {

    String name;
    List<Bucket> buckets = new ArrayList<Bucket>();

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<Bucket> buckets) {
        this.buckets = buckets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    

}

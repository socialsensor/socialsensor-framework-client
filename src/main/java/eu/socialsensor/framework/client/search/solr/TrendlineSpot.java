
package eu.socialsensor.framework.client.search.solr;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */

public class TrendlineSpot {
    
    long x;
    int y;

    public TrendlineSpot() {
        
    }    
    
    public TrendlineSpot(long x, int y) {
        this.x = x;
        this.y = y;
    }
    
    

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public void addOne() {
        
        y++;
//        System.out.println("adding one: new y=" + this.y);
    }
    
}

package eu.socialsensor.framework.client.dao;

import eu.socialsensor.framework.common.domain.Location;
import eu.socialsensor.framework.common.domain.Source;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public interface LocationDAO {

    public void insertLocation(String name, double latitude, double longitude);
    public void insertLocation(Location location, Source.Type sourceType);
    
    public void removeLocation(Location keyword, Source.Type sourceType);
    public void removeLocation(Location keyword);
}


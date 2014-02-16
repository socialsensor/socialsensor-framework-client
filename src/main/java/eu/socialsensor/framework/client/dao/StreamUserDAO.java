package eu.socialsensor.framework.client.dao;

import java.util.List;
import java.util.Map;

import eu.socialsensor.framework.common.domain.StreamUser;

/**
 * Data Access Object for Item
 *
 * @author etzoannos
 */
public interface StreamUserDAO {

    public void insertStreamUser(StreamUser user);

    public void updateStreamUser(StreamUser user);

    public void updateStreamUserOld(StreamUser user);

    public void updateStreamUserPopularity(StreamUser user);

    public void incStreamUserValue(String id, String field);
    
    public boolean deleteStreamUser(String id);

    public StreamUser getStreamUser(String id);
    
    public Map<String, StreamUser> getStreamUsers(List<String> ids);

    public StreamUser getStreamUserByName(String username);
    
    public boolean exists(String id);
}

package eu.socialsensor.framework.client.dao;

import eu.socialsensor.framework.common.domain.StreamUser;
import eu.socialsensor.framework.common.domain.StreamUser.Category;

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

    public void updateStreamUserMentions(String id);
    
    public boolean deleteStreamUser(String id);

    public StreamUser getStreamUser(String id);

    public StreamUser getStreamUserByName(String username);
    
    public void loadExpertsList(String file, Category category);
    
    public boolean exists(String id);
}

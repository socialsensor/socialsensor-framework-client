package eu.socialsensor.framework.client.dao;

import eu.socialsensor.framework.common.profile.Profile;
import eu.socialsensor.framework.common.profile.ScoredItem;
import eu.socialsensor.framework.common.profile.User;
import java.util.List;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public interface UserRecommendationsDAO {

    public void addUserProfiles(User user, List<Profile> profiles);

    public List<Profile> getUserProfiles(User user);

    public void addUserRecommendations(User user, List<ScoredItem> items);

    public List<ScoredItem> getUserRecommendations(User user);
}

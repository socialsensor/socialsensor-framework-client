/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.socialsensor.framework.client.dao;

import eu.socialsensor.framework.common.influencers.Influencer;
import java.util.List;

/**
 *
 * @author etzoannos
 */
public interface InfluencerDAO {

    public void addInfluencersForKeyword(String keyword, List<Influencer> influencers);

    public List<Influencer> getInfluencersForKeyword(String keyword);

    public List<Influencer> getInfluencersForKeywords(List<String> keywords);
}

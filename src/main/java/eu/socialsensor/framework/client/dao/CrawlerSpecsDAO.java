package eu.socialsensor.framework.client.dao;

import eu.socialsensor.framework.common.domain.Keyword;
import eu.socialsensor.framework.common.domain.Location;
import eu.socialsensor.framework.common.domain.Source;
import eu.socialsensor.framework.common.domain.SocialNetworkSource;
import eu.socialsensor.framework.common.domain.dysco.Dysco;
import java.util.List;

/**
 *
 * @author etzoannos
 */
public interface CrawlerSpecsDAO {

    public List<SocialNetworkSource> getSources();

    public List<Keyword> getTopKeywords(int count, SocialNetworkSource sourceType);

    public List<Source> getTopSources(int count);
    
    public List<Source> getTopSources(int count, SocialNetworkSource sourceType);
    
    public List<Dysco> getTopDyscos(int count);
    
    public void setKeywords(List<Keyword> keywords, SocialNetworkSource sourceType);
    
    public void setSources(List<Source> sources, SocialNetworkSource sourceType);
    
    public void setLocations(List<Location> locations, SocialNetworkSource sourceType);
    
    public void removeKeywords(List<Keyword> keywords,SocialNetworkSource sourceType);
    
    public void removeSources(List<Source> sources, SocialNetworkSource sourceType);
    
    public void removeLocations(List<Location> locations, SocialNetworkSource sourceType);
}

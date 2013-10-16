package eu.socialsensor.framework.client.dao;

import eu.socialsensor.framework.common.domain.Keyword;
import eu.socialsensor.framework.common.domain.Location;
import eu.socialsensor.framework.common.domain.Source;
import eu.socialsensor.framework.common.domain.dysco.Dysco;
import java.util.List;

/**
 *
 * @author etzoannos
 */
public interface CrawlerSpecsDAO {

    public List<Source.Type> getSources();

    public List<Keyword> getTopKeywords(int count, Source.Type sourceType);

    public List<Source> getTopSources(int count);
    
    public List<Source> getTopSources(int count, Source.Type sourceType);
    
    public List<Dysco> getTopDyscos(int count);
    
    public void setKeywords(List<Keyword> keywords, Source.Type sourceType);
    
    public void setSources(List<Source> sources, Source.Type sourceType);
    
    public void setLocations(List<Location> locations, Source.Type sourceType);
    
    public void removeKeywords(List<Keyword> keywords, Source.Type sourceType);
    
    public void removeSources(List<Source> sources, Source.Type sourceType);
    
    public void removeLocations(List<Location> locations, Source.Type sourceType);
}

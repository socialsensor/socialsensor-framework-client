package eu.socialsensor.framework.client.dao;

import java.util.List;
import eu.socialsensor.framework.common.domain.Source;
import eu.socialsensor.framework.common.domain.Source.Type;

public interface SourceDAO {
	
	public void insertSource(String source, float score);

	public void insertSource(Source source);
	
    public void insertSource(String source, float score, Source.Type sourceType);
    
    public void insertSource(Source source,  Source.Type sourceType);
    
    public void removeSource(Source source);

    public void removeSource(Source source, Source.Type sourceType);
    
    public void instertDyscoSource(String dyscoId, String source, float score);

	public List<Source> findTopSources(int n);

	public List<Source> findTopSources(int n, Type sourceType);

}

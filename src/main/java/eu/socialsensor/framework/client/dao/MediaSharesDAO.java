package eu.socialsensor.framework.client.dao;

import java.util.List;

import eu.socialsensor.framework.common.domain.MediaShare;

public interface MediaSharesDAO {

	
	public void addMediaShare(String id, String originalId, long publicationTime, String userid);
	
	public List<MediaShare> getMediaShares(String mediaId);

}

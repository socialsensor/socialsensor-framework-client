package eu.socialsensor.framework.client.dao;

import eu.socialsensor.framework.common.domain.MediaCluster;

public interface MediaClusterDAO {

	
	public void addMediaCluster(MediaCluster mediaCluster);
	
	public MediaCluster getMediaCluster(String clusterId);
	
	public void addMediaItemInCluster(String clusterId, String memberId);

}

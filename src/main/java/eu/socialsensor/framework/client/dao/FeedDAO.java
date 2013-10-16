package eu.socialsensor.framework.client.dao;

import eu.socialsensor.framework.common.domain.Feed;

/**
 * Data Access Object for Feed
 *
 * @author ailiakop
 * @email ailiakop@iti.gr
 */
public interface FeedDAO {
	
	public void insertFeed(Feed feed);
	
	public boolean deleteFeed(Feed feed);
	
	public Feed getFeed(String id);
	
	public Feed getFeedByItem(String id);
}

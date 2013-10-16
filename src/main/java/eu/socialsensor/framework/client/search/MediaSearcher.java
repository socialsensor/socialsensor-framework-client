package eu.socialsensor.framework.client.search;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MediaSearcher {

	public static final String CHANNEL = "searchRequestsChannel";
	
	private Jedis publisherJedis;
	
	public MediaSearcher(String searcherHostname) {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool jedisPool = new JedisPool(poolConfig, searcherHostname, 6379, 0);
        
        this.publisherJedis = jedisPool.getResource();
        
	}
	
	public void search(String dyscoId) {
		publisherJedis.publish(CHANNEL, dyscoId);
	}
	
	public void delete(String dyscoId){
		publisherJedis.publish(CHANNEL, dyscoId);
	}

}

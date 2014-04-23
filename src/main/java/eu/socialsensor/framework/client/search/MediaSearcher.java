package eu.socialsensor.framework.client.search;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MediaSearcher {

	public final Logger logger = Logger.getLogger(MediaSearcher.class);
	public static final String CHANNEL = "searchRequestsChannel";
	
	private Jedis publisherJedis;
	
	public MediaSearcher(String searcherHostname) {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool jedisPool = new JedisPool(poolConfig, searcherHostname, 6379, 0);
        
        this.publisherJedis = jedisPool.getResource();
	}
	
	public void search(String message) {
		try {
			publisherJedis.publish(CHANNEL, message);
		}
		catch(Exception e) {
			logger.error(e);
		}
	}
	
	public void delete(String message) {
		try {
			publisherJedis.publish(CHANNEL, message);
		}
		catch(Exception e) {
			logger.error(e);
		}
	}

	public void search(String message, String channel) {
		try {
			publisherJedis.publish(channel, message);
		}
		catch(Exception e) {
			logger.error(e);
		}
	}
	
	public void delete(String message, String channel) {
		try {
			publisherJedis.publish(channel, message);
		}
		catch(Exception e) {
			logger.error(e);
		}
	}
	
}

package eu.socialsensor.framework.client.search;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MediaSearcher {

	public final Logger logger = Logger.getLogger(MediaSearcher.class);
	private String searcherHostname;
	public static final String CHANNEL = "searchRequestsChannel";
	
	
	public MediaSearcher(String searcherHostname) {
		this.searcherHostname = searcherHostname;
	}
	
	public void search(String message) {
		search(message, CHANNEL);
	}
	
	public void delete(String message) {
		delete(message, CHANNEL);
	}

	public void search(String message, String channel) {
		Jedis publisherJedis = null;
		try {
			JedisPoolConfig poolConfig = new JedisPoolConfig();
	        JedisPool jedisPool = new JedisPool(poolConfig, searcherHostname, 6379, 0);
	        
	        publisherJedis = jedisPool.getResource();
	        
			publisherJedis.publish(channel, message);
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		finally {
			if(publisherJedis != null)
				publisherJedis.disconnect();
		}
	}
	
	public void delete(String message, String channel) {
		
		Jedis publisherJedis = null;
		try {
			JedisPoolConfig poolConfig = new JedisPoolConfig();
	        JedisPool jedisPool = new JedisPool(poolConfig, searcherHostname, 6379, 0);
	        
	        publisherJedis = jedisPool.getResource();
	        
			publisherJedis.publish(channel, message);
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		finally {
			if(publisherJedis != null)
				publisherJedis.disconnect();
		}
	}
	
}

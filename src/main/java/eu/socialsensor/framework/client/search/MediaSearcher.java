package eu.socialsensor.framework.client.search;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MediaSearcher {

	public final Logger logger = Logger.getLogger(MediaSearcher.class);
	public static final String CHANNEL = "searchRequestsChannel";
	
	private String searcherHostname;
	
	private JedisPool jedisPool;
	
	public MediaSearcher(String searcherHostname) {
		this.searcherHostname = searcherHostname;
		
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		
        this.jedisPool = new JedisPool(poolConfig, this.searcherHostname, 6379, 0);
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
	        publisherJedis = jedisPool.getResource();
			publisherJedis.publish(channel, message);

		}
		catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		finally {
			if(publisherJedis != null) {
				publisherJedis.disconnect();
				jedisPool.returnResource(publisherJedis);
			}
		}
	}
	
	public void delete(String message, String channel) {
		
		Jedis publisherJedis = null;
		try {
	        publisherJedis = jedisPool.getResource();
			publisherJedis.publish(channel, message);
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		finally {
			if(publisherJedis != null) {
				publisherJedis.disconnect();
				jedisPool.returnResource(publisherJedis);
			}
		}
	}
	
	public static void main(String...args) {
		MediaSearcher searcher = new MediaSearcher("160.40.51.18");
		
		for(int i=0; i<10; i++) {
			searcher.search("test_"+i, "test");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

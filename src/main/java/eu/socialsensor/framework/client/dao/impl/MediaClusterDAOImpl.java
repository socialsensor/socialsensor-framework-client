package eu.socialsensor.framework.client.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.socialsensor.framework.client.dao.MediaClusterDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.common.domain.MediaCluster;
import eu.socialsensor.framework.common.factories.ItemFactory;

public class MediaClusterDAOImpl implements MediaClusterDAO {

	private List<String> indexes = new ArrayList<String>();
	private MongoHandler mongoHandler;
	
	public MediaClusterDAOImpl(String host, String db, String collection) {
		indexes.add("id");
		
        try {
			mongoHandler = new MongoHandler(host, db, collection, indexes);
		} catch (Exception e) {
			e.printStackTrace();
		}
   
    }

	@Override
	public void addMediaCluster(MediaCluster mediaCluster) {
		mongoHandler.insert(mediaCluster);
	}

	@Override
	public MediaCluster getMediaCluster(String clusterId) {
		String json = mongoHandler.findOne("id", clusterId);
		MediaCluster mediaCluster = ItemFactory.createMediaCluster(json);
		return mediaCluster;
	}
	
	@Override
	public void addMediaItemInCluster(String clusterId, String memberId) {
		DBObject update = new BasicDBObject();
		update.put("$addToSet", new BasicDBObject("members", memberId));
		update.put("$inc", new BasicDBObject("count", 1));
	
		mongoHandler.update("id", clusterId, update);
	}

	public static void main(String...args) {
		
		String q = "4c423695-823e-45ae-ba15-877488fd7dfb";
		
		MediaClusterDAO dao = new MediaClusterDAOImpl("xxx.xxx.xxx.xxx","Prototype","MediaClusters");
		
		MediaCluster mediaCluster = dao.getMediaCluster(q);
		System.out.println(mediaCluster.toJSONString());
		
	}

}

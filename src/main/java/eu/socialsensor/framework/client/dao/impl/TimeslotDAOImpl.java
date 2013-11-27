package eu.socialsensor.framework.client.dao.impl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import eu.socialsensor.framework.client.dao.TimeslotDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.client.mongo.Selector;
import eu.socialsensor.framework.common.domain.Timeslot;
import eu.socialsensor.framework.common.factories.ItemFactory;

public class TimeslotDAOImpl implements TimeslotDAO {
	 
	private MongoHandler mongoHandler;
	
	private String db = "Streams";
	private final String collection = "Timeslots";
	private List<String> indexes = new ArrayList<String>();

	public TimeslotDAOImpl(String host) {
		try {
			indexes.add("timeslotId");
			indexes.add("timestamp");
			mongoHandler = new MongoHandler(host, db, collection, indexes);
			
			mongoHandler.sortBy("timestamp", MongoHandler.DESC);
		} catch (UnknownHostException ex) {
			org.apache.log4j.Logger.getRootLogger().error(ex.getMessage());
		}
	}
	
	@Override
	public void insertTimeslot(Timeslot timeslot) {
		mongoHandler.insert(timeslot);
	}

	@Override
	public Timeslot getTimeslot(String id) {
		String json = mongoHandler.findOne("timeslotId", id);
        Timeslot timeslot = ItemFactory.createTimeslot(json);
        return timeslot;
	}
	
	@Override
	public Timeslot getLastTimeslot() {
		String json = mongoHandler.findOne();
		Timeslot timeslot = ItemFactory.createTimeslot(json);
        return timeslot;
	}

	@Override
	public List<Timeslot> getTimeslots(long timestamp) {
		
		Selector selector = new Selector();
		selector.selectGreaterThan("timestamp", timestamp);
		
		List<String> jsonTimeslots = mongoHandler.findMany(selector, -1);
        List<Timeslot> timeslots = new ArrayList<Timeslot>();
        for (String json : jsonTimeslots) {
        	timeslots.add(ItemFactory.createTimeslot(json));
        }
        return timeslots;
	}

	@Override
	public List<Timeslot> getLatestTimeslots(int N) {
		
		List<String> jsonTimeslots = mongoHandler.findMany(new Selector(), N);
        List<Timeslot> timeslots = new ArrayList<Timeslot>();
        for (String json : jsonTimeslots) {
        	timeslots.add(ItemFactory.createTimeslot(json));
        }
        return timeslots;
	}
}
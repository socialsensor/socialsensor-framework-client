package eu.socialsensor.framework.client.dao;

import java.util.List;

import eu.socialsensor.framework.common.domain.Timeslot;

public interface TimeslotDAO {

	public void insertTimeslot(Timeslot timeslot);
	
	public Timeslot getTimeslot(String id);
	
	public Timeslot getLastTimeslot();
	public List<Timeslot> getLatestTimeslots(int N);
	public List<Timeslot> getTimeslots(long timestamp);

}
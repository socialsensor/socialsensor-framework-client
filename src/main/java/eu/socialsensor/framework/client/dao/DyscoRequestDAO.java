package eu.socialsensor.framework.client.dao;

import java.util.List;

import eu.socialsensor.framework.common.domain.DyscoRequest;

/**
 * 
 * @author ailiakop
 * @email  ailiakop@iti.gr
 */
public interface DyscoRequestDAO {
	
	public void insertDyscoRequest(DyscoRequest request);
	
	public boolean deleteDyscoRequest(DyscoRequest request);
	
	public DyscoRequest getDyscoRequest(String id);
	
	public void updateRequest(Object object);
	
	public boolean exists(String id);
	
	public void readRequests(List<Object> items);
	
	public List<DyscoRequest> readRequestsByStatus();
	
	public List<String> readKeywordsFromDyscos(List<String> dyscoIds);
	
	public List<DyscoRequest> readUnsearchedRequestsByType(String type);
	
}

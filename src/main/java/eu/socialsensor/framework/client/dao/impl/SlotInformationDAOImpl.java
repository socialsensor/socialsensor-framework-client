package eu.socialsensor.framework.client.dao.impl;

import eu.socialsensor.framework.client.dao.SlotInformationDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.common.domain.dysco.SlotInformation;
import eu.socialsensor.framework.common.factories.SlotInformationFactory;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class SlotInformationDAOImpl implements SlotInformationDAO {

    List<String> indexes = new ArrayList<String>();
    private final String db = "social-sensor";
    private final String collection = "SlotInformation";
    private MongoHandler mongoHandler;

    
    public SlotInformationDAOImpl(String host) {

        try {
            mongoHandler = new MongoHandler(host, db, collection, indexes);
        } catch (UnknownHostException ex) {
            Logger.getRootLogger().error(ex.getMessage());
        }
    }

    @Override
    public SlotInformation getSlotInformation() {

        
        String json = mongoHandler.findOne();
        
        SlotInformation slotInformation = SlotInformationFactory.create(json);
        return slotInformation;

    }

    @Override
    public void setSlotInformation(SlotInformation slotInformation) {

        mongoHandler.clean();
        mongoHandler.insert(slotInformation);

    }
}

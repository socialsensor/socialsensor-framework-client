package eu.socialsensor.framework.client.dao.impl;

import eu.socialsensor.framework.client.dao.PlatformUserDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.common.domain.PlatformUser;
import eu.socialsensor.framework.common.factories.ItemFactory;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author stzoannos
 */
public class PlatformUserDAOImpl implements PlatformUserDAO {

    List<String> indexes = new ArrayList<String>();
    private static String db = "Streams";
    private static String collection = "PlatformUsers";
    private MongoHandler mongoHandler;

    @Override
    public void addPlatformUser(PlatformUser user) {
        mongoHandler.insert(user);
    }

    @Override
    public void updatePlatformUser(PlatformUser user) {
        mongoHandler.update("name", user.getName(), user);
    }

    @Override
    public PlatformUser getPlatformUser(String name) {

        String json = mongoHandler.findOne("name", name);
        PlatformUser user = ItemFactory.createPlatformUser(json);
        return user;
    }

    public PlatformUserDAOImpl(String host) {
        this(host, db, collection);
    }

    public PlatformUserDAOImpl(String host, String db) {
        this(host, db, collection);
    }

    public PlatformUserDAOImpl(String host, String db, String collection) {
        indexes.add("name");

        try {
            mongoHandler = new MongoHandler(host, db, collection, indexes);
        } catch (UnknownHostException ex) {
            Logger.getRootLogger().error(ex.getMessage());
        }
    }
}

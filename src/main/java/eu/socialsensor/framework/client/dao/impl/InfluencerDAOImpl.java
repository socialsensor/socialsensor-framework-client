/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.socialsensor.framework.client.dao.impl;

import eu.socialsensor.framework.client.dao.InfluencerDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.common.factories.ItemFactory;
import eu.socialsensor.framework.common.factories.KeywordInfluencersPairFactory;
import eu.socialsensor.framework.common.influencers.Influencer;
import eu.socialsensor.framework.common.influencers.KeywordInfluencersPair;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author etzoannos
 */
public class InfluencerDAOImpl implements InfluencerDAO {

    List<String> indexes = new ArrayList<String>();
    private final String host = "";
    private final String db = "Streams";
    private final String collection = "influencers";
    private MongoHandler mongoHandler;

    public InfluencerDAOImpl() {

        try {
            indexes.add("keyword");
            mongoHandler = new MongoHandler("social1.atc.gr", "Streams", "influencers", indexes);

        } catch (UnknownHostException ex) {
            Logger.getRootLogger().error(ex.getMessage());
        }
    }

    public InfluencerDAOImpl(String host, String db, String collection) {


        try {
            indexes.add("keyword");
            mongoHandler = new MongoHandler(host, db, collection, indexes);

        } catch (UnknownHostException ex) {
            Logger.getRootLogger().error(ex.getMessage());
        }
    }

//    public InfluencerDAOImpl() {
//        try {
//        	indexes.add("keyword");
//            mongoHandler = new MongoHandler(host, db, collection, indexes);
//        } catch (UnknownHostException ex) {
//            org.apache.log4j.Logger.getRootLogger().error(ex.getMessage());
//        }
//    }
    @Override
    public void addInfluencersForKeyword(String keyword, List<Influencer> influencers) {

        KeywordInfluencersPair pair = new KeywordInfluencersPair(keyword, influencers);
        mongoHandler.insert(pair);
    }

    @Override
    public List<Influencer> getInfluencersForKeyword(String keyword) {
        String json = mongoHandler.findOne("keyword", keyword);
        KeywordInfluencersPair pair = KeywordInfluencersPairFactory.create(json);
        if (pair != null) {
            List<Influencer> influencers = pair.getInfluencers();
            for (Influencer influencer : influencers) {
                System.out.println(influencer.getId());
                System.out.println(influencer.getScore());
            }

            return pair.getInfluencers();
        } else {
            return null;
        }
    }

    public List<Influencer> getInfluencersForKeywords(List<String> keywords) {

        List<String> results = mongoHandler.findManyWithOr("keyword", keywords, 100);
        List<KeywordInfluencersPair> pairs = new ArrayList<KeywordInfluencersPair>();
        Set<Influencer> influencers = new HashSet<Influencer>();

        for (String json : results) {

            KeywordInfluencersPair pair = KeywordInfluencersPairFactory.create(json);
            if (pair != null) {
                influencers.addAll(pair.getInfluencers());
            }

        }

        List<Influencer> list = new ArrayList<Influencer>(influencers);
        return list;
    }

    public void removeInfluencerKeywordPair(Long contributorId, String keyword) {

        Map<String, Object> m = new HashMap<String, Object>();
        m.put("contributorId", contributorId);
        m.put("keyword", keyword);
        mongoHandler.delete(m);

    }

    public void clearAll() {
        mongoHandler.clean();
    }

    public static void main(String... args) {


        List<String> indexes = new ArrayList<String>();
        indexes.add("keyword");

        List<String> values = new ArrayList<String>();
        values.add("sharp");
        values.add("order");
        values.add("justin");
        InfluencerDAO influencerDAO = new InfluencerDAOImpl("", "", "");

        List<Influencer> influencers = influencerDAO.getInfluencersForKeywords(values);

        for (Influencer influencer : influencers) {
            System.out.println(influencer.getId());
        }
    }
}

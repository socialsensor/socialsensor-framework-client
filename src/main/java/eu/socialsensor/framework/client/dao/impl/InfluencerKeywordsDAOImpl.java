/*
 * Copyright 2014 stzoannos.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.socialsensor.framework.client.dao.impl;

import eu.socialsensor.framework.client.dao.InfluencerKeywordsDAO;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import eu.socialsensor.framework.common.domain.Keyword;
import eu.socialsensor.framework.common.factories.InfluencerKeywordsPairFactory;
import eu.socialsensor.framework.common.influencers.InfluencerKeywordsPair;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author stzoannos
 */
public class InfluencerKeywordsDAOImpl implements InfluencerKeywordsDAO {

    List<String> indexes = new ArrayList<String>();
    private final String host = "";
    private final String db = "";
    private final String collection = "";
    private MongoHandler mongoHandler;

    public InfluencerKeywordsDAOImpl(String host, String db, String collection) {

        try {
            indexes.add("influencer");
            mongoHandler = new MongoHandler(host, db, collection, indexes);

        } catch (UnknownHostException ex) {
            Logger.getRootLogger().error(ex.getMessage());
        } catch (Exception e) {
            Logger.getRootLogger().error(e.getMessage());
        }
    }

    @Override
    public void addKeywordsForInfluencer(String influencer, List<Keyword> keywords) {
        InfluencerKeywordsPair pair = new InfluencerKeywordsPair(influencer, keywords);
        mongoHandler.insert(pair);
    }

    @Override
    public List<Keyword> getKeywordsForInfluencer(String influencer) {

        String json = mongoHandler.findOne("influencer", influencer);
        List<Keyword> keywords = new ArrayList<Keyword>();
        if (json != null) {
            InfluencerKeywordsPair result = InfluencerKeywordsPairFactory.create(json);
            if (result != null) {
                keywords = result.getKeywords();
            }
        }
        return keywords;
    }

    public static void main(String... args) {

        InfluencerKeywordsDAO dao = new InfluencerKeywordsDAOImpl("social1.atc.gr", "Streams", "influencer-keywords");

        List<Keyword> keywords = new ArrayList<Keyword>();

        Keyword key1 = new Keyword("key1", 1.0d);
        Keyword key2 = new Keyword("key2", 2.0d);
        Keyword key3 = new Keyword("key3", 3.0d);

        keywords.add(key1);
        keywords.add(key2);
        keywords.add(key3);

        dao.addKeywordsForInfluencer("influencer1", keywords);
        dao.addKeywordsForInfluencer("influencer2", keywords);
        dao.addKeywordsForInfluencer("influencer3", keywords);

        Logger.getRootLogger().info("successfully inserted influencers");

        List<Keyword> results = dao.getKeywordsForInfluencer("influencer2");

        for (Keyword result : results) {
            Logger.getRootLogger().info("found: " + result.getName() + " " + result.getScore());

        }

    }

}

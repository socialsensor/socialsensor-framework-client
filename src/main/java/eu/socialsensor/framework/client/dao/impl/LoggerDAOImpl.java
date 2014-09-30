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

import com.mongodb.BasicDBObject;
import eu.socialsensor.framework.client.mongo.MongoHandler;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author stzoannos
 */
public class LoggerDAOImpl {

    List<String> indexes = new ArrayList<String>();
    private static String db = "Streams";
    private static String collection = "Logs";
    private MongoHandler mongoHandler;

    public LoggerDAOImpl(String host, String db, String collection) throws Exception {

        mongoHandler = new MongoHandler(host, db, collection, indexes);

    }

    public void insertLog(String json) {

        mongoHandler.insertJson(json);
    }

    public String findLog(String dyscoId, String msEpochTime, String deviceVendorId, String sortingPosition) {

        BasicDBObject query = new BasicDBObject();
        if (dyscoId != null) {
            query.put("dyscoId", dyscoId);
        }
        if (msEpochTime != null) {
            Long epochTime = new Long(msEpochTime);
            query.put("msEpochTime", new BasicDBObject("$gte", epochTime));
        }
        if (deviceVendorId != null) {
            query.put("deviceVendorId", deviceVendorId);
        }
        if (sortingPosition != null) {
            query.put("sortingPosition", sortingPosition);
        }

        List<String> results = mongoHandler.findMany(query, 1000);

        String response = "[";

        for (int i = 0; i < results.size(); i++) {

            response = response + results.get(i);
            
            if (i!=(results.size()-1)) {
                response = response + ",";
            }
       
        }

        response = response + "]";
        return response;
    }

    public static void main(String... args) throws Exception {

        LoggerDAOImpl dao = new LoggerDAOImpl("socialmdb1", "Streams", "Logs");

        String response = dao.findLog(null, null, "1410336928096", null);
        
        System.out.println(response);

    }

}

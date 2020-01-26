package com.github.qareport;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Objects;

public class QaReportTest {

    private QaReport qaReport=new QaReport();

    private MongoDatabase database;

    private int previousBuildId;

    @Test
    private void testSetConnectionCreatesABuild()
    {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://" + "localhost:27017"));

        database = mongoClient.getDatabase("qaReportAutomate");

        Document lastBuildDocument = new MongoClient().getDatabase("qaReportAutomate").getCollection("builds").find().sort(new BasicDBObject("_id", -1)).first();

        if (lastBuildDocument != null) {

            previousBuildId = (Integer) (lastBuildDocument.get("_id"));

        } else {

            previousBuildId = 0;
        }

        qaReport.setConnection("localhost:27017", "qaReportAutomate", "Feature");

        int buildId = (Integer) Objects.requireNonNull(new MongoClient().getDatabase("qaReportAutomate").getCollection("builds").find().sort(new BasicDBObject("_id", -1)).first()).get("_id");

        Assert.assertEquals(buildId, previousBuildId+1, "setConnection should create a build using the connection parameters" );
    }
}

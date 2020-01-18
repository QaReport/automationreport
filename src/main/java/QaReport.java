import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class QaReport {

    private MongoDatabase database;

    private static int build_id;

    private int previousBuildId;

    private static int test_id;

    private static int logTestId;

    private String reportName;

    private long buildStartTime;

    private long buildDuration;

    private long testStartTime;

    private long testDuration;

    private String logStart = "";

    private String projectName;

    private static int actualTestId;

    private static int createBuild;


    /**
     * <p>
     * <b>setConnection(String mongoURL, String projectName, String reportName)</b>
     *
     * <br><br>setConnection configures the mongoDb instance com.dynamics.connection, creates a database based on the project name and creates a build with the report name.
     *
     * <br><br><b>Example: setConnection("localhost:27017", "Your Project", "Smoke")</b>
     *
     * </p>
     *
     * @param mongoURL    Connection URL for mongodb
     * @param projectName Database name to be set
     * @param reportName  Build name to be set
     **/
    public void setConnection(String mongoURL, String projectName, String reportName) {

        this.projectName = projectName;

        this.reportName = reportName;

        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://" + mongoURL));

        database = mongoClient.getDatabase(this.projectName);

        Document lastBuildId = new MongoClient().getDatabase(this.projectName).getCollection("builds").find().sort(new BasicDBObject("_id", -1)).first();

        if (lastBuildId != null) {

            previousBuildId = (Integer) (lastBuildId.get("_id"));

        } else {

            previousBuildId = 0;
        }

        createBuild();

    }

    /**
     * <p>
     * <b>setConnection(String mongoURL, String userName, String password, String projectName, String reportName)</b>
     *
     * <br><br>setConnection configures the mongoDb instance com.dynamics.connection, creates a database based on the project name and creates a build with the report name.
     *
     * <br><br><b>Example: setConnection("localhost:27017", "admin", "password", "Your Project", "Smoke")</b>
     *
     * </p>
     *
     * @param mongoURL    Connection URL for mongodb
     * @param projectName Database name to be set
     * @param userName    username for mongodb
     * @param password    password for mongodb
     * @param reportName  Build name to be set
     **/
    public void setConnection(String mongoURL, String userName, String password, String projectName, String reportName) {

        this.projectName = projectName;

        this.reportName = reportName;

        MongoClient mongoClient = new MongoClient(new MongoClientURI(String.format("mongodb://%s:%s@%s", userName, password, mongoURL)));

        database = mongoClient.getDatabase(this.projectName);

        Document lastBuildId = new MongoClient().getDatabase(this.projectName).getCollection("builds").find().sort(new BasicDBObject("_id", -1)).first();

        if (lastBuildId != null) {

            previousBuildId = (Integer) (lastBuildId.get("_id"));

        } else {

            previousBuildId = 0;
        }

        createBuild();

    }

    private void createBuild() {

        createBuild++;

        if (createBuild == 1) {

            buildStartTime = System.currentTimeMillis();

            build_id = previousBuildId + 1;

            Document buildDocument = new Document("_id", build_id)
                    .append("build_name", reportName)
                    .append("number_of_tests", test_id)
                    .append("created_time", new SimpleDateFormat("M/d/yyyy hh:mm:ss").format(Calendar.getInstance().getTime()))
                    .append("ended_time", new SimpleDateFormat("M/d/yyyy hh:mm:ss").format(Calendar.getInstance().getTime()))
                    .append("duration", buildDuration).append("Status", "Pass")
                    .append("Total Number of Tests", test_id);

            database.getCollection("builds").insertOne(buildDocument);

        } else {

            updateBuildDocument();

        }
    }

    /**
     * <p>
     * <b>createTest(String testName, Status status)</b>
     *
     * <br><br>createTest creates a test and assigns a tag to it in the respective build it is running in.
     *
     * <br><br><b>Example: createTest("Test Name", Status.Critical)</b>
     *
     * </p>
     *
     * @param testName Name to be assigned for the test
     * @param status   A Tag to assign for the test (Critical/Major/Minor)
     **/
    public void createTest(String testName, Status status) {

        test_id++;

        if (test_id == 1) {

            Document lastTestId = new MongoClient().getDatabase(this.projectName).getCollection("tests").find().sort(new BasicDBObject("_id", -1)).first();

            if (lastTestId != null) {

                actualTestId = (Integer) lastTestId.get("_id");

            } else {

                actualTestId = 0;
            }

        }

        actualTestId++;

        testStartTime = System.currentTimeMillis();

        Document testDocument;

        if ("Critical".equalsIgnoreCase(status.toString())) {

            testDocument = new Document("_id", actualTestId)
                    .append("testName", testName)
                    .append("testLog", logStart)
                    .append("build_id", build_id)
                    .append("Status", "Pass")
                    .append("Tag", "Critical")
                    .append("created_time", new SimpleDateFormat("M/d/yyyy hh:mm:ss").format(Calendar.getInstance().getTime()))
                    .append("created_time", new SimpleDateFormat("M/d/yyyy hh:mm:ss").format(Calendar.getInstance().getTime()))
                    .append("duration", testDuration);

        } else if ("Major".equalsIgnoreCase(status.toString())) {

            testDocument = new Document("_id", actualTestId)
                    .append("testName", testName)
                    .append("testLog", logStart)
                    .append("build_id", build_id)
                    .append("Status", "Pass")
                    .append("Tag", "Major")
                    .append("created_time", new SimpleDateFormat("M/d/yyyy hh:mm:ss").format(Calendar.getInstance().getTime()))
                    .append("created_time", new SimpleDateFormat("M/d/yyyy hh:mm:ss").format(Calendar.getInstance().getTime()))
                    .append("duration", testDuration);

        } else {
            testDocument = new Document("_id", actualTestId)
                    .append("testName", testName)
                    .append("testLog", logStart)
                    .append("build_id", build_id)
                    .append("Status", "Pass")
                    .append("Tag", "Minor")
                    .append("created_time", new SimpleDateFormat("M/d/yyyy hh:mm:ss").format(Calendar.getInstance().getTime()))
                    .append("created_time", new SimpleDateFormat("M/d/yyyy hh:mm:ss").format(Calendar.getInstance().getTime()))
                    .append("duration", testDuration);
        }

        database.getCollection("tests").insertOne(testDocument);

        createBuild();
    }

    /**
     * <p>
     * <b>log(String log, com.dynamics.TestStatus status)</b>
     *
     * <br><br>log creates a log for the test that is currently in progress.
     *
     * <br><br><b>Example: log("log", com.dynamics.TestStatus.PASS)</b>
     *
     * </p>
     *
     * @param log Log for the test
     * @param status Status of the log
     **/
    public void log(String log, TestStatus status) throws QaReportException {
        StringBuilder logBuilder = new StringBuilder(logStart);

        int ascii;

        String sign;

        if (test_id == 0) {

            throw new QaReportException("create test to log");

        } else {

            if (logTestId != test_id) {

                logBuilder = new StringBuilder("");

                if ("Pass".equals(status.toString())) {

                    ascii = 0x2714;

                    sign = Character.toString((char) ascii);

                } else {

                    ascii = 0x2718;

                    sign = Character.toString((char) ascii);

                    failTest();

                    failBuild();
                }

                logStart = logBuilder.append(sign).toString();


                logStart = logBuilder.append(log).toString();

                logTestId = test_id;

                updateTestDocument(logStart);

            } else {
                logStart = logBuilder.append(System.getProperty("line.separator")).toString();

                if ("Pass".equals(status.toString())) {

                    ascii = 0x2714;

                    sign = Character.toString((char) ascii);


                } else {

                    ascii = 0x2718;

                    sign = Character.toString((char) ascii);

                    failTest();

                    failBuild();

                }

                logStart = logBuilder.append(sign).toString();

                logStart = logBuilder.append(log).toString();

                updateTestDocument(logStart);
            }

        }

    }

    private void updateTestDocument(String log) {

        long testEndTime = System.currentTimeMillis();

        testDuration = testEndTime - testStartTime;

        database.getCollection("tests").updateOne(new BasicDBObject("_id", actualTestId),
                new BasicDBObject("$set", new BasicDBObject("testLog", log)));

        database.getCollection("tests").updateOne(new BasicDBObject("_id", actualTestId),
                new BasicDBObject("$set", new BasicDBObject("ended_time", new SimpleDateFormat("M/d/yyyy hh:mm:ss").format(Calendar.getInstance().getTime()))));

        database.getCollection("tests").updateOne(new BasicDBObject("_id", actualTestId),
                new BasicDBObject("$set", new BasicDBObject("duration", testDuration)));

    }

    private void updateBuildDocument() {
        long buildEndTime = System.currentTimeMillis();

        buildDuration = buildEndTime - buildStartTime;

        database.getCollection("builds").updateOne(new BasicDBObject("_id", build_id),
                new BasicDBObject("$set", new BasicDBObject("number_of_tests", test_id)));

        database.getCollection("builds").updateOne(new BasicDBObject("_id", build_id),
                new BasicDBObject("$set", new BasicDBObject("Total Number of Tests", test_id)));

        database.getCollection("builds").updateOne(new BasicDBObject("_id", build_id),
                new BasicDBObject("$set", new BasicDBObject("ended_time", new SimpleDateFormat("M/d/yyyy hh:mm:ss").format(Calendar.getInstance().getTime()))));

        database.getCollection("builds").updateOne(new BasicDBObject("_id", build_id),
                new BasicDBObject("$set", new BasicDBObject("duration", buildDuration)));

    }

    /**
     * <p>
     * <b>failTest()</b>
     *
     * <br><br>failTest updates the status of the test to fail. It is to handle in cases where there is unexpected error and the log statements could not be reached. It is usually used in @After methods where the result of the test can be caught.
     *
     * </p>
     *
     **/
    public void failTest() {

        database.getCollection("tests").updateOne(new BasicDBObject("_id", actualTestId),
                new BasicDBObject("$set", new BasicDBObject("Status", "Fail")));
    }

    /**
     * <p>
     * <b>failBuild()</b>
     *
     * <br><br>failBuild updates the status of the build to fail. It is to handle in cases where there is unexpected error and the log statements could not be reached. It is usually used in @After methods where the result of the test can be caught.
     *
     * </p>
     *
     **/
    public void failBuild() {

        database.getCollection("builds").updateOne(new BasicDBObject("_id", build_id),
                new BasicDBObject("$set", new BasicDBObject("Status", "Fail")));
    }

    /**
     * <p>
     * <b>createCoverage()</b>
     *
     * <br><br>createCoverage sets a total number of tests field in the build document which can be used for covergae charts with number of tests vs total number of tests fields in the build document.
     *
     * </p>
     *
     **/
    public void createCoverage(int totalNumberOfTests) {

        database.getCollection("builds").updateOne(new BasicDBObject("_id", build_id),
                new BasicDBObject("$set", new BasicDBObject("Total Number of Tests", totalNumberOfTests)));
    }
}

package ebs.communication.entities;

public class Constants {
    public static final String RABBITMQ_SERVER = "http://localhost:15672";
    public static final String IP = "localhost";
    public static final int PORT = 5672;
    public static final String USERNAME = "guest";
    public static final String PASSWORD = "guest";

    public static final String ROOT_QUEUE_NAME = "broker_1";

    public static final String SUBSCRIBER_QUEUE_NAME = "sub_";
    public static final String PUBLISHER_QUEUE_NAME = "pub_";
    public static final String BROKER_QUEUE_NAME = "broker_";

    public static final String PUBLICATION_TYPE = "publication";
    public static final String SUBSCRIPTION_TYPE = "subscribe";

    public static final int NUMBER_OF_SUBSCRIBERS = 3;
    public static final int NUMBER_OF_PUBLISHERS = 2;
    public static final int NUMBER_OF_BROKERS = 3;
}

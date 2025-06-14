package ebs.communication;

import com.rabbitmq.client.*;
import ebs.communication.helpers.RabbitMqConfig;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class RabbitQueue {
    @Getter
    private final String name;
    @Getter
    private final RabbitMqConfig config;
    private Connection connection;
    private Channel channel;

    protected static final Logger logger = LoggerFactory.getLogger(RabbitQueue.class);
    private static final int MAX_RETRIES = 3;

    public RabbitQueue(RabbitMqConfig rabbitMqConfig, boolean purgeTheQueue) {
        this.config = rabbitMqConfig;
        this.name = rabbitMqConfig.getQueueName();
        this.init();

        if (purgeTheQueue) {
            this.purge();
        }
    }

    public void init() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(config.getUsername());
        factory.setPassword(config.getPassword());
        factory.setHost(config.getHost());
        factory.setPort(config.getPort());

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(config.getExchange(), "direct", true);
            channel.queueDeclare(config.getQueueName(), true, false, false, null);
            channel.queueBind(config.getQueueName(), config.getExchange(), config.getRoutingKey());
            logger.info("RabbitMQ initialized successfully.");

        } catch (Exception e) {
            logger.error("Failed to initialize RabbitMQ.", e);
        }
    }

    public void callback(byte[] message) {
        System.out.println("Implement me");
    }

    public void sendMessage(byte[] message) {
        for (int retryCount = 0; retryCount < MAX_RETRIES; retryCount++) {
            try {
//                log("S", jsonObject.getString("source"), getName(), jsonObject.getString("message"));
                channel.basicPublish(config.getExchange(), config.getRoutingKey(), null, message);
                return;
            } catch (Exception e) {
                logger.error("Failed to send message. Attempt {}/{}", retryCount + 1, MAX_RETRIES, e);
                init();
            }
        }

        logger.error("Exceeded maximum retries. Failed to send message.");
        throw new RuntimeException("Failed to send message after " + MAX_RETRIES + " attempts");
    }

    public void receiveMessage() {
        try {
            channel.basicConsume(config.getQueueName(), false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    for (int retryCount = 0; retryCount < MAX_RETRIES; retryCount++) {
                        try {

                            channel.basicAck(envelope.getDeliveryTag(), false);

//                            JSONObject jsonObject = new JSONObject(body);
//                            log("R", jsonObject.getString("source"), getName(), jsonObject.getString("message"));

                            callback(body);

                            return;
                        } catch (Exception e) {
                            logger.error("Failed to process message. Attempt {}/{}", retryCount + 1, MAX_RETRIES, e);
                        }
                    }
                    logger.error("Exceeded maximum retries. Sending NACK for message.");
                    try {
                        channel.basicNack(envelope.getDeliveryTag(), false, true);
                    } catch (Exception nackException) {
                        logger.error("Failed to send NACK.", nackException);
                    }
                }
            });
            logger.info(" [*] Waiting for messages. To exit press CTRL+C");
        } catch (Exception e) {
            logger.error("Failed to receive messages.", e);
            throw new RuntimeException("Failed to receive messages", e);
        }
    }

    public void log(String direction, String from, String to, String message) {
        logger.info("[{}] {} -> {}: M: '{}'", direction, from, to, message);
    }

    public void purge() {
        try {
            channel.queuePurge(config.getQueueName());
        } catch (IOException e) {
            logger.info("Could not purge queue {}", config.getQueueName());
        }
    }

    public void close() {
        try {
            channel.close();
            connection.close();
            logger.info("RabbitMQ connection closed successfully.");
        } catch (Exception e) {
            logger.error("Failed to close RabbitMQ connection.", e);
        }
    }
}

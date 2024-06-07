package ebs.communication;

import com.rabbitmq.client.*;
import ebs.communication.helpers.RabbitMqConfig;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;


public class RabbitQueue {
    @Getter
    private final String name;
    private final RabbitMqConfig config;
    private Connection connection;
    private Channel channel;

    private static final Logger logger = LoggerFactory.getLogger(RabbitQueue.class);
    private static final int MAX_RETRIES = 3;

    public RabbitQueue(RabbitMqConfig rabbitMqConfig) {
        this.config = rabbitMqConfig;
        this.name = rabbitMqConfig.getQueueName();
        this.init();
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

    public void callback(String message) {
        System.out.println(message);
    }

    public void sendMessage(String message) {
        for (int retryCount = 0; retryCount < MAX_RETRIES; retryCount++) {
            try {
                channel.basicPublish(config.getExchange(), config.getRoutingKey(), null, message.getBytes(StandardCharsets.UTF_8));
                logger.info("[*] Sent to {}: '{}'", config.getQueueName(), message);
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
                            String message = new String(body, StandardCharsets.UTF_8);
                            logger.info(" [+] Received '{}'", message);

                            channel.basicAck(envelope.getDeliveryTag(), false);
                            callback(message);

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

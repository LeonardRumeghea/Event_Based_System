package ebs.communication.helpers;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class  RabbitMqConfig {
    private String username;
    private String password;
    private String host;
    private int port;
    private String queueName;
    private String routingKey;
    private String exchange;

    public String getAmqpUri() {
        return String.format("amqp://%s:%s@%s:%d", username, password, host, port);
    }
}
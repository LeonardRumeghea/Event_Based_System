package ebs.communication.helpers;

import static ebs.communication.entities.Constants.*;

public class Tools {
    public static RabbitMqConfig getConfigFor(String name) {
        return new RabbitMqConfigBuilder()
                .username(USERNAME)
                .password(PASSWORD)
                .host(IP)
                .port(PORT)
                .queueName(name)
                .routingKey(name + "_routing")
                .exchange(name + "_exchange")
                .build();

    }
}

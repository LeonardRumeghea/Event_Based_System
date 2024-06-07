package ebs.communication.helpers;

public class Tools {
    public static RabbitMqConfig getConfigFor(String name) {
        return new RabbitMqConfigBuilder()
                .username("guest")
                .password("guest")
                .host("localhost")
                .port(5672)
                .queueName(name)
                .routingKey(name + "_routing")
                .exchange(name + "_exchange").build();

    }
}

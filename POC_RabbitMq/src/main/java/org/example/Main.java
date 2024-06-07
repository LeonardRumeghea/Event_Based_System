package org.example;

import org.example.communication.RabbbitMq;
import org.example.communication.Subscription;
import org.example.communication.helpers.RabbitMqConfigBuilder;

public class Main {
    public static void main(String[] args) {
        var config = new RabbitMqConfigBuilder()
                .username("guest")
                .password("guest")
                .host("localhost")
                .port(5672)
                .queueName("test")
                .routingKey("test")
                .exchange("test").build();

        var rabbit = new RabbbitMq(config);
        rabbit.init();

        var sub = new Subscription("Costel", 10, 10.0);
        rabbit.sendMessage(sub.toJson());

        var subscriptionString = rabbit.receiveMessage();
        var subscription = Subscription.fromString(subscriptionString);
        System.out.println("Received: " + subscription);

    }
}
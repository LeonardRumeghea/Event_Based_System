package org.example.communication.helpers;

public class RabbitMqConfigBuilder {
    private final RabbitMqConfig rabbitMq;

    public RabbitMqConfigBuilder() {
        rabbitMq = new RabbitMqConfig();
    }

    public RabbitMqConfigBuilder username(String username) {
        rabbitMq.setUsername(username);
        return this;
    }

    public RabbitMqConfigBuilder password(String password) {
        rabbitMq.setPassword(password);
        return this;
    }

    public RabbitMqConfigBuilder host(String host) {
        rabbitMq.setHost(host);
        return this;
    }

    public RabbitMqConfigBuilder port(int port) {
        rabbitMq.setPort(port);
        return this;
    }

    public RabbitMqConfigBuilder queueName(String queueName) {
        rabbitMq.setQueueName(queueName);
        return this;
    }

    public RabbitMqConfigBuilder routingKey(String routingKey) {
        rabbitMq.setRoutingKey(routingKey);
        return this;
    }

    public RabbitMqConfigBuilder exchange(String exchange) {
        rabbitMq.setExchange(exchange);
        return this;
    }

    public RabbitMqConfig build() {
        return rabbitMq;
    }
}

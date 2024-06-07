package ebs.communication.entities;

import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;

import java.util.List;
import java.util.stream.Collectors;

public class Broker extends RabbitQueue {

    List<RabbitQueue> brokers;
    List<RabbitQueue> subs;

    public Broker(String brokerQueue, List<String> brokerQueues, List<String> subQueues) {
        super(Tools.getConfigFor(brokerQueue));
        brokers = brokerQueues.stream()
                .map(e -> new RabbitQueue(Tools.getConfigFor(e)))
                .collect(Collectors.toList());

        subs = subQueues.stream()
                .map(e -> new RabbitQueue(Tools.getConfigFor(e)))
                .collect(Collectors.toList());
    }

    @Override
    public void callback(String message) {
        for (var broker : brokers) {
            broker.sendMessage(message);
        }
        for (var sub : subs) {
            sub.sendMessage(message);
        }
    }
}

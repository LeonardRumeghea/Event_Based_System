package ebs.communication.entities;

import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import ebs.generator.entities.Publication;
import ebs.generator.entities.Subscription;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

import static ebs.communication.entities.Constants.PUBLICATION_TYPE;
import static ebs.communication.entities.Constants.SUBSCRIPTION_TYPE;
import static ebs.communication.helpers.Tools.isPubMatchedBySub;

public class Broker extends RabbitQueue {

    private final List<RabbitQueue> brokers;
    @Getter
    private final List<RabbitQueue> subscribers;

    private final Map<String, Set<Subscription>> routingTable;

    public Broker(String brokerQueue, @NotNull List<String> brokerQueues, @NotNull List<String> subQueues) {
        super(Tools.getConfigFor(brokerQueue));
        brokers = brokerQueues.stream()
                .map(e -> new RabbitQueue(Tools.getConfigFor(e)))
                .collect(Collectors.toList());

        subscribers = subQueues.stream()
                .map(e -> new RabbitQueue(Tools.getConfigFor(e)))
                .collect(Collectors.toList());

        routingTable = new HashMap<>();
        for (var sub : subscribers) {
            routingTable.put(sub.getName(), new HashSet<>());
        }
    }

//    This method is called when a message is received by the broker. It forwards the message to all other brokers and subs
    @Override
    public void callback(String message) {

        JSONObject jsonObject = new JSONObject(message);

        String source = jsonObject.getString("source");
        String type = jsonObject.getString("type");

        if (type.equals(SUBSCRIPTION_TYPE)) {
            String messageContent = jsonObject.getString("message");

            JSONObject subJson = new JSONObject(messageContent);
            Subscription subscription = Subscription.fromJson(subJson);

            if (routingTable.containsKey(source)) {
                routingTable.get(source).add(subscription);
            } else {
                routingTable.put(source, new HashSet<>(Set.of(subscription)));
            }

//            logger.info("Received subscription {}.  New routing table: {}", subscription, routingTable);
        }

        jsonObject.put("source", getName());

        for (var broker : brokers) {
            if (broker.getName().equals(source)) continue; // Do not send the message back to the source

            broker.sendMessage(jsonObject.toString());
        }

        if (type.equals(PUBLICATION_TYPE)) {
            for (var subscriber : subscribers) {

                if (!routingTable.containsKey(subscriber.getName())) {
                    continue;
                }

                var subscriptions = routingTable.get(subscriber.getName());
                var publication = Publication.fromJson(new JSONObject(jsonObject.getString("message")));

                for (var subscription : subscriptions) {
                    if (isPubMatchedBySub(publication, subscription)) {
                        subscriber.sendMessage(jsonObject.toString());

                        logger.info("Matched publication {} with subscription {}. Sent to {}", publication, subscription, subscriber.getName());

                        break;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Broker{" +
                "name='" + getName() + '\'' +
                ", brokers=" + brokers.stream().map(RabbitQueue::getName).toString() +
                ", subs=" + subscribers.stream().map(RabbitQueue::getName).toString() +
                '}';
    }
}

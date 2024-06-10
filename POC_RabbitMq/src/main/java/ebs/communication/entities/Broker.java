package ebs.communication.entities;

import com.google.protobuf.InvalidProtocolBufferException;
import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import ebs.generator.entities.Publication;
import ebs.generator.entities.Subscription;
import lombok.Getter;
import org.example.protobuf.AddressBookProtos;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ebs.communication.entities.Constants.PUBLICATION_TYPE;
import static ebs.communication.entities.Constants.SUBSCRIPTION_TYPE;
import static ebs.communication.helpers.Tools.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Broker extends RabbitQueue {

    private final List<RabbitQueue> brokers;
    @Getter
    private final List<RabbitQueue> subscribers;

    private final Map<String, Set<Subscription>> routingTable;

    private final Map<Subscription, LinkedHashSet<String>> filterToSources;

    private int publicationNumber;

    public Broker(String brokerQueue, @NotNull List<String> brokerQueues, @NotNull List<String> subQueues, boolean purgeTheQueue) {
        super(Tools.getConfigFor(brokerQueue), purgeTheQueue);
        this.publicationNumber = 0;
        brokers = brokerQueues.stream()
                .map(e -> new RabbitQueue(Tools.getConfigFor(e), true))
                .collect(Collectors.toList());

        subscribers = subQueues.stream()
                .map(e -> new RabbitQueue(Tools.getConfigFor(e), true))
                .collect(Collectors.toList());

        routingTable = new HashMap<>();
        filterToSources = new HashMap<>();
    }

    //    This method is called when a message is received by the broker. It forwards the message to all other brokers and subs
    @Override
    public void callback(byte[] message) {
        try {
            AddressBookProtos.MessageWrapper deserializedMessage = AddressBookProtos.MessageWrapper.parseFrom(message);
            String source = deserializedMessage.getSource();
            String type = deserializedMessage.getType();

            AddressBookProtos.MessageWrapper updatedMessage = deserializedMessage
                    .toBuilder()
                    .setSource(getName())
                    .build();

            if (type.equals(SUBSCRIPTION_TYPE)) {
                Subscription subscription = getSubFromContent(deserializedMessage.getSubscription());
                subscriptionTypeHandler(subscription, source, updatedMessage);
            }
            if (type.equals(PUBLICATION_TYPE)) {
                Publication publication = getPubFromContent(deserializedMessage.getPublication());
                publicationTypeHandler(publication, source, updatedMessage);
            }

            var value = (brokerTimestamps.get(getName()) + 1) % Long.MAX_VALUE;
            brokerTimestamps.put(getName(), value);

        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    private void publicationTypeHandler(Publication publication, String source, AddressBookProtos.MessageWrapper updatedMessage) {

        List<RabbitQueue> allQueues = Stream.concat(brokers.stream(), subscribers.stream()).toList();
        publicationNumber++;
        brokerPubs.put(getName(), publicationNumber);

        for (RabbitQueue entity : allQueues) {

            if (entity.getName().equals(source)) continue; // Do not send the message back to the source

            if (!routingTable.containsKey(entity.getName())) {
                continue;
            }

            var subscriptions = routingTable.get(entity.getName());

            for (var subscription : subscriptions) {
                if (isPubMatchedBySub(publication, subscription)) {
                    entity.sendMessage(updatedMessage.toByteArray());
                    //logger.info("Matched publication with subscription: \n\t{}\n\t{}.\nSent to {}\n", publication, subscription, entity.getName());
                    break;
                }
            }
        }
    }

    private void subscriptionTypeHandler (Subscription subscription, String source, AddressBookProtos.MessageWrapper updatedMessage) {

        if (routingTable.containsKey(source)) {
            routingTable.get(source).add(subscription);
        } else {
            routingTable.put(source, new HashSet<>(Set.of(subscription)));
        }

        if (filterToSources.containsKey(subscription) && filterToSources.get(subscription).size() == 2) {
//            logger.info("[{}]\n\tRouting table: {}\n\tFilter to sources: {}\n", getName(), routingTable, filterToSources);

            return;
        }

        if (filterToSources.containsKey(subscription) && filterToSources.get(subscription).size() == 1) {
            var initialSource = filterToSources.get(subscription).getFirst();
            filterToSources.get(subscription).addLast(source);

            if (initialSource.equals(source)) {
                return;
            }

            brokers
                .stream()
                .filter(e -> e.getName().equals(initialSource))
                .findFirst()
                .ifPresent(sourceBroker -> sourceBroker.sendMessage(updatedMessage.toByteArray()));

//            logger.info("[{}]\n\tRouting table: {}\n\tFilter to sources: {}\n", getName(), routingTable, filterToSources);

            return;
        }

        filterToSources.put(subscription, new LinkedHashSet<>(Set.of(source)));

        for (var broker : brokers) {
            if (broker.getName().equals(source)) continue; // Do not send the message back to the source
            broker.sendMessage(updatedMessage.toByteArray());
        }

//        logger.info("[{}]\n\tRouting table: {}\n\tFilter to sources: {}\n", getName(), routingTable, filterToSources);
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

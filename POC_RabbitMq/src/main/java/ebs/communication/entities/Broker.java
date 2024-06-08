package ebs.communication.entities;

import com.google.protobuf.InvalidProtocolBufferException;
import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import ebs.generator.entities.Pair;
import ebs.generator.entities.Publication;
import ebs.generator.entities.Subscription;
import lombok.Getter;
import org.example.protobuf.AddressBookProtos;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
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
    public void callback(byte[] message) {

        AddressBookProtos.MessageWrapper deserializedMessage = null;
        try {
            deserializedMessage = AddressBookProtos.MessageWrapper.parseFrom(message);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }

        String source = deserializedMessage.getSource();
        String type = deserializedMessage.getType();

        if (type.equals(SUBSCRIPTION_TYPE)) {
            var messageContent = deserializedMessage.getSubscription();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            java.sql.Date date = null;
            Pair<String, java.sql.Date> pair = null;
            try {
                var dateString = (String) messageContent.getDate().getValue();
                if (dateString.compareTo("") != 0) {
                    var tmpDate = dateFormat.parse(dateString);
                    date = new java.sql.Date(tmpDate.getTime());
                    pair = new Pair<>(messageContent.getDate().getSign(), date);
                }
            } catch (Exception e) {
                logger.error("Something went wrong: " + e);
            }

            Pair<String, String> companyPair = null;
            Pair<String, Float> valuePair = null;
            Pair<String, Float> dropPair = null;
            Pair<String, Float> variationPair = null;

            if ((messageContent.getCompany().getSign().compareTo("")) != 0) {
                companyPair = new Pair<>(messageContent.getCompany().getSign(), messageContent.getCompany().getValue());
            }


            if ((messageContent.getValue().getSign().compareTo("")) != 0) {
                valuePair = new Pair<>(messageContent.getValue().getSign(), messageContent.getValue().getValue());
            }

            if ((messageContent.getDrop().getSign().compareTo("")) != 0) {
                dropPair = new Pair<>(messageContent.getDrop().getSign(), messageContent.getDrop().getValue());
            }

            if ((messageContent.getVariation().getSign().compareTo("")) != 0) {
                variationPair = new Pair<>(messageContent.getVariation().getSign(), messageContent.getVariation().getValue());
            }
            Subscription subscription = new Subscription(
                    companyPair,
                    valuePair,
                    dropPair,
                    variationPair,
                    pair
            );

            if (routingTable.containsKey(source)) {
                routingTable.get(source).add(subscription);
            } else {
                routingTable.put(source, new HashSet<>(Set.of(subscription)));
            }

//            logger.info("Received subscription {}.  New routing table: {}", subscription, routingTable);
        }


        AddressBookProtos.MessageWrapper updatedMessage = deserializedMessage.toBuilder()
                .setSource(getName())
                .build();


        for (var broker : brokers) {
            if (broker.getName().equals(source)) continue; // Do not send the message back to the source
            broker.sendMessage(updatedMessage.toByteArray());
        }

        if (type.equals(PUBLICATION_TYPE)) {
            for (var subscriber : subscribers) {

                if (!routingTable.containsKey(subscriber.getName())) {
                    continue;
                }

                var subscriptions = routingTable.get(subscriber.getName());
                var messageContent = deserializedMessage.getPublication();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                java.sql.Date date = null;

                try {
                    var tmpDate = dateFormat.parse((String) messageContent.getDate());
                    date = new java.sql.Date(tmpDate.getTime());
                } catch (Exception e) {
                    logger.error("Something went wrong: " + e);
                }
                var publication = new Publication(
                        messageContent.getCompany(),
                        messageContent.getValue(),
                        messageContent.getDrop(),
                        messageContent.getVariation(),
                        date
                );


                for (var subscription : subscriptions) {
                    if (isPubMatchedBySub(publication, subscription)) {
                    subscriber.sendMessage(updatedMessage.toByteArray());
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

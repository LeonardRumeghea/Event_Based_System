package ebs;

import ebs.communication.RabbitQueue;
import ebs.communication.entities.Broker;
import ebs.communication.entities.Publisher;
import ebs.communication.entities.Subscriber;
import ebs.communication.helpers.QueueNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ebs.communication.entities.Constants.NUMBER_OF_PUBLISHERS;
import static ebs.communication.entities.Constants.ROOT_QUEUE_NAME;

public class Main {

    public static void main(String[] args) {

        var namesFetcher = new QueueNames();
        namesFetcher.fetchQueues();
        var brokers = namesFetcher.getBrokers();
        var subs = namesFetcher.getSubs();

//        Create the brokers and start them
        var brokersList = getBrokers(brokers, subs);
        brokersList.forEach(RabbitQueue::receiveMessage);

//        Create the subscribers and start them
        var subToBrokerMap = brokersList.stream()
                .collect(Collectors.toMap(
                        Broker::getName,
                        broker -> broker.getSubs().stream().map(RabbitQueue::getName).collect(Collectors.toList())
                ));

        var subscribers = getSubscribers(subToBrokerMap);
        subscribers.forEach(RabbitQueue::receiveMessage);
//        subscribers.forEach(Subscriber::generateSubscriptions);


//       Create the publishers and start them
        startPublishers();
    }

    private static @Unmodifiable List<Broker> getBrokers(@NotNull List<String> brokers, @NotNull List<String> subs) {
        var splitIndex = new Random().nextInt() % 2 + 1;
        return List.of(
//                Route broker
                new Broker(
                        brokers.get(0),  // Broker name
                        Arrays.asList(brokers.get(1), brokers.get(2)),   // Broker neighbours
                        new ArrayList<>()  // Subscribers
                ),
//                Up broker
                new Broker(
                        brokers.get(1),  // Broker name
                        Collections.singletonList(brokers.get(0)),  // Broker neighbours
                        new ArrayList<>(subs.subList(0, splitIndex))  // Subscribers
                ),
//                Down broker
                new Broker(
                        brokers.get(2),  // Broker name
                        Collections.singletonList(brokers.get(0)), // Broker neighbours
                        new ArrayList<>(subs.subList(splitIndex, 3)) // Subscribers
                )
        );
    }

    private static void startPublishers() {
        List<Thread> tasks = Stream.generate(() -> new Publisher(ROOT_QUEUE_NAME))
                .limit(NUMBER_OF_PUBLISHERS)
                .peek(Thread::start)
                .collect(Collectors.toList());

        tasks.forEach(task -> {
            try {
                task.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static @NotNull List<Subscriber> getSubscribers(@NotNull Map<String, List<String>> subToBrokerMap) {

        System.out.println("[!] Subscribers to brokers map: " + subToBrokerMap);

        List<Subscriber> result = new ArrayList<>();
        for (var entry : subToBrokerMap.entrySet()) {
            var brokerName = entry.getKey();
            var subNames = entry.getValue();
            for (var name : subNames) {

                System.out.println("[+] Creating subscriber: " + name + " for broker: " + brokerName);

                result.add(new Subscriber(name, brokerName));
            }
        }

        return result;
    }
}

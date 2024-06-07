package ebs;

import ebs.communication.entities.Broker;
import ebs.communication.entities.Publisher;
import ebs.communication.helpers.QueueNames;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {

        var namesFetcher = new QueueNames();
        namesFetcher.fetchQueues();
        var brokers = namesFetcher.getBrokers();
        var subs = namesFetcher.getSubs();

        var splitIndex = new Random().nextInt() % 2 + 1;

//        Create the brokers chain configuration
        var routeBroker = new Broker(
                brokers.get(0),  // Broker name
                Arrays.asList(brokers.get(1), brokers.get(2)),   // Broker neighbours
                new ArrayList<>()  // Subscribers
        );
        var upBroker = new Broker(
                brokers.get(1),  // Broker name
                Collections.singletonList(brokers.get(0)),  // Broker neighbours
                new ArrayList<>(subs.subList(0, splitIndex))  // Subscribers
        );
        var downBroker = new Broker(
                brokers.get(2),  // Broker name
                Collections.singletonList(brokers.get(0)), // Broker neighbours
                new ArrayList<>(subs.subList(splitIndex, 3)) // Subscribers
        );

//        Start the brokers
        routeBroker.receiveMessage();
        upBroker.receiveMessage();
        downBroker.receiveMessage();

//       Create the publishers and start them
        List<Thread> tasks = Stream.generate(() -> new Publisher("broker_1"))
                .limit(2)
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
}

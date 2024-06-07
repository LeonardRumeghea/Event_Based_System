package ebs;

import ebs.communication.entities.Broker;
import ebs.communication.entities.Publisher;
import ebs.communication.helpers.QueueNames;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
//        var config = new RabbitMqConfigBuilder()
//                .username("guest")
//                .password("guest")
//                .host("localhost")
//                .port(5672)
//                .queueName("sub_1")
//                .routingKey("sub_1_routing")
//                .exchange("sub_1_exchange").build();
//
//        var rabbit = new Broker(config);
//        rabbit.init();
//
////        var rabbit1 = new RabbitMq(config);
////        rabbit1.init();
//
//
//        for (int msgIndex = 0; msgIndex < 10; msgIndex++) {
//            var sub = new Subscription("Costel", 10, 10.0);
//            rabbit.sendMessage(sub.toJson());
////            var sub1 = new Subscription("Mircea", 10, 10.0);
////            rabbit1.sendMessage(sub1.toJson());
//        }
//
//        rabbit.receiveMessage();
////        rabbit1.receiveMessage();
//
//
////        var rabbit1 = new RabbitMq(config);
////        var subscriptionString1 = rabbit.receiveMessage();
//
////        System.out.println("Received: " + subscription);
//
//        System.out.println("Done");
//        var a = new QueueNames();
//        a.fetchQueues();
//        var brokers = a.getBrokers();

        var namesFetcher = new QueueNames();
        namesFetcher.fetchQueues();
        var brokers = namesFetcher.getBrokers();
        var subs = namesFetcher.getSubs();

        var random = new Random();
        var upBrokerNeighboursCount = random.nextInt() % 2 + 1;
        var downBrokerNeighboursCount = 3 - upBrokerNeighboursCount;

        var routeBroker = new Broker(brokers.get(0), Arrays.asList(brokers.get(1), brokers.get(2)), new ArrayList<>());
        routeBroker.receiveMessage();

        var upClients = new ArrayList<String>();
        for (var index = 0; index < upBrokerNeighboursCount; index++) {
            upClients.add(subs.getFirst());
            subs.removeFirst();
        }

        var downClients = new ArrayList<String>();
        for (var index = 0; index < downBrokerNeighboursCount; index++) {
            downClients.add(subs.getFirst());
            subs.removeFirst();
        }

        var upBroker = new Broker(brokers.get(1), Collections.singletonList(brokers.get(0)), upClients);
        upBroker.receiveMessage();


        var downBroker = new Broker(brokers.get(2), Collections.singletonList(brokers.get(0)), downClients);
        downBroker.receiveMessage();

        List<Thread> tasks = new ArrayList<>();
        for (var threadIndex = 0; threadIndex < 2; threadIndex++) {
            var task = new Publisher("broker_1");
            task.start();
            tasks.add(task);
        }


        for (var task : tasks) {
            task.join();
        }
    }
}
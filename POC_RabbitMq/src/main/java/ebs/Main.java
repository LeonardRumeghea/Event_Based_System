package ebs;

import ebs.communication.entities.Publisher;

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
        var task = new Publisher("broker_1");
        task.start();
        task.join();
    }
}
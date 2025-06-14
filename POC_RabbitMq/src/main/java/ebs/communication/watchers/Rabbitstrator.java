package ebs.communication.watchers;

import ch.qos.logback.core.db.dialect.SybaseSqlAnywhereDialect;
import com.rabbitmq.client.ConnectionFactory;
import ebs.Main;
import ebs.communication.RabbitQueue;
import ebs.communication.entities.Broker;
import ebs.communication.helpers.RabbitMqConfig;

import java.nio.channels.Channel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ebs.communication.helpers.Tools.*;

public class Rabbitstrator extends Thread {

    private final List<String> queues;

    private final Map<String, List<Long>> oldBrokerTimestamps;

    public Rabbitstrator(List<String> queues) {
        this.queues = queues;
        this.oldBrokerTimestamps = new java.util.HashMap<>();

        for (var queue : queues) {
            oldBrokerTimestamps.put(queue, new ArrayList<>());
        }

        initBrokersTimestamps(queues);
    }

    @Override
    public void run() {
        waitForPublishers();

        checkForBrokenBroker();
    }

    private void checkForBrokenBroker() {

        System.out.println("[Rabbitstrator] Starting to check for broken brokers 🕵️‍♂️");

        while (true) {
            for (var queue : queues) {

                var newValue = brokerTimestamps.get(queue);
                var oldTimestamps = oldBrokerTimestamps.get(queue);

                oldTimestamps.addLast(newValue);
                if (oldTimestamps.size() > 64) {
                    oldTimestamps.removeFirst();
                }
                
                if (oldTimestamps.stream().distinct().count() == 1 && newValue != 0) {

                    RabbitMqConfig config = brokersBackup
                            .stream()
                            .filter(e -> e.getName().equals(queue))
                            .findFirst()
                            .get()
                            .getConfig();

                    try {
                        ConnectionFactory factory = new ConnectionFactory();
                        factory.setUsername(config.getUsername());
                        factory.setPassword(config.getPassword());
                        factory.setHost(config.getHost());
                        factory.setPort(config.getPort());

                        var connection = factory.newConnection();
                        var channel = connection.createChannel();

                        var count = channel.messageCount(queue);

                        if (count == 0) {
                            connection.close();
                            continue;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("[Rabbitstrator] " + queue + " is broken 😟. Trying to restart..");

                    for (var broker : brokersBackup) {
                        if (broker.getName().equals(queue)) {

                            var neighborNames = broker.getBrokers().stream().map(RabbitQueue::getName).toList();
                            var subNames = broker.getSubscribers().stream().map(RabbitQueue::getName).toList();

                            Broker newBroker = new Broker(broker.getName(), neighborNames, subNames, false, 999_999_999);
                            newBroker.setRoutingTable(broker.getRoutingTable());
                            newBroker.setFilterToSources(broker.getFilterToSources());

                            var publicationNumber = Main.brokersList
                                    .stream()
                                    .filter(e -> e.getName().equals(queue))
                                    .findFirst()
                                    .get()
                                    .getPublicationNumber();

                            newBroker.setPublicationNumber(publicationNumber);

                            newBroker.receiveMessage();

                            System.out.println("[Rabbitstrator] " + queue + " is restarted! 😊");
                            return;
                        }
                    }
                }
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void waitForPublishers() {
        while (true) {
            boolean ready = true;
            for (var queue : queues)
            {

//                System.out.println("[Rabbitstrator] " + queue + " count: " + brokerTimestamps.get(queue));

                var newValue = brokerTimestamps.get(queue);
                var oldTimestamps = oldBrokerTimestamps.get(queue);

                oldTimestamps.addLast(newValue);
                if (oldTimestamps.size() > 16) {
                    oldTimestamps.removeFirst();
                }

                if (oldTimestamps.stream().distinct().count() != 1 || newValue == 0) {
                    ready = false;
                    break;
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (ready) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                String time = dtf.format(java.time.LocalTime.now());
                String date = java.time.LocalDate.now().toString();

                System.out.println(date + " " + time + " [Rabbitstrator] All queues are ready. Starting publishers.");
                Main.saveBrokersBackup();

                Main.startPublishers();

                System.out.println(date + " " + time + " [Rabbitstrator] All publishers are started.");

                return;
            }
        }
    }
}

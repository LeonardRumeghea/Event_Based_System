package ebs.communication.entities;

import ebs.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static ebs.communication.helpers.Tools.brokerTimestamps;
import static ebs.communication.helpers.Tools.initBrokersTimestamps;

public class Rabbitstrator extends Thread {

    private final List<String> queues;

    private final Map<String, List<AtomicLong>> oldBrokerTimestamps;

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
        while (true) {
            for (var queue : queues) {

                var newValue = brokerTimestamps.get(queue);
                var oldTimestamps = oldBrokerTimestamps.get(queue);

                oldTimestamps.addLast(newValue);
                if (oldTimestamps.size() > 16) {
                    oldTimestamps.removeFirst();
                }

                if (oldTimestamps.stream().distinct().count() != 1) {
                    //System.out.println("[Rabbitstrator] " + queue + " is broken 😟. Trying to restart..");

                    resetBroker(queue);
                    break;
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
            for (var queue : queues) {

                System.out.println("[Rabbitstrator] " + queue + " count: " + brokerTimestamps.get(queue));

                var newValue = brokerTimestamps.get(queue);
                var oldTimestamps = oldBrokerTimestamps.get(queue);

                oldTimestamps.addLast(newValue);
                if (oldTimestamps.size() > 8) {
                    oldTimestamps.removeFirst();
                }

                if (oldTimestamps.stream().distinct().count() != 1 || newValue.get() == 0) {
                    ready = false;
                    break;
                }
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (ready) {
                System.out.println("[Rabbitstrator] All queues are ready. Starting publishers.");
                Main.startPublishers();
                break;
            }
        }
    }

    private void resetBroker(String queue) {

    }
}

package ebs.communication.entities;

import ebs.Main;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ebs.communication.helpers.Tools.brokerTimestamps;
import static ebs.communication.helpers.Tools.initBrokersTimestamps;

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
        while (true) {
            for (var queue : queues) {

                var newValue = brokerTimestamps.get(queue);
                var oldTimestamps = oldBrokerTimestamps.get(queue);

                oldTimestamps.addLast(newValue);
                if (oldTimestamps.size() > 16) {
                    oldTimestamps.removeFirst();
                }

                if (oldTimestamps.stream().distinct().count() == 1) {
//                    System.out.println("[Rabbitstrator] " + queue + " is broken 😟. Trying to restart..");

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
                Main.startPublishers();
                break;
            }
        }
    }

    private void resetBroker(String queue) {

    }
}

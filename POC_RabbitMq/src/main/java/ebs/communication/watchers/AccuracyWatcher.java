package ebs.communication.watchers;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ebs.communication.helpers.Tools.*;
public class AccuracyWatcher extends Thread{
    private final List<String> subs;
    private final List<String> brokers;
    private final Map<String,Integer> oldReceivedPubs;
    private final Map<String,Integer> oldBrokerCounters;

    public AccuracyWatcher(List<String> subs, List<String> brokers){
        this.subs=subs;
        this.brokers=brokers;
        this.oldBrokerCounters = new HashMap<>();
        this.oldReceivedPubs = new HashMap<>();
        for (String sub : subs){
            oldReceivedPubs.put(sub, 0);
        }
        for (String broker : brokers){
            oldBrokerCounters.put(broker, 0);
        }
    }

    @Override
    public void run() {
        waitForBrokersAndSubs();
    }

    private void waitForBrokersAndSubs() {
        while (true) {
            boolean brokersReady = true;
            boolean subsReady = true;
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (String broker:brokers){
                //System.out.println("[BrokerWatcher] " + broker +" publications received: "+ oldCounters.get(broker));

                int newCount = brokerPubs.getOrDefault(broker,0);
                int oldCount = oldBrokerCounters.get(broker);

                oldBrokerCounters.put(broker, newCount);
                if(newCount!=oldCount){
                    brokersReady=false;
                }
            }

            for (String sub: subs){

                int newCount = receivedPubs.get(sub);
                int oldCount = oldReceivedPubs.get(sub);
                oldReceivedPubs.put(sub, newCount);

                if (newCount != oldCount){
                    subsReady = false;
                }
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            String time = dtf.format(java.time.LocalTime.now());
            String date = java.time.LocalDate.now().toString();
            if (subsReady && brokersReady){
                for (String sub: subs){
                    double accuracy = (double) receivedPubs.get(sub) / brokerPubs.get(brokers.getFirst());
                    System.out.printf(date + " " + time + " [AccuracyWatcher] %s matching rate: %.2f%%\n", sub, accuracy * 100);
                }
                break;
            }
        }
    }
}

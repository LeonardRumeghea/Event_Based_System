package ebs.communication.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ebs.communication.helpers.Tools.initBrokerPubs;
import static ebs.communication.helpers.Tools.brokerPubs;

public class BrokerWatcher extends Thread{
    private final List<String> brokers;
    private final Map<String,Integer> oldCounters;
    public BrokerWatcher(List<String> brokers){
        this.brokers=brokers;
        this.oldCounters = new HashMap<>();

        for (String broker : brokers){
            oldCounters.put(broker, 0);
        }
        initBrokerPubs(brokers);
    }
    @Override
    public void run() {
        waitForPubs();
    }

    private void waitForPubs(){
        while (true) {
            boolean ready = true;
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (String broker:brokers){
                //System.out.println("[BrokerWatcher] " + broker +" publications received: "+ oldCounters.get(broker));

                int newCount = brokerPubs.get(broker);
                int oldCount = oldCounters.get(broker);

                oldCounters.put(broker, newCount);
                if(newCount!=oldCount){
                    ready=false;
                }
            }
            if (ready){
                System.out.println("[BrokerWatcher] Finished work for brokers");
                for (String broker:brokers){
                    System.out.println("[BrokerWatcher] " + broker +" publications received: "+ brokerPubs.get(broker));
                }
                break;
            }
        }
    }
}

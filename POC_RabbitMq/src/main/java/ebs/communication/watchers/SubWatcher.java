package ebs.communication.watchers;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ebs.communication.helpers.Tools.*;

public class SubWatcher extends Thread {
    private final List<String> subs;
    private final Map<String,Double> oldLatencies;
    private final Map<String,Integer> oldReceivedPubs;

    public SubWatcher(List<String> subs){
        this.subs=subs;
        this.oldLatencies = new HashMap<>();
        this.oldReceivedPubs = new HashMap<>();
        for (String sub : subs){
            oldLatencies.put(sub, (double)0);
            oldReceivedPubs.put(sub, 0);
        }
        initReceivedPubs(subs);
        initAverageLatency(subs);
    }

    @Override
    public void run() {
        waitForSubs();
    }

    private void waitForSubs(){
        while (true) {
            boolean ready = true;
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (String sub: subs){
                //System.out.println("[SubWatcher] " + sub +" publications received: "+ oldReceivedPubs.get(sub) + " with an average latency: " + oldLatencies.get(sub));

                int newCount = receivedPubs.get(sub);
                int oldCount = oldReceivedPubs.get(sub);
                oldReceivedPubs.put(sub, newCount);

                double newLatency = averageLatency.get(sub);
                double oldLatency = oldLatencies.get(sub);
                oldLatencies.put(sub, newLatency);

                if (newCount != oldCount && newLatency!=oldLatency){
                    ready = false;
                }
            }

            if (ready){
                for (String sub: subs){
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String time = dtf.format(java.time.LocalTime.now());
                    String date = java.time.LocalDate.now().toString();

                    System.out.printf(date + " " + time + " [SubWatcher] %s publications received: %d with an average latency: %.2f ms\n", sub, oldReceivedPubs.get(sub), oldLatencies.get(sub));
                }
                break;
            }
        }
    }
}

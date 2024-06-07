package ebs.communication.entities;

import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.List;
import java.util.stream.Collectors;

public class Broker extends RabbitQueue {

    List<RabbitQueue> brokers;
    List<RabbitQueue> subs;

    public Broker(String brokerQueue, List<String> brokerQueues, List<String> subQueues) {
        super(Tools.getConfigFor(brokerQueue));
        brokers = brokerQueues.stream()
                .map(e -> new RabbitQueue(Tools.getConfigFor(e)))
                .collect(Collectors.toList());

        subs = subQueues.stream()
                .map(e -> new RabbitQueue(Tools.getConfigFor(e)))
                .collect(Collectors.toList());
    }

    @Override
    public void callback(String message) {
        System.out.println(message);
        JSONObject json = new JSONObject(message);

        String source = (String) json.getJSONArray("source").get(0);
        json.remove("source");
        json.append("source", getName());
        for (var broker : brokers) {
            if (broker.getName().equals(source)) {
                continue;
            }

            broker.sendMessage(json.toString());
        }

//        for (var sub : subs) {
//            sub.sendMessage(json.toString());
//        }
    }
}

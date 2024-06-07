package ebs.communication.entities;

import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class Broker extends RabbitQueue {

    List<RabbitQueue> brokers;
    List<RabbitQueue> subs;

    public Broker(String brokerQueue, @NotNull List<String> brokerQueues, @NotNull List<String> subQueues) {
        super(Tools.getConfigFor(brokerQueue));
        brokers = brokerQueues.stream()
                .map(e -> new RabbitQueue(Tools.getConfigFor(e)))
                .collect(Collectors.toList());

        subs = subQueues.stream()
                .map(e -> new RabbitQueue(Tools.getConfigFor(e)))
                .collect(Collectors.toList());
    }

//    This method is called when a message is received by the broker. It forwards the message to all other brokers and subs
    @Override
    public void callback(String message) {
        JSONObject jsonObject = new JSONObject(message);
        String source = jsonObject.getString("source");
        jsonObject.put("source", getName());

//        Send the message to all brokers and subs except the source
        for (var broker : brokers) {
            if (broker.getName().equals(source)) {
                continue;
            }
            broker.sendMessage(jsonObject.toString());
        }

        for (var sub : subs) {
            sub.sendMessage(jsonObject.toString());
        }
    }
}

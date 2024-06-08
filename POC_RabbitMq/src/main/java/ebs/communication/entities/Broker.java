package ebs.communication.entities;

import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ebs.communication.entities.Constants.PUBLICATION_TYPE;
import static ebs.communication.entities.Constants.SUBSCRIPTION_TYPE;

public class Broker extends RabbitQueue {

    private final List<RabbitQueue> brokers;
    @Getter
    private final List<RabbitQueue> subs;

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

        String type = jsonObject.getString("type");

        for (var broker : brokers) {
            if (broker.getName().equals(source)) continue; // Do not send the message back to the source

            broker.sendMessage(jsonObject.toString());
        }

        if (type.equals(PUBLICATION_TYPE)) {
            for (var sub : subs) {
                sub.sendMessage(jsonObject.toString());
            }
        }
    }

    @Override
    public String toString() {
        return "Broker{" +
                "name='" + getName() + '\'' +
                ", brokers=" + brokers.stream().map(RabbitQueue::getName).toString() +
                ", subs=" + subs.stream().map(RabbitQueue::getName).toString() +
                '}';
    }
}

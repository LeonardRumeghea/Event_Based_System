package ebs.communication.entities;

import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import org.json.JSONObject;

import static ebs.communication.entities.Constants.SUBSCRIPTION_TYPE;

public class Subscriber  extends RabbitQueue {

    private final RabbitQueue broker;
    private final String name;

    public Subscriber(String name, String brokerName) {
        super(Tools.getConfigFor(name));
        this.broker = new RabbitQueue(Tools.getConfigFor(brokerName));
        this.name = name;
    }

    @Override
    public void callback(String message) {
        JSONObject jsonObject = new JSONObject(message);
        String source = jsonObject.getString("source");
        String messageContent = jsonObject.getString("message");
    }

    public void subscribe() {
        JSONObject jsonObject = new JSONObject()
                .put("type", SUBSCRIPTION_TYPE)
                .put("source", name)
                .put("message", "Hello, I am a subscriber 😁");

        broker.sendMessage(jsonObject.toString());
    }

    public void generateSubscriptions() {
        for (int i = 0; i < 10; i++) {
            subscribe();
        }
    }
}

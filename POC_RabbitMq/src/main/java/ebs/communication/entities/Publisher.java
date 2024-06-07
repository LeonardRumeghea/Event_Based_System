package ebs.communication.entities;

import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import org.json.JSONObject;

public class Publisher extends Thread  {
    private RabbitQueue broker;

    public Publisher(String brokerName) {
        super();
        this.broker = new RabbitQueue(Tools.getConfigFor(brokerName));
    }

    @Override
    public void run() {
        for (int i = 0; i< 10; i++) {
            var obj = new JSONObject()
                    .append("message", "testulescu")
                    .append("source", "madagascar");

            this.broker.sendMessage(obj.toString());
        }
    }
}

package ebs.communication.entities;

import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import org.json.JSONObject;

import static ebs.communication.entities.Constants.PUBLICATION_TYPE;

public class Publisher extends Thread  {
    private final RabbitQueue broker;

    public Publisher(String brokerName) {
        super();
        this.broker = new RabbitQueue(Tools.getConfigFor(brokerName));
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {

//          Generate a random publication and send it to the broker queue

            JSONObject jsonObj = new JSONObject()
                    .put("source", "publisher")
                    .put("type", PUBLICATION_TYPE)
                    .put("message", "Do you wanna some 🍺?");

            this.broker.sendMessage(jsonObj.toString());
        }
    }
}

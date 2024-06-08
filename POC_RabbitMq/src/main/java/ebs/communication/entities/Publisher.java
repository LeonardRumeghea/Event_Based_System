package ebs.communication.entities;

import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import ebs.generator.DBGenerator;
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

        int numberOfPublications = 10;
        var publications = DBGenerator.generatePublicationsList(numberOfPublications);

        for (var pub : publications) {

            JSONObject jsonObject = new JSONObject()
                    .put("type", PUBLICATION_TYPE)
                    .put("source", "publisher")
                    .put("message", pub.toJson().toString());

            this.broker.sendMessage(jsonObject.toString());
        }
    }
}

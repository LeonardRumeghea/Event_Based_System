package ebs.communication.entities;

import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import ebs.generator.DBGenerator;
import lombok.Getter;
import org.json.JSONObject;

import static ebs.communication.entities.Constants.SUBSCRIPTION_TYPE;

public class Subscriber extends RabbitQueue {

    private final RabbitQueue broker;
    @Getter
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
//        int companyEqualSign = 15;
//        int numberOfSubscriptions = 25;
//        int totalFields = 60;
//        int nrCompany = 20;
//        int nrValue = 15;
//        int nrDrop = 10;
//        int nrVariation = 10;
//        int nrDate = 5;

        int companyEqualSign = 2;
        int numberOfSubscriptions = 3;
        int totalFields = 8;
        int nrCompany = 3;
        int nrValue = 2;
        int nrDrop = 1;
        int nrVariation = 0;
        int nrDate = 2;


        var subscriptions = DBGenerator.generateSubscriptionsList(companyEqualSign, numberOfSubscriptions, totalFields, nrCompany, nrValue, nrDrop, nrVariation, nrDate);

        for (var sub : subscriptions) {
            JSONObject jsonObject = new JSONObject()
                    .put("type", SUBSCRIPTION_TYPE)
                    .put("source", name)
                    .put("message", sub.toJson().toString());

//            System.out.println(jsonObject.toString());

            broker.sendMessage(jsonObject.toString());
        }
    }
}

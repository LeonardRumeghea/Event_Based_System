package ebs.communication.entities;

import com.google.protobuf.InvalidProtocolBufferException;
import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import ebs.generator.DBGenerator;
import ebs.generator.entities.Publication;
import ebs.generator.entities.Subscription;
import lombok.Getter;
import org.example.protobuf.AddressBookProtos;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

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
    public void callback(byte[] message) {
        AddressBookProtos.MessageWrapper deserializedMessage = null;
        try {
            deserializedMessage = AddressBookProtos.MessageWrapper.parseFrom(message);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }

        var messageContent = deserializedMessage.getPublication();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        java.sql.Date date = null;

        try {
            var tmpDate = dateFormat.parse((String) messageContent.getDate());
            date = new java.sql.Date(tmpDate.getTime());
        } catch (Exception e) {
            logger.error("Something went wrong: " + e);
        }
        var publication = new Publication(
                messageContent.getCompany(),
                messageContent.getValue(),
                messageContent.getDrop(),
                messageContent.getVariation(),
                date
        );
        System.out.println("I found publication " + publication);
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

        int companyEqualSign = 3;
        int numberOfSubscriptions = 3;
        int totalFields = 3;
        int nrCompany = 3;
        int nrValue = 0;
        int nrDrop = 0;
        int nrVariation = 0;
        int nrDate = 0;


        ArrayList<Subscription> subscriptions = DBGenerator.generateSubscriptionsList(companyEqualSign, numberOfSubscriptions, totalFields, nrCompany, nrValue, nrDrop, nrVariation, nrDate);

        ArrayList<AddressBookProtos.MessageWrapper> protoMessages = new ArrayList<>();
        for (Subscription sub : subscriptions){
            AddressBookProtos.Subscription.Builder protoSubBuilder = AddressBookProtos.Subscription.newBuilder();
            if(sub.getVariation() != null){
                protoSubBuilder.setVariation(AddressBookProtos.SubscriptionFieldFloat.newBuilder()
                        .setSign(sub.getVariation().getFirst())
                        .setValue(sub.getVariation().getSecond()));
            }
            if(sub.getValue() != null){
                protoSubBuilder.setValue(AddressBookProtos.SubscriptionFieldFloat.newBuilder()
                        .setSign(sub.getValue().getFirst())
                        .setValue(sub.getValue().getSecond()));
            }
            if(sub.getDrop() != null){
                protoSubBuilder.setDrop(AddressBookProtos.SubscriptionFieldFloat.newBuilder()
                        .setSign(sub.getDrop().getFirst())
                        .setValue(sub.getDrop().getSecond()));
            }
            if(sub.getDate() != null){
                protoSubBuilder.setDate(AddressBookProtos.SubscriptionFieldString.newBuilder()
                        .setSign(sub.getDate().getFirst())
                        .setValue(String.valueOf(sub.getDate().getSecond())));
            }
            if(sub.getCompany() != null){
                protoSubBuilder.setCompany(AddressBookProtos.SubscriptionFieldString.newBuilder()
                        .setSign(sub.getCompany().getFirst())
                        .setValue(sub.getCompany().getSecond()));
            }
            AddressBookProtos.Subscription protoSub = protoSubBuilder.build();

            protoMessages.add(AddressBookProtos.MessageWrapper.newBuilder()
                    .setType(SUBSCRIPTION_TYPE)
                    .setSource(name)
                    .setTimestamp(System.currentTimeMillis())
                    .setSubscription(protoSub)
                    .build());
            //System.out.println(sub.getVariation());
        }
//
        for (AddressBookProtos.MessageWrapper message: protoMessages){
            byte[] serializedMessage = message.toByteArray();
            broker.sendMessage(serializedMessage);

//            AddressBookProtos.MessageWrapper deserializedMessage = null;
//            try {
//                deserializedMessage = AddressBookProtos.MessageWrapper.parseFrom(serializedMessage);
//            } catch (InvalidProtocolBufferException e) {
//                throw new RuntimeException(e);
//            }

//            System.out.println("Message company: " + deserializedMessage.getSubscription().getCompany());
//            System.out.println("Company: " + message.getSubscription().getCompany());
//            System.out.println("Date: " + message.getSubscription().getDate());
//            System.out.println("Drop: " + message.getSubscription().getDrop());
//            System.out.println("Value: " + message.getSubscription().getValue());
//            System.out.println("Variation: " + message.getSubscription().getVariation());
        }


        for (Subscription sub : subscriptions) {
            JSONObject jsonObject = new JSONObject()
                    .put("type", SUBSCRIPTION_TYPE)
                    .put("source", name)
                    .put("message", sub.toJson().toString());

            //System.out.println(jsonObject.toString());
            //System.out.println(jsonObject.getString("source"));
            //broker.sendMessage(jsonObject.toString());
        }
    }
}

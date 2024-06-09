package ebs.communication.entities;

import com.google.protobuf.InvalidProtocolBufferException;
import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import ebs.generator.DBGenerator;
import ebs.generator.entities.Publication;
import ebs.generator.entities.Subscription;
import lombok.Getter;
import org.example.protobuf.AddressBookProtos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static ebs.communication.entities.Constants.SUBSCRIPTION_TYPE;

public class Subscriber extends RabbitQueue {

    private final RabbitQueue broker;
    @Getter
    private final String name;

    public Subscriber(String name, String brokerName) {
        super(Tools.getConfigFor(name), true);
        this.broker = new RabbitQueue(Tools.getConfigFor(brokerName), true);
        this.name = name;
    }

    @Override
    public void callback(byte[] message) {
        try {
            AddressBookProtos.MessageWrapper deserializedMessage = AddressBookProtos.MessageWrapper.parseFrom(message);
            var messageContent = deserializedMessage.getPublication();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date tmpDate = dateFormat.parse(messageContent.getDate());
            java.sql.Date date = new java.sql.Date(tmpDate.getTime());

            var publication = new Publication(
                messageContent.getCompany(),
                messageContent.getValue(),
                messageContent.getDrop(),
                messageContent.getVariation(),
                date
            );

        } catch (InvalidProtocolBufferException | ParseException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("Something went wrong: {}", e.getMessage());
        }
    }

    public void generateSubscriptions() {
//        int companyEqualSign = 2;
//        int numberOfSubscriptions = 5;
//        int totalFields = 10;
//        int nrCompany = 4;
//        int nrValue = 3;
//        int nrDrop = 3;
//        int nrVariation = 0;
//        int nrDate = 0;

        int companyEqualSign = 2500;
        int numberOfSubscriptions = 10_000;
        int totalFields = 17_000;
        int nrCompany = 8000;
        int nrValue = 6000;
        int nrDrop = 2000;
        int nrVariation = 1000;
        int nrDate = 0;

        ArrayList<Subscription> subscriptions = DBGenerator.generateSubscriptionsList(companyEqualSign, numberOfSubscriptions, totalFields, nrCompany, nrValue, nrDrop, nrVariation, nrDate);

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

            var resultSub = AddressBookProtos.MessageWrapper.newBuilder()
                    .setType(SUBSCRIPTION_TYPE)
                    .setSource(name)
                    .setTimestamp(System.currentTimeMillis())
                    .setSubscription(protoSubBuilder.build())
                    .build()
                    .toByteArray();

            broker.sendMessage(resultSub);
        }
    }
}

package ebs.communication.entities;

import com.google.protobuf.InvalidProtocolBufferException;
import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import ebs.generator.DBGenerator;
import ebs.generator.entities.Subscription;
import lombok.Getter;
import org.example.protobuf.AddressBookProtos;

import java.util.ArrayList;

import static ebs.communication.entities.Constants.*;
import static ebs.communication.helpers.Tools.averageLatency;
import static ebs.communication.helpers.Tools.receivedPubs;

public class Subscriber extends RabbitQueue {

    private final RabbitQueue broker;
    @Getter
    private final String name;
    private double latency;
    private int publicationsNumber;

    public Subscriber(String name, String brokerName) {
        super(Tools.getConfigFor(name), true);
        this.broker = new RabbitQueue(Tools.getConfigFor(brokerName), true);
        this.name = name;
        this.latency=0;
        this.publicationsNumber=0;
    }

    @Override
    public void callback(byte[] message) {
        try {
            AddressBookProtos.MessageWrapper deserializedMessage = AddressBookProtos.MessageWrapper.parseFrom(message);

            this.publicationsNumber ++;
            this.latency = this.latency + ((System.currentTimeMillis()-deserializedMessage.getTimestamp() - this.latency) / this.publicationsNumber);

            receivedPubs.put(getName(), this.publicationsNumber);
            averageLatency.put(getName(), this.latency);

        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("Something went wrong: {}", e.getMessage());
        }
    }

    public void generateSubscriptions() {
//        int companyEqualSign = 2;
//        int numberOfSubscriptions = 5;
//        int totalFields = 13;
//        int nrCompany = 4;
//        int nrValue = 3;
//        int nrDrop = 3;
//        int nrVariation = 3;
//        int nrDate = 0;

//        int companyEqualSign = 125;
//        int numberOfSubscriptions = 500;
//        int totalFields = 2500;
//        int nrCompany = 500;
//        int nrValue = 500;
//        int nrDrop = 500;
//        int nrVariation = 500;
//        int nrDate = 500;

        int totalFields = NUMBER_OF_COMPANIES + NUMBER_OF_VALUES + NUMBER_OF_DROPS + NUMBER_OF_VARIATIONS + NUMBER_OF_DATES;

        ArrayList<Subscription> subscriptions = DBGenerator.generateSubscriptionsList(
                NUMBER_OF_EQUAL_SIGNS,
                NUMBER_OF_SUBSCRIPTIONS,
                totalFields,
                NUMBER_OF_COMPANIES,
                NUMBER_OF_VALUES,
                NUMBER_OF_DROPS,
                NUMBER_OF_VARIATIONS,
                NUMBER_OF_DATES);

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

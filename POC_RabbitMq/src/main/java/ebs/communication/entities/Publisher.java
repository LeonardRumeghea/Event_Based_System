package ebs.communication.entities;

import com.google.protobuf.InvalidProtocolBufferException;
import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import ebs.generator.DBGenerator;
import ebs.generator.entities.Publication;
import org.example.protobuf.AddressBookProtos;
import org.json.JSONObject;

import java.util.ArrayList;

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
        ArrayList<Publication> publications = DBGenerator.generatePublicationsList(numberOfPublications);

        ArrayList<AddressBookProtos.MessageWrapper> protoMessages = new ArrayList<>();
        for (Publication pub : publications) {
            AddressBookProtos.Publication.Builder protoPubBuilder = AddressBookProtos.Publication.newBuilder();
            protoPubBuilder.setDrop(pub.getDrop());
            protoPubBuilder.setValue(pub.getValue());
            protoPubBuilder.setVariation(pub.getVariation());
            protoPubBuilder.setDate(String.valueOf(pub.getDate()));
            protoPubBuilder.setCompany(pub.getCompany());

            AddressBookProtos.Publication protoPub = protoPubBuilder.build();
            protoMessages.add(AddressBookProtos.MessageWrapper.newBuilder()
                    .setTimestamp(System.currentTimeMillis())
                    .setSource("publisher")
                    .setType(PUBLICATION_TYPE)
                    .setPublication(protoPub)
                    .build());
        }
        for (AddressBookProtos.MessageWrapper message: protoMessages){
            byte[] serializedMessage = message.toByteArray();
            broker.sendMessage(serializedMessage);

//            AddressBookProtos.MessageWrapper deserializedMessage = null;
//            try {
//                deserializedMessage = AddressBookProtos.MessageWrapper.parseFrom(serializedMessage);
//            } catch (InvalidProtocolBufferException e) {
//                throw new RuntimeException(e);
//            }

        }


        for (Publication pub : publications) {

            JSONObject jsonObject = new JSONObject()
                    .put("type", PUBLICATION_TYPE)
                    .put("source", "publisher")
                    .put("message", pub.toJson().toString());
            //System.out.println("!!!!!!!!!" + jsonObject.toString());
            //this.broker.sendMessage(jsonObject.toString());
        }
    }
}

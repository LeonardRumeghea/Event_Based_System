package ebs.communication.entities;

import ebs.communication.RabbitQueue;
import ebs.communication.helpers.Tools;
import ebs.generator.DBGenerator;
import org.example.protobuf.AddressBookProtos;

import java.sql.Date;
import java.time.format.DateTimeFormatter;

import static ebs.communication.entities.Constants.PUBLICATION_TYPE;

public class Publisher extends Thread  {
    private final RabbitQueue broker;

    public Publisher(String brokerName) {
        super();
        this.broker = new RabbitQueue(Tools.getConfigFor(brokerName), true);
    }

    @Override
    public void run() {

//        var executionTime = 3 * 60 * 1_000;  // ms
        // 3 minutes for the two publishers to finish publishing
        var executionTime = 60 * 1_000;  // ms

        Date stopDate = new Date(System.currentTimeMillis() + executionTime);

        while (System.currentTimeMillis() < stopDate.getTime()) {
            var pub = DBGenerator.generatePublication();

            AddressBookProtos.Publication.Builder protoPubBuilder = AddressBookProtos.Publication.newBuilder();
            protoPubBuilder.setDrop(pub.getDrop());
            protoPubBuilder.setValue(pub.getValue());
            protoPubBuilder.setVariation(pub.getVariation());
            protoPubBuilder.setDate(String.valueOf(pub.getDate()));
            protoPubBuilder.setCompany(pub.getCompany());

            var resultPub = AddressBookProtos.MessageWrapper.newBuilder()
                    .setTimestamp(System.currentTimeMillis())
                    .setSource("publisher")
                    .setType(PUBLICATION_TYPE)
                    .setPublication(protoPubBuilder.build())
                    .build()
                    .toByteArray();

            broker.sendMessage(resultPub);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = dtf.format(java.time.LocalTime.now());
        String date = java.time.LocalDate.now().toString();

        System.out.println(date + " " + time + " [Publisher] I finished publishing messages.");
    }
}

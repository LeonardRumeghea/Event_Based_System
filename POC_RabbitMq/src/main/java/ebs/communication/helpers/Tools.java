package ebs.communication.helpers;

import ebs.generator.entities.Pair;
import ebs.generator.entities.Publication;
import ebs.generator.entities.Subscription;
import org.example.protobuf.AddressBookProtos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static ebs.communication.entities.Constants.*;

public class Tools {

    public static Map<String, AtomicLong> brokerTimestamps;

    public static RabbitMqConfig getConfigFor(String name) {
        return new RabbitMqConfigBuilder()
                .username(USERNAME)
                .password(PASSWORD)
                .host(IP)
                .port(PORT)
                .queueName(name)
                .routingKey(name + "_routing")
                .exchange(name + "_exchange")
                .build();

    }

    public static Date parseDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            if (dateString.compareTo("") != 0) {
                return new Date(dateFormat.parse(dateString).getTime());
            }
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }

        return null;
    }

    public static void initBrokersTimestamps(List<String> brokers) {
        brokerTimestamps = new HashMap<>();
        for (var broker : brokers) {
            brokerTimestamps.put(broker, new AtomicLong(0));
        }
    }

    @Contract("_ -> new")
    public static @NotNull Subscription getSubFromContent(AddressBookProtos.@NotNull Subscription messageContent) {

        Pair<String, String> companyPair = null;
        Pair<String, Float> valuePair = null;
        Pair<String, Float> dropPair = null;
        Pair<String, Float> variationPair = null;
        Pair<String, java.sql.Date> datePair = null;

        if ((messageContent.getCompany().getSign().compareTo("")) != 0) {
            companyPair = new Pair<>(messageContent.getCompany().getSign(), messageContent.getCompany().getValue());
        }

        if ((messageContent.getValue().getSign().compareTo("")) != 0) {
            valuePair = new Pair<>(messageContent.getValue().getSign(), messageContent.getValue().getValue());
        }

        if ((messageContent.getDrop().getSign().compareTo("")) != 0) {
            dropPair = new Pair<>(messageContent.getDrop().getSign(), messageContent.getDrop().getValue());
        }

        if ((messageContent.getVariation().getSign().compareTo("")) != 0) {
            variationPair = new Pair<>(messageContent.getVariation().getSign(), messageContent.getVariation().getValue());
        }

        java.sql.Date date = parseDate(messageContent.getDate().getValue());
        if (date != null) {
            datePair = new Pair<>(messageContent.getDate().getSign(), date);
        }

        return new Subscription(companyPair, valuePair, dropPair, variationPair, datePair);
    }

    public static @NotNull Publication getPubFromContent(AddressBookProtos.@NotNull Publication messageContent) {
        java.sql.Date date = parseDate(messageContent.getDate());
        return new Publication(messageContent.getCompany(), messageContent.getValue(), messageContent.getDrop(), messageContent.getVariation(), date);
    }

    private static boolean compare(String sign, String value1, String value2) {
        return switch (sign) {
            case "<" -> value1.compareTo(value2) < 0;
            case "<=" -> value1.compareTo(value2) <= 0;
            case "=" -> value1.compareTo(value2) == 0;
            case "!" -> value1.compareTo(value2) != 0;
            case ">=" -> value1.compareTo(value2) >= 0;
            case ">" -> value1.compareTo(value2) > 0;
            default -> false;
        };
    }

    private static boolean compare(String sign, Float value1, Float value2) {
        return switch (sign) {
            case "<" -> value1 < value2;
            case "<=" -> value1 <= value2;
            case "=" -> value1.compareTo(value2) == 0;
            case "!" -> value1.compareTo(value2) != 0;
            case ">=" -> value1 >= value2;
            case ">" -> value1 > value2;
            default -> false;
        };
    }

    private static boolean compare(String sign, Date value1, Date value2) {
        return switch (sign) {
            case "<" -> value1.compareTo(value2) < 0;
            case "<=" -> value1.compareTo(value2) <= 0;
            case "=" -> value1.compareTo(value2) == 0;
            case "!" -> value1.compareTo(value2) != 0;
            case ">=" -> value1.compareTo(value2) >= 0;
            case ">" -> value1.compareTo(value2) > 0;
            default -> false;
        };
    }

    public static boolean isPubMatchedBySub(Publication pub, Subscription sub) {
        if (sub.isAttributeSet(1) && !compare(sub.getCompany().getFirst(), pub.getCompany(), sub.getCompany().getSecond())) {
            return false;
        }

        if (sub.isAttributeSet(2) && !compare(sub.getValue().getFirst(), pub.getValue(), sub.getValue().getSecond())) {
            return false;
        }

        if (sub.isAttributeSet(3) && !compare(sub.getDrop().getFirst(), pub.getDrop(), sub.getDrop().getSecond())) {
            return false;
        }

        if (sub.isAttributeSet(4) && !compare(sub.getVariation().getFirst(), pub.getVariation(), sub.getVariation().getSecond())) {
            return false;
        }

        if (sub.isAttributeSet(5) && !compare(sub.getDate().getFirst(), pub.getDate(), sub.getDate().getSecond())) {
            return false;
        }

        return true;
    }
}

package ebs.communication.helpers;

import ebs.generator.entities.Publication;
import ebs.generator.entities.Subscription;

import java.sql.Date;
import java.util.Objects;

import static ebs.communication.entities.Constants.*;

public class Tools {
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

package org.generator;


import org.json.JSONArray;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.generator.Constants.*;
import static org.generator.Constants.NUMBER_OF_SUBSCRIPTIONS;

public class Main {

    static int calculatePercentage(int part, int whole) {
        return (int) Math.ceil((double) part / 100 * whole);
    }

    public static void main(String[] args) {

        int nrCompany = calculatePercentage(COMPANY_PERCENTAGE, NUMBER_OF_SUBSCRIPTIONS);
        int nrValue = calculatePercentage(VALUE_PERCENTAGE, NUMBER_OF_SUBSCRIPTIONS);
        int nrDrop = calculatePercentage(DROP_PERCENTAGE, NUMBER_OF_SUBSCRIPTIONS);
        int nrVariation = calculatePercentage(VARIATION_PERCENTAGE, NUMBER_OF_SUBSCRIPTIONS);
        int nrDate = calculatePercentage(DATA_PERCENTAGE, NUMBER_OF_SUBSCRIPTIONS);

        int totalFields = nrCompany + nrValue + nrDrop + nrVariation + nrDate;

        System.out.println("Number of companies: " + nrCompany);
        System.out.println("Number of values: " + nrValue);
        System.out.println("Number of drops: " + nrDrop);
        System.out.println("Number of variations: " + nrVariation);
        System.out.println("Number of dates: " + nrDate);
        System.out.println("Total number of fields: " + totalFields);

        DBGenerator generator = new DBGenerator();
        JSONArray jsonArray;

        try {
            ArrayList<Subscription> sub_list = generator.generateSubscriptions(totalFields, nrCompany, nrValue, nrDrop, nrVariation, nrDate);
            jsonArray = new JSONArray(sub_list.stream().map(Subscription::toJson).collect(Collectors.toList()));
            Files.write(Path.of(SUBSCRIPTIONS_OUTPUT_FILE), jsonArray.toString(2).getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            ArrayList<Publication> pub_list = generator.generatePublications();
            jsonArray = new JSONArray(pub_list.stream().map(Publication::toJson).collect(Collectors.toList()));
            Files.write(Path.of(PUBLICATIONS_OUTPUT_FILE), jsonArray.toString(2).getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
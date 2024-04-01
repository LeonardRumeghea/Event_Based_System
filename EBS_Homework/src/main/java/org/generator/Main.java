package org.generator;


import org.json.JSONArray;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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

        // Creating the threads for publications
        ArrayList<Integer> pubThreadValues = generator.splitValue(NUMBER_OF_PUBLICATIONS, NUMBER_OF_THREADS);
        //System.out.println(pubThreadValues);
        List<MultithreadedPubGeneration> pubTasks = new ArrayList<>();
        for(int i=0; i<NUMBER_OF_THREADS;i++){
            pubTasks.add(new MultithreadedPubGeneration(generator, pubThreadValues.get(i)));
        }
        long pubStartTime = System.currentTimeMillis();
        for(int i=0;i<NUMBER_OF_THREADS;i++){
            pubTasks.get(i).start();
        }
        try {
            for(int i=0;i<NUMBER_OF_THREADS;i++) {
                pubTasks.get(i).join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long pubEndTime = System.currentTimeMillis();
        long pubCreationTime = pubEndTime - pubStartTime;

        ArrayList<Publication> publicationsList = new ArrayList<>();
        for(int i=0;i<NUMBER_OF_THREADS;i++) {
            publicationsList.addAll(pubTasks.get(i).pubList);
        }

        // Creating the threads for subscriptions
        ArrayList<ArrayList<Integer>> threadValues = generator.divideWorkForThreads(NUMBER_OF_THREADS, NUMBER_OF_SUBSCRIPTIONS, nrCompany, nrValue, nrDrop, nrVariation, nrDate);
        List<MultithreadedSubGeneration> tasks = new ArrayList<>();
        for(int i=0; i<NUMBER_OF_THREADS;i++){
            ArrayList<Integer> threadValue = threadValues.get(i);
            tasks.add(new MultithreadedSubGeneration(i,generator, threadValue.get(0), threadValue.get(1), threadValue.get(2), threadValue.get(3), threadValue.get(4), threadValue.get(5), threadValue.get(6), threadValue.get(7)));
        }
        long subStartTime = System.currentTimeMillis();
        for(int i=0;i<NUMBER_OF_THREADS;i++){
            tasks.get(i).start();
        }
        try {
            for(int i=0;i<NUMBER_OF_THREADS;i++) {
                tasks.get(i).join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long subEndTime = System.currentTimeMillis();
        long subCreationTime = subEndTime - subStartTime;
        System.out.println("Creation time with " + NUMBER_OF_THREADS + " threads: " + (subCreationTime + pubCreationTime) + " ms");
        ArrayList<Subscription> SubscriptionsList = new ArrayList<>();
        for(int i=0;i<NUMBER_OF_THREADS;i++) {
            SubscriptionsList.addAll(tasks.get(i).subList);
        }
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(SubscriptionsList.stream().map(Subscription::toJson).collect(Collectors.toList()));
            Files.write(Path.of(SUBSCRIPTIONS_OUTPUT_FILE), jsonArray.toString(2).getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            jsonArray = new JSONArray(publicationsList.stream().map(Publication::toJson).collect(Collectors.toList()));
            Files.write(Path.of(PUBLICATIONS_OUTPUT_FILE), jsonArray.toString(2).getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


//        long startTime2 = System.currentTimeMillis();
//        ArrayList<Subscription> sub_list = generator.generateSubscriptions(COMPANY_PERCENTAGE_EQUAL_SIGN, NUMBER_OF_SUBSCRIPTIONS, totalFields, nrCompany, nrValue, nrDrop, nrVariation, nrDate);
//        long endTime2 = System.currentTimeMillis();
//        System.out.println("Creation time without threads: " + (endTime2 - startTime2) + " ms");
        //System.out.println(sub_list);

    }
}
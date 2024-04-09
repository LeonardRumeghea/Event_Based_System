package org.generator;

import org.generator.entities.Publication;
import org.generator.entities.Subscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.generator.entities.Constants.*;
import static org.generator.entities.Constants.NUMBER_OF_SUBSCRIPTIONS;

public class Main {

    static void generateSubscriptionsWithThreads(int nrCompany, int nrValue, int nrDrop, int nrVariation, int nrDate)
            throws IOException, InterruptedException {

        DBGenerator generator = new DBGenerator();
        ArrayList<ArrayList<Integer>> threadValues = Utils.divideWorkForThreads(NUMBER_OF_THREADS, NUMBER_OF_SUBSCRIPTIONS, nrCompany, nrValue, nrDrop, nrVariation, nrDate);
        List<SubGeneratorThread> tasks = new ArrayList<>();

        for(int threadIdx = 0; threadIdx < NUMBER_OF_THREADS; threadIdx++){
            ArrayList<Integer> threadValue = threadValues.get(threadIdx);
            tasks.add(
                    new SubGeneratorThread(
                            generator,
                            threadValue.get(0),
                            threadValue.get(1),
                            threadValue.get(2),
                            threadValue.get(3),
                            threadValue.get(4),
                            threadValue.get(5),
                            threadValue.get(6),
                            threadValue.get(7),
                            threadIdx
                    )
            );
        }
//        Start the timer for the threads
        long startTime = System.currentTimeMillis();
        tasks.forEach(Thread::start);

        for (SubGeneratorThread task : tasks) {
            task.join();
        }

//        Stop the timer for the threads
        long endTime = System.currentTimeMillis();

        Utils.timeLogger(endTime - startTime, "subscriptions", true);

        List<Subscription> SubscriptionsList = tasks
                .stream()
                .map(task -> task.subList)
                .flatMap(List::stream)
                .toList();

//        Utils.writeToJsonFile(
//                SUBSCRIPTIONS_OUTPUT_FILE,
//                    SubscriptionsList
//                            .stream()
//                            .map(Subscription::toJson)
//                            .collect(Collectors.toList())
//        );
    }

    static void generateSubscriptionsWithoutThreads(int nrCompany, int nrValue, int nrDrop, int nrVariation, int nrDate, int totalFields)
            throws IOException {

        DBGenerator generator = new DBGenerator();
//        Start the timer for the creation of the subscriptions without threads
        long startTime = System.currentTimeMillis();
        ArrayList<Subscription> sub_list = generator.generateSubscriptions(COMPANY_PERCENTAGE_EQUAL_SIGN, NUMBER_OF_SUBSCRIPTIONS, totalFields, nrCompany, nrValue, nrDrop, nrVariation, nrDate);

//        Stop the timer for the creation of the subscriptions without threads
        long endTime = System.currentTimeMillis();
        Utils.timeLogger(endTime - startTime, "subscriptions", false);

        Utils.writeToJsonFile(
                SUBSCRIPTIONS_OUTPUT_FILE,
                sub_list
                        .stream()
                        .map(Subscription::toJson)
                        .collect(Collectors.toList())
        );
    }

    static void generatePublicationsWithThreads() throws IOException, InterruptedException {
        DBGenerator generator = new DBGenerator();
        ArrayList<Integer> pubThreadValues = Utils.splitValue(NUMBER_OF_PUBLICATIONS, NUMBER_OF_THREADS);
        List<PubGeneratorThread> tasks = new ArrayList<>();

        for (int threadIdx = 0; threadIdx < NUMBER_OF_THREADS; threadIdx++) {
            tasks.add(new PubGeneratorThread(generator, pubThreadValues.get(threadIdx)));
        }

        long startTime = System.currentTimeMillis();
        tasks.forEach(Thread::start);

        for (PubGeneratorThread task : tasks) {
            task.join();
        }

        long endTime = System.currentTimeMillis();
        Utils.timeLogger(endTime - startTime, "publications", true);

        List<Publication> PublicationsList = tasks
                .stream()
                .map(task -> task.pubList)
                .flatMap(List::stream)
                .toList();

//        Utils.writeToJsonFile(
//                PUBLICATIONS_OUTPUT_FILE,
//                PublicationsList
//                        .stream()
//                        .map(Publication::toJson)
//                        .collect(Collectors.toList())
//        );
    }

    static void generatePublicationsWithoutThreads() throws IOException {
        DBGenerator generator = new DBGenerator();
//        Start the timer for the creation of the publications without threads
        long startTime = System.currentTimeMillis();
        ArrayList<Publication> pub_list = generator.generatePublications(NUMBER_OF_PUBLICATIONS);

//        Stop the timer for the creation of the publications without threads
        long endTime = System.currentTimeMillis();

        Utils.timeLogger(endTime - startTime, "publications", false);

        Utils.writeToJsonFile(
                PUBLICATIONS_OUTPUT_FILE,
                pub_list
                        .stream()
                        .map(Publication::toJson)
                        .collect(Collectors.toList())
        );
    }

    public static void main(String[] args) {

        int nrCompany =   Utils.calculatePercentage(COMPANY_PERCENTAGE,   NUMBER_OF_SUBSCRIPTIONS);
        int nrValue =     Utils.calculatePercentage(VALUE_PERCENTAGE,     NUMBER_OF_SUBSCRIPTIONS);
        int nrDrop =      Utils.calculatePercentage(DROP_PERCENTAGE,      NUMBER_OF_SUBSCRIPTIONS);
        int nrVariation = Utils.calculatePercentage(VARIATION_PERCENTAGE, NUMBER_OF_SUBSCRIPTIONS);
        int nrDate =      Utils.calculatePercentage(DATA_PERCENTAGE,      NUMBER_OF_SUBSCRIPTIONS);

        int totalFields = nrCompany + nrValue + nrDrop + nrVariation + nrDate;

        System.out.println("Number of: ----------------------------------------------------------------------");
        System.out.println("Companies: " + nrCompany);
        System.out.println("Values: " + nrValue);
        System.out.println("Drops: " + nrDrop);
        System.out.println("Variations: " + nrVariation);
        System.out.println("Dates: " + nrDate);
        System.out.println("> Generated fields: " + totalFields);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Subscriptions: " + NUMBER_OF_SUBSCRIPTIONS);
        System.out.println("Publications: " + NUMBER_OF_PUBLICATIONS);
        System.out.println("Threads: " + NUMBER_OF_THREADS);
        System.out.println("----------------------------------------------------------------------");

        try {
            generateSubscriptionsWithThreads(nrCompany, nrValue, nrDrop, nrVariation, nrDate);
            generateSubscriptionsWithoutThreads(nrCompany, nrValue, nrDrop, nrVariation, nrDate, totalFields);

            generatePublicationsWithThreads();
            generatePublicationsWithoutThreads();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

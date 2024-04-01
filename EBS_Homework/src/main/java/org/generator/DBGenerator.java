package org.generator;

import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

import static org.generator.Constants.*;

public class DBGenerator {

    public DBGenerator() { }

    public int equalSignCalculator(int numberOfSubs){
        return (int) Math.ceil((double) EQUAL_SIGHT_FREQUENCY / 100 * Math.ceil((double) COMPANY_PERCENTAGE / 100 * numberOfSubs));
    }

    public ArrayList<Integer> splitValue(int number, int nrThreads){
        ArrayList<Integer> dividedTasks = new ArrayList<>();
        while(nrThreads>1){
            int split = number/nrThreads;
            dividedTasks.add(split);
            number-=split;
            nrThreads--;
        }
        dividedTasks.add(number);
        return dividedTasks;
    }

    public ArrayList<ArrayList<Integer>> divideWorkForThreads(int nrThreads, int totalSubs, int nrCompany, int nrValue, int nrDrop, int nrVariation, int nrDate){
        // Split the number of fields for each category based on the number of threads
        ArrayList<Integer> dividedSubs = splitValue(totalSubs, nrThreads);
        ArrayList<Integer> dividedCompany = splitValue(nrCompany, nrThreads);
        ArrayList<Integer> dividedValue = splitValue(nrValue, nrThreads);
        ArrayList<Integer> dividedDrop = splitValue(nrDrop, nrThreads);
        ArrayList<Integer> dividedVariation = splitValue(nrVariation, nrThreads);
        ArrayList<Integer> dividedDate = splitValue(nrDate, nrThreads);
        ArrayList<Integer> companyPercentageEqualSign = new ArrayList<>();
        ArrayList<ArrayList<Integer>> threadValues = new ArrayList<>();
        // Compute the number of fields that must have the "=" sign
        for(Integer subs : dividedSubs){
            companyPercentageEqualSign.add(equalSignCalculator(subs));
        }
        // Create a list for each thread with the number of fields that it must create
        for(int i=0; i<nrThreads;i++){
            if (dividedCompany.get(i)<companyPercentageEqualSign.get(i)){
                companyPercentageEqualSign.set(i, dividedCompany.get(i));
            }
            int totalFields = dividedCompany.get(i) + dividedValue.get(i) + dividedDrop.get(i) + dividedVariation.get(i) + dividedDate.get(i);
            threadValues.add(new ArrayList<>(Arrays.asList(companyPercentageEqualSign.get(i), dividedSubs.get(i), totalFields, dividedCompany.get(i), dividedValue.get(i), dividedDrop.get(i),dividedVariation.get(i), dividedDate.get(i))));
        }
        return threadValues;
    }

    private int getValidAttribute(@NotNull ArrayList<Integer> counts, Subscription subscription) {

        if (counts.getFirst() == 0) {
            return -1;
        }

        ArrayList<Integer> attributes = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        Collections.shuffle(attributes);

        for (Integer idx : attributes) {
            if (counts.get(idx) != 0 && !subscription.isAttributeSet(idx)) {
                return idx;
            }
        }

        return -1;
    }

    private boolean populateSubsRandom(@NotNull ArrayList<Integer> counts, Subscription subscription, ArrayList<String> companiesSigns) {

        int atr = getValidAttribute(counts, subscription);
        if (atr == -1) { return false; }

        switch (atr) {
            case 1:
                subscription.setCompany(companiesSigns.getFirst());
                companiesSigns.removeFirst();
                break;
            case 2: subscription.setValue(); break;
            case 3: subscription.setDrop(); break;
            case 4: subscription.setVariation(); break;
            case 5: subscription.setDate(); break;
            default: break;
        }

        counts.set(atr, counts.get(atr) - 1);

        return true;
    }

    public ArrayList<Subscription> generateSubscriptions(int companyEqualSign, int numberOfSubscriptions, int totalFields, int nrCompany, int nrValue, int nrDrop, int nrVariation, int nrDate){

//        Contains the number of each type of field that needs to be generated
        ArrayList<Integer> counts = new ArrayList<>(Arrays.asList(totalFields, nrCompany, nrValue, nrDrop, nrVariation, nrDate));

//        Contains the list of subscriptions that will be generated
        ArrayList<Subscription> listOfSubscriptions = IntStream.range(0, numberOfSubscriptions)
                .collect(ArrayList::new, (list, i) -> list.add(new Subscription()), ArrayList::addAll);

//        Contains the list of companies signs that will be used. The list is shuffled to ensure randomness in the distribution
        ArrayList<String> companiesSigns = IntStream.range(0, companyEqualSign)
                .collect(ArrayList::new, (list, i) -> list.add("="), ArrayList::addAll);
        companiesSigns.addAll(IntStream.range(0, nrCompany - companyEqualSign)
                .collect(ArrayList::new, (list, i) -> list.add("!="), ArrayList::addAll));
        Collections.shuffle(companiesSigns);

        int idx = 0;
        boolean result;
        while(counts.getFirst() != 0) {

            result = populateSubsRandom(counts, listOfSubscriptions.get(idx), companiesSigns);

            idx = (idx + 1) % numberOfSubscriptions;

            if (result) counts.set(0, counts.getFirst() - 1);
        }

        Collections.shuffle(listOfSubscriptions);
        return listOfSubscriptions;
    }

    public ArrayList<Publication> generatePublications(int numberOfPublications) {

        ArrayList<Publication> listOfPublications = IntStream.range(0, numberOfPublications)
                .collect(ArrayList::new, (list, i) -> list.add(new Publication()), ArrayList::addAll);

           for (Publication publication : listOfPublications) {
                publication.setCompany(COMPANIES.get((int) (Math.random() * COMPANIES.size())));
                publication.setValue((float) (Math.random() * 1000));
                publication.setDrop((float) (Math.random() * 25));
                publication.setVariation((float) (Math.random() * 100 - 50));
                publication.setDate(new Date(System.currentTimeMillis() - (long) (Math.random() * 100) * 24 * 60 * 60 * 1000));
           }

        return listOfPublications;
    }
}

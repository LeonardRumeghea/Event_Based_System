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

    public ArrayList<Subscription> generateSubscriptions(int totalFields, int nrCompany, int nrValue, int nrDrop, int nrVariation, int nrDate){

//        Contains the number of each type of field that needs to be generated
        ArrayList<Integer> counts = new ArrayList<>(Arrays.asList(totalFields, nrCompany, nrValue, nrDrop, nrVariation, nrDate));

//        Contains the list of subscriptions that will be generated
        ArrayList<Subscription> listOfSubscriptions = IntStream.range(0, NUMBER_OF_SUBSCRIPTIONS)
                .collect(ArrayList::new, (list, i) -> list.add(new Subscription()), ArrayList::addAll);

//        Contains the list of companies signs that will be used. The list is shuffled to ensure randomness in the distribution
        ArrayList<String> companiesSigns = IntStream.range(0, COMPANY_PERCENTAGE_EQUAL_SIGN)
                .collect(ArrayList::new, (list, i) -> list.add("="), ArrayList::addAll);
        companiesSigns.addAll(IntStream.range(0, nrCompany - COMPANY_PERCENTAGE_EQUAL_SIGN)
                .collect(ArrayList::new, (list, i) -> list.add("!="), ArrayList::addAll));
        Collections.shuffle(companiesSigns);

        int idx = 0;
        boolean result;
        while(counts.getFirst() != 0) {

            result = populateSubsRandom(counts, listOfSubscriptions.get(idx), companiesSigns);

            idx = (idx + 1) % NUMBER_OF_SUBSCRIPTIONS;

            if (result) counts.set(0, counts.getFirst() - 1);
        }

        Collections.shuffle(listOfSubscriptions);
        return listOfSubscriptions;
    }

    public ArrayList<Publication> generatePublications() {

        ArrayList<Publication> listOfPublications = IntStream.range(0, NUMBER_OF_PUBLICATIONS)
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

package ebs.generator;

import lombok.NoArgsConstructor;
import ebs.generator.entities.Publication;
import ebs.generator.entities.Subscription;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

import static ebs.generator.entities.Constants.*;

@NoArgsConstructor
public class DBGenerator {

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

    private boolean populateSubscriptionRandom(@NotNull ArrayList<Integer> counts, Subscription subscription, ArrayList<String> companiesSigns) {

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
            result = populateSubscriptionRandom(counts, listOfSubscriptions.get(idx), companiesSigns);
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
                publication.setCompany(Utils.generateRandomCompany());
                publication.setValue(Utils.generateRandomFloat(MIN_VALUE, MAX_VALUE));
                publication.setDrop(Utils.generateRandomFloat(MIN_DROP, MAX_DROP));
                publication.setVariation(Utils.generateRandomFloat(MIN_VARIATION, MAX_VARIATION));
                publication.setDate(Utils.generateRandomDate(NUMBER_OF_DAYS_AGO));
           }

        return listOfPublications;
    }
}

package ebs.generator;

import ebs.generator.entities.Constants;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    static int calculatePercentage(int part, int whole) {
        return (int) Math.ceil((double) part / 100 * whole);
    }

    public static float generateRandomFloat(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }

    @Contract("_ -> new")
    public static @NotNull Date generateRandomDate(int days) {
        return new Date(System.currentTimeMillis() - (long) (Math.random() * days) * 24 * 60 * 60 * 1000);
    }

    public static String generateRandomSign(int lowerBound, int upperBound) {
        return Constants.COMPARISON_SIGNS.get((int) (Math.random() * (upperBound - lowerBound) + lowerBound));
    }

    public static String generateRandomCompany() {
        return Constants.COMPANIES.get((int) (Math.random() * Constants.COMPANIES.size()));
    }

    public static void timeLogger(long time, String method, boolean isThread){
        System.out.println("Creation time with " + (isThread ? "threads" : "no threads") + " for " + method + ":\t " + time + " ms");
    }

    public static void writeToJsonFile(String path, List<JSONObject> jsonObjects) throws IOException {
        JSONArray jsonArray = new JSONArray(jsonObjects);
        Files.write(Path.of(path), jsonArray.toString(2).getBytes());
    }

    public static int equalSignCalculator(int numberOfSubs){
        return (int) Math.ceil((double) Constants.EQUAL_SIGHT_FREQUENCY / 100 * Math.ceil((double) Constants.COMPANY_PERCENTAGE / 100 * numberOfSubs));
    }

    public static @NotNull ArrayList<Integer> splitValue(int number, int nrThreads){
        ArrayList<Integer> dividedTasks = new ArrayList<>();
        int split;
        while(nrThreads > 1){
            split = number / nrThreads;
            dividedTasks.add(split);
            number -= split;
            nrThreads--;
        }
        dividedTasks.add(number);
        return dividedTasks;
    }

    public static @NotNull ArrayList<ArrayList<Integer>> divideWorkForThreads(int nrThreads, int totalSubs, int nrCompany, int nrValue, int nrDrop, int nrVariation, int nrDate){
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
        int totalFields;
        for(int i = 0; i < nrThreads; i++){
            if (dividedCompany.get(i) < companyPercentageEqualSign.get(i)){
                companyPercentageEqualSign.set(i, dividedCompany.get(i));
            }
            totalFields = dividedCompany.get(i) + dividedValue.get(i) + dividedDrop.get(i) + dividedVariation.get(i) + dividedDate.get(i);
            threadValues.add(
                    new ArrayList<>(
                            Arrays.asList(
                                    companyPercentageEqualSign.get(i),
                                    dividedSubs.get(i),
                                    totalFields,
                                    dividedCompany.get(i),
                                    dividedValue.get(i),
                                    dividedDrop.get(i),
                                    dividedVariation.get(i),
                                    dividedDate.get(i)
                            )
                    )
            );
        }
        return threadValues;
    }
}

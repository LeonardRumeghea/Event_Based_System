package org.example;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
    // Change all these variables to be read from keyboard
    public static List<String> comparisonSigns = new ArrayList<>(Arrays.asList("=", "!=", ">", ">=", "<", "<="));
    public static List<String> companies = new ArrayList<>(Arrays.asList("Apple", "Google", "Microsoft", "Amazon", "Facebook", "Netflix", "Tesla", "IBM", "Oracle", "Intel"));
    public static int numberOfSubscriptions = 10;
    public static int numberOfPublications = 10;
    public static int companyPercentage = 60;
    public static int valuePercentage = 30;
    public static int dropPercentage = 40;
    public static int variationPercentage = 0;
    public static int datePercentage = 0;
    public static int equalSightFreq = 70;
    public static int companyPercentageEqualSign = (int) Math.ceil((double) equalSightFreq / 100 * Math.ceil((double) companyPercentage / 100 * numberOfSubscriptions));

    public static void main(String[] args) {
        /*
        List<String> test = null;
        Subscription subscription = new Subscription();
        List<String> company = new ArrayList<>(Arrays.asList("!=", "google"));
        List<String> value = new ArrayList<>(Arrays.asList(">=", "30"));
        List<String> variation = new ArrayList<>(Arrays.asList("<", "0.8"));
        subscription.setCompany(company);
        subscription.setValue(value);
        subscription.setVariation(variation);
        JSONObject jsonTest= subscription.subToJson();
        System.out.println(jsonTest);
        */
        //System.out.println(subscription.getVariation());
        DBGenerator db = new DBGenerator();
        ArrayList<Subscription> sub_list = db.generateSubsStructure();
        for (Subscription subscription : sub_list) {
            System.out.println(subscription);
        }

    }
}
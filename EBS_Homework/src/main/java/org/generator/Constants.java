package org.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {
    public static List<String> COMPARISON_SIGNS = new ArrayList<>(Arrays.asList("=", "!=", ">", ">=", "<", "<="));
    public static List<String> COMPANIES = new ArrayList<>(Arrays.asList("Apple", "Google", "Microsoft", "Amazon", "Facebook", "Netflix", "Tesla", "IBM", "Oracle", "Intel"));
    public static int NUMBER_OF_SUBSCRIPTIONS = 100;
    public static int NUMBER_OF_PUBLICATIONS = 25;
    public static int COMPANY_PERCENTAGE = 60;
    public static int VALUE_PERCENTAGE = 60;
    public static int DROP_PERCENTAGE = 40;
    public static int VARIATION_PERCENTAGE = 50;
    public static int DATA_PERCENTAGE = 30;
    public static int EQUAL_SIGHT_FREQUENCY = 70;
    public static int COMPANY_PERCENTAGE_EQUAL_SIGN = (int) Math.ceil((double) EQUAL_SIGHT_FREQUENCY / 100 * Math.ceil((double) COMPANY_PERCENTAGE / 100 * NUMBER_OF_SUBSCRIPTIONS));

    public static String PUBLICATIONS_OUTPUT_FILE = "publications.json";
    public static String SUBSCRIPTIONS_OUTPUT_FILE = "subscriptions.json";
}

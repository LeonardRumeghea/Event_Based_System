package ebs.generator.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {
    public static List<String> COMPARISON_SIGNS = new ArrayList<>(Arrays.asList("=", "!=", ">", ">=", "<", "<="));
    public static List<String> COMPANIES =
            new ArrayList<>(Arrays.asList("Apple", "Google", "Microsoft", "Amazon", "Facebook", "Netflix", "Tesla", "IBM",
                    "Oracle", "Intel", "Cisco", "HP", "Dell", "Nvidia", "AMD", "Qualcomm", "Adobe", "Paypal", "Salesforce",
                    "VMware", "Twitter", "Uber", "Airbnb", "Spotify", "Snapchat", "Pinterest", "Dropbox", "Slack", "Lyft",
                    "Zoom", "TikTok", "Reddit", "Ebay", "Shopify", "Alibaba", "Tencent", "Baidu", "JD.com", "Meituan",
                    "Pinduoduo", "Xiaomi", "Nio", "Li Auto", "Xpeng", "BYD", "Geely", "Volkswagen", "Toyota", "Ford",
                    "General Motors", "BMW", "Mercedes-Benz", "Audi", "Porsche", "Ferrari", "Lamborghini", "Maserati",
                    "Bugatti", "Koenigsegg", "Pagani", "McLaren", "Aston Martin", "Bentley", "Rolls-Royce", "Jaguar",
                    "Land Rover", "Lexus", "Infiniti", "Acura", "Cadillac", "Lincoln", "Buick", "Chevrolet", "GMC",
                    "Dodge", "Jeep", "Chrysler", "Ram", "Fiat", "Alfa Romeo", "Mazda", "Subaru", "Honda", "Nissan",
                    "Mitsubishi", "Kia", "Hyundai", "Genesis", "Volvo", "Polestar", "Saab", "Scania"));

//            new ArrayList<>(Arrays.asList("Apple", "Google"));
    public static int COMPANY_PERCENTAGE = 90;
    public static int EQUAL_SIGHT_FREQUENCY = 70;

    public static String PUBLICATIONS_OUTPUT_FILE = "publications.json";
    public static String SUBSCRIPTIONS_OUTPUT_FILE = "subscriptions.json";

//    Random generation boundaries

    public static int MIN_VALUE = 0;
    public static int MAX_VALUE = 1000;
    public static int MIN_DROP = 0;
    public static int MAX_DROP = 25;
    public static int MIN_VARIATION = -50;
    public static int MAX_VARIATION = 50;
    public static int NUMBER_OF_DAYS_AGO = 100;
}

package org.generator;

import lombok.Getter;
import org.json.JSONObject;

import java.sql.Date;

import static org.generator.Constants.*;

@Getter
public class Subscription {
    private Pair<String, String> company    = null;
    private Pair<String, Float> value       = null;
    private Pair<String, Float> drop        = null;
    private Pair<String, Float> variation   = null;
    private Pair<String, Date> date         = null;

    public Subscription(){}

    public void setCompany(String sign) {
        String company = COMPANIES.get((int) (Math.random() * COMPANIES.size()));

        this.company = new Pair<>(sign, company);
    }

    public void setValue() {
        String sign = COMPARISON_SIGNS.get((int) (Math.random() * 4 + 2));
//        generate random float between 0 and 1000
        Float value = (float) (Math.random() * 1000);

        this.value = new Pair<>(sign, value);
    }

    public void setDrop() {
        String sign = COMPARISON_SIGNS.get((int) (Math.random() * 4 + 2));
//        generate random float between 0 and 25
        Float value = (float) (Math.random() * 25);

        this.drop = new Pair<>(sign, value);
    }

    public void setVariation() {
        String sign = COMPARISON_SIGNS.get((int) (Math.random() * 4 + 2));
//        generate random float between -50 and 50
        Float value = (float) (Math.random() * 100 - 50);

        this.variation = new Pair<>(sign, value);
    }

    public void setDate() {
        String sign = COMPARISON_SIGNS.get((int) (Math.random() * 2));
        //        generate random date between now and 100 days ago
        Date date = new Date(System.currentTimeMillis() - (long) (Math.random() * 100) * 24 * 60 * 60 * 1000);

        this.date = new Pair<>(sign, date);
    }

    public boolean isAttributeSet(int atr){
        return switch (atr) {
            case 1 -> company != null;
            case 2 -> value != null;
            case 3 -> drop != null;
            case 4 -> variation != null;
            case 5 -> date != null;
            default -> false;
        };
    }

    public JSONObject toJson(){
        return new JSONObject() {{
            if (company     != null)  put("company", company.toJson());
            if (value       != null)  put("value", value.toJson());
            if (drop        != null)  put("drop", drop.toJson());
            if (variation   != null)  put("variation", variation.toJson());
            if (date        != null)  put("date", date.toJson());

        }};
    }

    @Override
    public String toString() {
        return toJson().toString(4);
    }
}

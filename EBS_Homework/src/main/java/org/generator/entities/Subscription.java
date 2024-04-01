package org.generator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.generator.Utils;
import org.json.JSONObject;

import java.sql.Date;

import static org.generator.entities.Constants.*;

@Getter
@NoArgsConstructor
public class Subscription {
    private Pair<String, String> company    = null;
    private Pair<String, Float> value       = null;
    private Pair<String, Float> drop        = null;
    private Pair<String, Float> variation   = null;
    private Pair<String, Date> date         = null;

    public void setCompany(String sign) {
        this.company = new Pair<>(sign, Utils.generateRandomCompany());
    }

    public void setValue() {
        this.value = new Pair<>(
                Utils.generateRandomSign(2, 6),
                Utils.generateRandomFloat(MIN_VALUE, MAX_VALUE)
        );
    }

    public void setDrop() {
        this.drop = new Pair<>(
                Utils.generateRandomSign(2, 6),
                Utils.generateRandomFloat(MIN_DROP, MAX_DROP)
        );
    }

    public void setVariation() {
        this.variation = new Pair<>(
                Utils.generateRandomSign(2, 6),
                Utils.generateRandomFloat(MIN_VARIATION, MAX_VARIATION)
        );
    }

    public void setDate() {
        this.date = new Pair<>(
                Utils.generateRandomSign(2, 6),
                Utils.generateRandomDate(NUMBER_OF_DAYS_AGO)
        );
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

package ebs.generator.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ebs.generator.Utils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.sql.Date;

import static ebs.generator.entities.Constants.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    public static @NotNull Subscription fromJson(@NotNull JSONObject json){
        Subscription subscription = new Subscription();
        if (json.has("company")) {
            subscription.company = Pair.fromJson(json.getJSONObject("company"));
        }
        if (json.has("value")) {
            subscription.value = Pair.fromJson(json.getJSONObject("value"));
        }
        if (json.has("drop")) {
            subscription.drop = Pair.fromJson(json.getJSONObject("drop"));
        }
        if (json.has("variation")) {
            subscription.variation = Pair.fromJson(json.getJSONObject("variation"));
        }
        if (json.has("date")) {
            subscription.date = Pair.fromJson(json.getJSONObject("date"));
        }
        return subscription;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public boolean matches(JSONObject jsonObject) {

        Subscription other = Subscription.fromJson(jsonObject);

        if (company != null && !company.equals(other.company)) return false;
        if (value != null && !value.equals(other.value)) return false;
        if (drop != null && !drop.equals(other.drop)) return false;
        if (variation != null && !variation.equals(other.variation)) return false;
        if (date != null && !date.equals(other.date)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Subscription) {
            return this.toString().equals(obj.toString());
        }
        return false;
    }
}

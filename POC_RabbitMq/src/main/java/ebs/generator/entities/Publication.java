package ebs.generator.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Publication {
    private String company;
    private Float value;
    private Float drop;
    private Float variation;
    private Date date;

    public JSONObject toJson(){
        return new JSONObject() {{
            put("company", company);
            put("value", value);
            put("drop", drop);
            put("variation", variation);
            put("date", date);
        }};
    }

    public static Publication fromJson(JSONObject jsonObject) {
        String companyValue = jsonObject.getString("company");
        Float valueValue = (float) jsonObject.getDouble("value");
        Float dropValue = (float) jsonObject.getDouble("drop");
        Float variationValue = (float) jsonObject.getDouble("variation");
        Date dateValue = Date.valueOf(jsonObject.getString("date"));

        return new Publication(companyValue, valueValue, dropValue, variationValue, dateValue);
    }

    @Override
    public String toString() {
        return toJson().toString(2);
    }
}

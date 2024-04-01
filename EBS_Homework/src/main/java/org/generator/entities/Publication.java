package org.generator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
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

    @Override
    public String toString() {
        return toJson().toString(2);
    }
}

package org.generator.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Pair <T1, T2>{
    private T1 first;
    private T2 second;

    public JSONObject toJson(){
        return new JSONObject() {{
            put("sign", first);
            put("value", second);
        }};
    }

    @Override
    public String toString() {
        return toJson().toString(2);
    }
}

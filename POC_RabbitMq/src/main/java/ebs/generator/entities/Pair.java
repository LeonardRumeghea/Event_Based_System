package ebs.generator.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.sql.Date;

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

            if (second instanceof Float) {
                put("value", (float) second);
            } else if (second instanceof Date) {
                put("value", second.toString());
            } else {
                put("value", second);
            }
        }};
    }

    public Pair<T1, T2> fromJson(@NotNull JSONObject jsonObject) {
        T1 firstValue = (T1) jsonObject.get("sign");
        T2 secondValue = (T2) jsonObject.get("value");
        return new Pair<>(firstValue, secondValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Pair pair)) {
            return false;
        }
        return first.equals(pair.first) && second.equals(pair.second);
    }

    @Override
    public String toString() {
        return toJson().toString(2);
    }
}

package ebs.generator.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Pair<T1, T2> {
    private T1 first;
    private T2 second;

    public JSONObject toJson() {
        return new JSONObject() {{
            put("sign", first);
            if (second instanceof Float) {
                put("value", second);
            } else if (second instanceof Date) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                put("value", dateFormat.format((Date) second));
            } else {
                put("value", second);
            }
        }};
    }

    private static boolean isValidDate(String dateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateFormat.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static <T1, T2> Pair<T1, T2> fromJson(@NotNull JSONObject jsonObject) {
        T1 firstValue = (T1) jsonObject.get("sign");

        T2 secondValue;
        var a = jsonObject.get("value");
        if (a instanceof BigDecimal) {
            secondValue = (T2) (Float) ((BigDecimal) a).floatValue();
        } else if (a instanceof String && isValidDate((String) a)) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                secondValue = (T2) dateFormat.parse((String) a);
            } catch (ParseException e) {
                throw new RuntimeException("Failed to parse date", e);
            }
        } else {
            secondValue = (T2) a;
        }

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

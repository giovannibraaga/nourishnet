package backend.nourishnet.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    private static final DateTimeFormatter F = DateTimeFormatter.ISO_LOCAL_DATE;
    @Override public JsonElement serialize(LocalDate src, Type t, JsonSerializationContext c) {
        return new JsonPrimitive(F.format(src));
    }
    @Override public LocalDate deserialize(JsonElement json, Type t, JsonDeserializationContext c)
    { return LocalDate.parse(json.getAsString(), F); }
}

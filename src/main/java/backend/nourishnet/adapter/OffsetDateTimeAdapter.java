package backend.nourishnet.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeAdapter implements JsonSerializer<OffsetDateTime>, JsonDeserializer<OffsetDateTime> {
    private static final DateTimeFormatter F = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public JsonElement serialize(OffsetDateTime src, Type t, JsonSerializationContext c) {
        return new JsonPrimitive(F.format(src));
    }

    @Override
    public OffsetDateTime deserialize(JsonElement json, Type t, JsonDeserializationContext c) {
        return OffsetDateTime.parse(json.getAsString(), F);
    }
}

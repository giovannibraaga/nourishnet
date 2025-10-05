package backend.nourishnet.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;


public class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
    @Override public JsonElement serialize(Instant src, Type t, JsonSerializationContext c) {
        return new JsonPrimitive(src.toString()); // ISO-8601
    }
    @Override public Instant deserialize(JsonElement json, Type t, JsonDeserializationContext c)
    { return Instant.parse(json.getAsString()); }
}
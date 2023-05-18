package net.aesten.werewolfmc.backend;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UnixTimestampTypeAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public JsonElement serialize(Timestamp src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getTime());
    }

    @Override
    public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            long timestampMillis = json.getAsLong();
            return new Timestamp(timestampMillis);
        } catch (NumberFormatException e) {
            try {
                String dateString = json.getAsString();
                Date parsedDate = dateFormat.parse(dateString);
                return new Timestamp(parsedDate.getTime());
            } catch (ParseException ex) {
                throw new JsonParseException("Failed to parse timestamp: " + json.getAsString(), ex);
            }
        }
    }
}
package com.github.frapontillo.pulse.crowd.sentiment.sentit.rest;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author Francesco Pontillo
 */
public class SentitResultMapDeserializer
        implements JsonDeserializer<SentitResponse.SentitResultMap> {
    @Override public SentitResponse.SentitResultMap deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        SentitResponse.SentitResultMap map = new SentitResponse.SentitResultMap();
        JsonArray array = json.getAsJsonArray();
        array.forEach(elem -> {
            JsonObject obj = elem.getAsJsonObject();
            map.put(obj.get("id").getAsString(),
                    new SentitResponse.SentitResult(obj.get("subjectivity").getAsString(),
                            obj.get("polarity").getAsString()));
        });
        return map;
    }
}

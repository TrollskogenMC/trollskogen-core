package com.github.hornta.trollskogen.bans.deserializers;

import com.github.hornta.trollskogen.bans.Ban;
import com.github.hornta.trollskogen.bans.BanManager;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BansDeserializer implements JsonDeserializer<Ban[]> {
  @Override
  public Ban[] deserialize(JsonElement elem, Type type, JsonDeserializationContext jsonDeserializationContext) {
    ArrayList<Ban> bans = new ArrayList<>();
    for(JsonElement json : elem.getAsJsonObject().getAsJsonArray("bans")) {
      bans.add(BanManager.parseBan(json.getAsJsonObject()));
    }

    return bans.toArray(new Ban[0]);
  }
}

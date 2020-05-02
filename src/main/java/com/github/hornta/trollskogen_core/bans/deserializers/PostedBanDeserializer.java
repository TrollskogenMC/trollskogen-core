package com.github.hornta.trollskogen_core.bans.deserializers;

import com.github.hornta.trollskogen_core.bans.Ban;
import com.github.hornta.trollskogen_core.bans.BanManager;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class PostedBanDeserializer implements JsonDeserializer<Ban> {
  @Override
  public Ban deserialize(JsonElement elem, Type type, JsonDeserializationContext jsonDeserializationContext) {
    JsonObject json = elem.getAsJsonObject().get("posted").getAsJsonObject();
    return BanManager.parseBan(json);
  }
}

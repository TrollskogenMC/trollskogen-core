package com.github.hornta.trollskogen.bans.deserializers;

import com.github.hornta.trollskogen.bans.Ban;
import com.github.hornta.trollskogen.bans.BanManager;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class PatchedBanDeserializer implements JsonDeserializer<Ban> {
  @Override
  public Ban deserialize(JsonElement elem, Type type, JsonDeserializationContext jsonDeserializationContext) {
    JsonObject json = elem.getAsJsonObject().get("patched").getAsJsonObject();
    return BanManager.parseBan(json);
  }
}

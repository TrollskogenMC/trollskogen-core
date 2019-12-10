package com.github.hornta.trollskogen.homes.deserializers;

import com.github.hornta.trollskogen.homes.Home;
import com.github.hornta.trollskogen.homes.HomeManager;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HomesDeserializer implements JsonDeserializer<Home[]> {
  @Override
  public Home[] deserialize(JsonElement elem, Type type, JsonDeserializationContext jsonDeserializationContext) {
    ArrayList<Home> homes = new ArrayList<>();
    for (JsonElement jsonElement : elem.getAsJsonObject().getAsJsonArray("homes")) {
      JsonObject json = jsonElement.getAsJsonObject();
      Home home = HomeManager.parseHome(json);
      if (home != null) {
        homes.add(home);
      }
    }
    return homes.toArray(new Home[0]);
  }
}

package com.github.hornta.trollskogen_core.users.deserializers;

import com.github.hornta.trollskogen_core.users.UserManager;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class PostedUserObjectDeserializer implements JsonDeserializer<UserObject> {
  @Override
  public UserObject deserialize(JsonElement elem, Type type, JsonDeserializationContext jsonDeserializationContext) {
    JsonObject json = elem.getAsJsonObject().get("posted").getAsJsonObject();
    return UserManager.parseUser(json);
  }
}

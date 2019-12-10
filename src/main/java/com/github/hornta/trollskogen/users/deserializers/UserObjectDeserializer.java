package com.github.hornta.trollskogen.users.deserializers;

import com.github.hornta.trollskogen.users.UserManager;
import com.github.hornta.trollskogen.users.UserObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class UserObjectDeserializer implements JsonDeserializer<UserObject[]> {
  @Override
  public UserObject[] deserialize(JsonElement elem, Type type, JsonDeserializationContext jsonDeserializationContext) {
    ArrayList<UserObject> users = new ArrayList<>();
    for(JsonElement json : elem.getAsJsonObject().getAsJsonArray("users")) {
      users.add(UserManager.parseUser(json.getAsJsonObject()));
    }

    return users.toArray(new UserObject[0]);
  }
}

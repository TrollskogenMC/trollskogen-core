package com.github.hornta.trollskogen_core.users.deserializers;

import com.github.hornta.trollskogen_core.users.UserManager;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public class PatchedUserObjectDeserializer implements JsonDeserializer<UserObject> {
	@Override
	public UserObject deserialize(JsonElement elem, Type type, JsonDeserializationContext jsonDeserializationContext) {
		var json = elem.getAsJsonObject().get("patched").getAsJsonObject();
		return UserManager.parseUser(json);
	}
}

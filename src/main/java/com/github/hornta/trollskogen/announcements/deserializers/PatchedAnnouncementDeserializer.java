package com.github.hornta.trollskogen.announcements.deserializers;

import com.github.hornta.trollskogen.announcements.Announcement;
import com.github.hornta.trollskogen.announcements.AnnouncementManager;
import com.github.hornta.trollskogen.bans.Ban;
import com.github.hornta.trollskogen.bans.BanManager;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class PatchedAnnouncementDeserializer implements JsonDeserializer<Announcement> {
  @Override
  public Announcement deserialize(JsonElement elem, Type type, JsonDeserializationContext jsonDeserializationContext) {
    JsonObject json = elem.getAsJsonObject().get("patched").getAsJsonObject();
    return AnnouncementManager.parseAnnouncement(json);
  }
}

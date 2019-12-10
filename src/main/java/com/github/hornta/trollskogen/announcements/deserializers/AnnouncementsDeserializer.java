package com.github.hornta.trollskogen.announcements.deserializers;

import com.github.hornta.trollskogen.announcements.Announcement;
import com.github.hornta.trollskogen.announcements.AnnouncementManager;
import com.github.hornta.trollskogen.bans.Ban;
import com.github.hornta.trollskogen.bans.BanManager;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AnnouncementsDeserializer implements JsonDeserializer<Announcement[]> {
  @Override
  public Announcement[] deserialize(JsonElement elem, Type type, JsonDeserializationContext jsonDeserializationContext) {
    ArrayList<Announcement> announcements = new ArrayList<>();
    for(JsonElement json : elem.getAsJsonObject().getAsJsonArray("announcements")) {
      announcements.add(AnnouncementManager.parseAnnouncement(json.getAsJsonObject()));
    }

    return announcements.toArray(new Announcement[0]);
  }
}

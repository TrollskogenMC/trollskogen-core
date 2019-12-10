package com.github.hornta.trollskogen.announcements;

import com.github.hornta.carbon.CarbonArgument;
import com.github.hornta.carbon.CarbonArgumentType;
import com.github.hornta.trollskogen.ConfigKey;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.announcements.commands.*;
import com.github.hornta.trollskogen.announcements.commands.argumentHandlers.AnnouncementArgumentHandler;
import com.github.hornta.trollskogen.announcements.deserializers.AnnouncementsDeserializer;
import com.github.hornta.trollskogen.announcements.deserializers.PatchedAnnouncementDeserializer;
import com.github.hornta.trollskogen.announcements.deserializers.PostedAnnouncementDeserializer;
import com.github.hornta.trollskogen.announcements.events.LoadAnnouncementsEvent;
import com.github.hornta.trollskogen.announcements.events.RequestDeleteAnnouncementEvent;
import com.github.hornta.trollskogen.announcements.events.RequestSetAnnouncementEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.asynchttpclient.Response;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

public class AnnouncementManager implements Listener {
  private Main main;
  private List<Announcement> announcements;
  private int currentAnnouncementIndex = 0;
  private BukkitRunnable task;
  private ScheduledExecutorService scheduledExecutor;

  public AnnouncementManager(Main main) {
    this.main = main;
    this.announcements = new ArrayList<>();
    scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    task = new AnnouncementTask(main, this);
    task.runTaskTimer(main, 0L, (int)main.getConfiguration().get(ConfigKey.ANNOUNCEMENT_INTERVAL) * 20);
    loadAllAnnouncements();
  }

  @EventHandler
  void onRequestSetAnnouncement(RequestSetAnnouncementEvent event) {
    for(Announcement announcement : announcements) {
      if(announcement.getName().equalsIgnoreCase(event.getName())) {
        JsonObject json = generateAnnouncementData(announcement);
        json.addProperty("text", event.getText());
        patchAnnouncement(announcement, json);
        return;
      }
    }
    postAnnouncement(event.getName(), event.getText());
  }

  @EventHandler
  void onRequestDeleteAnnouncement(RequestDeleteAnnouncementEvent event) {
    deleteAnnouncement(event.getAnnouncement());
  }

  public Announcement getAnnouncement(String name) {
    for(Announcement announcement : announcements) {
      if(announcement.getName().equalsIgnoreCase(name)) {
        return announcement;
      }
    }
    return null;
  }

  public List<Announcement> getAnnouncements() {
    return announcements;
  }

  Announcement getNextAnnouncement() {
    if (announcements.size() == 0) {
      return null;
    }

    if (currentAnnouncementIndex >= announcements.size() - 1) {
      currentAnnouncementIndex = 0;
    } else {
      currentAnnouncementIndex += 1;
    }

    return announcements.get(currentAnnouncementIndex);
  }

  public static void setupCommands(Main main) {
    main.getCarbon()
      .addCommand("announcement set")
      .withArgument(
        new CarbonArgument.Builder("id").create()
      )
      .withArgument(
        new CarbonArgument.Builder("message")
        .catchRemaining()
        .create()
      )
      .withHandler(new CommandAnnouncementSet(main))
      .requiresPermission("ts.announcement.set");

    CarbonArgument announcementArgument =
      new CarbonArgument.Builder("id")
      .setHandler(new AnnouncementArgumentHandler(main))
      .create();

    main.getCarbon()
      .addCommand("announcement delete")
      .withArgument(announcementArgument)
      .withHandler(new CommandAnnouncementDelete(main))

      .requiresPermission("ts.announcement.delete");

    main.getCarbon()
      .addCommand("announcement list")
      .withHandler(new CommandAnnouncementList(main))
      .requiresPermission("ts.announcement.list");

    main.getCarbon()
      .addCommand("announcement read")
      .withArgument(announcementArgument)
      .withHandler(new CommandAnnouncementRead(main))
      .requiresPermission("ts.announcement.read");

    main.getCarbon()
      .addCommand("announcement")
      .withHandler(new CommandAnnouncement(main))
      .requiresPermission("ts.announcement");
  }

  private void deleteAnnouncement(Announcement announcement) {
    scheduledExecutor.submit(() -> {
      Main.request("DELETE", "/announcement/" + announcement.getId(), (Response response) -> {
        Bukkit.getScheduler().callSyncMethod(Main.getPlugin(), () -> {
          if(response.getStatusCode() == 200) {
            announcements.remove(announcement);
          }
          return null;
        });
      });
    });
  }

  private void postAnnouncement(String name, String text) {
    JsonObject json = new JsonObject();
    json.addProperty("name", name);
    json.addProperty("text", text);

    scheduledExecutor.submit(() -> {
      Main.request("POST", "/announcement", json, (Response response) -> {
        Gson gson = new GsonBuilder()
          .registerTypeAdapter(Announcement.class, new PostedAnnouncementDeserializer())
          .create();
        Announcement parsedAnnouncement;
        try {
          parsedAnnouncement = gson.fromJson(response.getResponseBody(), Announcement.class);
        } catch (Throwable ex) {
          Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
          return;
        }
        Bukkit.getScheduler().callSyncMethod(Main.getPlugin(), () -> {
          announcements.add(parsedAnnouncement);
          return null;
        });
      });
    });
  }

  private void patchAnnouncement(Announcement announcement, JsonObject json) {
    scheduledExecutor.submit(() -> {
      Main.request("PATCH", "/announcement/" + announcement.getId(), json, (Response response) -> {
        Gson gson = new GsonBuilder()
          .registerTypeAdapter(Announcement.class, new PatchedAnnouncementDeserializer())
          .create();
        Announcement parsedAnnouncement;
        try {
          parsedAnnouncement = gson.fromJson(response.getResponseBody(), Announcement.class);
        } catch (Throwable ex) {
          Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
          return;
        }
        Bukkit.getScheduler().callSyncMethod(Main.getPlugin(), () -> {
          announcement.setMessage(parsedAnnouncement.getMessage());
          return null;
        });
      });
    });
  }

  public void loadAllAnnouncements() {
    scheduledExecutor.submit(() -> {
      Main.request("GET", "/announcements", (Response response) -> {
        Gson gson = new GsonBuilder()
          .registerTypeAdapter(Announcement[].class, new AnnouncementsDeserializer())
          .create();
        Announcement[] parsedAnnouncements;
        try {
          parsedAnnouncements = gson.fromJson(response.getResponseBody(), Announcement[].class);
        } catch (Throwable ex) {
          Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
          return;
        }

        Bukkit.getScheduler().callSyncMethod(Main.getPlugin(), () -> {
          announcements.clear();
          Collections.addAll(announcements, parsedAnnouncements);
          Bukkit.getLogger().info("Loaded " + parsedAnnouncements.length + " announcements");
          Bukkit.getPluginManager().callEvent(new LoadAnnouncementsEvent(this));
          return null;
        });
      });
    });
  }

  private JsonObject generateAnnouncementData(Announcement announcement) {
    JsonObject json = new JsonObject();
    json.addProperty("name", announcement.getName());
    json.addProperty("text", announcement.getMessage());
    return json;
  }

  public static Announcement parseAnnouncement(JsonObject json) {
    return new Announcement(
      json.get("id").getAsInt(),
      json.get("name").getAsString(),
      json.get("text").getAsString()
    );
  }
}

package com.github.hornta.trollskogen.announcements;

import com.github.hornta.carbon.CarbonArgument;
import com.github.hornta.carbon.CarbonArgumentType;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.announcements.commands.*;
import com.github.hornta.trollskogen.announcements.commands.argumentHandlers.AnnouncementArgumentHandler;
import com.github.hornta.trollskogen.messagemanager.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Announcements {
  private JavaPlugin plugin;
  private Config config;
  private Map<String, Announcement> announcements;
  private List<String> announcementsList;
  private boolean isEnabled;
  private long interval;
  private int currentAnnouncementIndex = 0;
  private BukkitRunnable task;

  public Announcements(JavaPlugin plugin) {
    this.plugin = plugin;
    this.config = new Config(plugin, "announcements.yml");
    this.config.saveDefault();

    this.loadFromConfig();

    task = new AnnouncementTask(plugin, this);
    task.runTaskTimer(plugin, 0L, interval * 20);
  }

  public void reload() {
    config.reloadConfig();
    long oldInterval = interval;
    loadFromConfig();

    // restart task if interval changes during reload
    if (interval != oldInterval) {
      task.cancel();
      task = new AnnouncementTask(plugin, this);
      task.runTaskTimer(plugin, 0L, interval * 20);
    }
  }

  public boolean containsAnnouncement(String id) {
    return announcementsList.contains(id);
  }

  public void setAnnouncement(String id, String message) {
    config.getConfig().set("announcements." + id, message);
    config.save();
    loadFromConfig();
  }

  public List<String> getAnnouncementIds() {
    return announcementsList;
  }

  public String getAnnouncement(String id) {
    return announcements.get(id).getMessage();
  }

  public long getInterval() {
    return interval;
  }

  public void setInterval(long interval) {
    config.getConfig().set("interval", interval);
    config.save();
    if (this.interval != interval) {
      task.cancel();
      task = new AnnouncementTask(plugin, this);
      task.runTaskTimer(plugin, 0L, interval * 20);
    }

    this.interval = interval;
  }

  private void loadFromConfig() {
    interval = config.getConfig().getLong("interval");
    isEnabled = config.getConfig().getBoolean("isEnabled");
    announcements = new LinkedHashMap<>();

    ConfigurationSection announcementsSection = config.getConfig().getConfigurationSection("announcements");

    if (announcementsSection != null) {
      for (String key : announcementsSection.getKeys(false)) {
        String message = announcementsSection.getString(key);
        announcements.put(key, new Announcement(key, message));
      }
    }

    announcementsList = new ArrayList<>(announcements.keySet());

    String lastExecuted = config.getConfig().getString("lastExecuted");

    if (announcements.containsKey(lastExecuted)) {
      currentAnnouncementIndex = announcementsList.indexOf(lastExecuted);
    } else {
      currentAnnouncementIndex = 0;
    }
  }

  public void save() {
    if (announcementsList.size() < currentAnnouncementIndex) {
      config.getConfig().set("lastExecuted", announcementsList.get(currentAnnouncementIndex));
    }
    config.save();
  }

  public void setEnabled(boolean enabled) {
    config.getConfig().set("isEnabled", enabled);
    config.save();
    isEnabled = enabled;
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  Announcement getNextAnnouncement() {
    if (announcements.size() == 0) {
      return null;
    }

    if (currentAnnouncementIndex == announcements.size() - 1) {
      currentAnnouncementIndex = 0;
    } else {
      currentAnnouncementIndex += 1;
    }

    return announcements.get(announcementsList.get(currentAnnouncementIndex));
  }

  public static void setupCommands(Main main) {
    main.getCarbon()
      .addCommand("announcement disable")
      .withHandler(new CommandAnnouncementDisable(main))
      .requiresPermission("ts.announcement.disable");

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
      .addCommand("announcement interval")
      .withHandler(new CommandAnnouncementInterval(main))
      .requiresPermission("ts.announcement.interval");

    main.getCarbon()
      .addCommand("announcement interval set")
      .withArgument(
        new CarbonArgument.Builder("seconds")
        .setType(CarbonArgumentType.INTEGER)
        .setMin(1)
        .setMax(Integer.MAX_VALUE)
        .create()
      )
      .withHandler(new CommandAnnouncementIntervalSet(main))
      .requiresPermission("ts.announcement.interval.set");

    main.getCarbon()
      .addCommand("announcement")
      .withHandler(new CommandAnnouncement(main))
      .requiresPermission("ts.announcement");

    main.getCarbon()
      .addCommand("announcement enable")
      .withHandler(new CommandAnnouncementEnable(main))
      .requiresPermission("ts.announcement.enable");
  }
}

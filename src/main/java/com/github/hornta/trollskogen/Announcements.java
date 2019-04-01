package com.github.hornta.trollskogen;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import se.hornta.carbon.Config;

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

  Announcements(JavaPlugin plugin) {
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
    if(interval != oldInterval) {
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
    if(this.interval != interval) {
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

    if(announcementsSection != null) {
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

  void save() {
    config.getConfig().set("lastExecuted", announcementsList.get(currentAnnouncementIndex));
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
    if(announcements.size() == 0) {
      return null;
    }

    if(currentAnnouncementIndex == announcements.size() - 1) {
      currentAnnouncementIndex = 0;
    } else {
      currentAnnouncementIndex += 1;
    }

    return announcements.get(announcementsList.get(currentAnnouncementIndex));
  }
}

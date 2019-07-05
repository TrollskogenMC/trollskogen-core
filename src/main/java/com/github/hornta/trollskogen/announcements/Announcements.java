package com.github.hornta.trollskogen.announcements;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.announcements.commands.*;
import com.github.hornta.trollskogen.messagemanager.Config;
import com.github.hornta.trollskogen.validators.NumberInRangeValidator;
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
    AnnouncementExistValidator announcementExistsValidator = new AnnouncementExistValidator(main);
    AnnouncementCompleter announcementCompleter = new AnnouncementCompleter(main);

    main.getCarbon()
      .addCommand("announcement disable")
      .withHandler(new CommandAnnouncementDisable(main))
      .setNumberOfArguments(0)
      .requiresPermission("ts.announcement.disable")
      .addHelpText("/announcement disable");

    main.getCarbon()
      .addCommand("announcement set")
      .withHandler(new CommandAnnouncementSet(main))
      .setMinNumberOfArguments(2)
      .addHelpText("/announcement set <id> <message>")
      .requiresPermission("ts.announcement.set")
      .setTabComplete(0, announcementCompleter);

    main.getCarbon()
      .addCommand("announcement delete")
      .withHandler(new CommandAnnouncementDelete(main))
      .setNumberOfArguments(1)
      .addHelpText("/announcement delete <id>")
      .requiresPermission("ts.announcement.delete")
      .setTabComplete(0, announcementCompleter)
      .validateArgument(0, announcementExistsValidator);

    main.getCarbon()
      .addCommand("announcement list")
      .withHandler(new CommandAnnouncementList(main))
      .setNumberOfArguments(0)
      .addHelpText("/announcement list")
      .requiresPermission("ts.announcement.list");

    main.getCarbon()
      .addCommand("announcement read")
      .withHandler(new CommandAnnouncementRead(main))
      .setNumberOfArguments(1)
      .addHelpText("/announcement read <id>")
      .requiresPermission("ts.announcement.read")
      .setTabComplete(0, announcementCompleter)
      .validateArgument(0, announcementExistsValidator);

    main.getCarbon()
      .addCommand("announcement interval")
      .withHandler(new CommandAnnouncementInterval(main))
      .setNumberOfArguments(0)
      .addHelpText("/announcement interval")
      .requiresPermission("ts.announcement.interval");

    main.getCarbon()
      .addCommand("announcement interval set")
      .withHandler(new CommandAnnouncementIntervalSet(main))
      .addHelpText("/announcement interval set <seconds>")
      .requiresPermission("ts.announcement.interval.set")
      .setNumberOfArguments(1)
      .validateArgument(0, new NumberInRangeValidator(main, 1, Integer.MAX_VALUE));

    main.getCarbon()
      .addCommand("announcement")
      .withHandler(new CommandAnnouncement(main))
      .setNumberOfArguments(0)
      .requiresPermission("ts.announcement")
      .addHelpText("/announcement");

    main.getCarbon()
      .addCommand("announcement enable")
      .withHandler(new CommandAnnouncementEnable(main))
      .setNumberOfArguments(0)
      .requiresPermission("ts.announcement.enable")
      .addHelpText("/announcement enable");
  }
}

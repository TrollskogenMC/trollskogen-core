package com.github.hornta.trollskogen;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;

class UserManager {
  private Main main;
  private Map<UUID, User> userCache = new ConcurrentHashMap<>();
  private Map<String, User> userNames = new ConcurrentHashMap<>();
  private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
  private File userDataFile;
  private static long DELAY = 5;
  private boolean pendingWrite;

  UserManager(Main main) {
    this.main = main;
    userDataFile = new File(main.getDataFolder(), "userdata.yml");

    try {
      boolean createdFile = userDataFile.createNewFile();
      if(createdFile) {
        Bukkit.getLogger().log(Level.INFO, userDataFile.getCanonicalPath() + " was created");
      }
    } catch (IOException ex) {
      Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
      ex.printStackTrace();
    }

    scheduledExecutor.submit(new UserReader());
    scheduledExecutor.scheduleWithFixedDelay(new UserWriter(), DELAY, DELAY, TimeUnit.SECONDS);
  }

  public void shutdown() {
    scheduledExecutor.shutdown();
    new UserWriter().run();
  }

  public void load() {
    scheduledExecutor.submit(new UserReader());
  }

  Map<UUID, User> getUsers() {
    return userCache;
  }

  User getUser(UUID uuid) {
    return userCache.computeIfAbsent(uuid, (UUID k) -> {
      pendingWrite = true;
      User user = new User(main, uuid);
      userNames.put(user.getLastSeenAs(), user);
      return user;
    });
  }

  User getUser(String name) {
    return userNames.get(name.toLowerCase(Locale.ENGLISH));
  }

  void setPendingWrite() {
    pendingWrite = true;
  }

  private class UserReader implements Runnable {
    @Override
    public void run() {
      try {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(userDataFile);
        ConfigurationSection userSection = config.getConfigurationSection("users");
        if (userSection == null) {
          Bukkit.getLogger().log(Level.SEVERE, "`users` section is missing");
          return;
        }

        Set<String> keys = userSection.getKeys(false);
        userCache.clear();
        userNames.clear();
        for (String key : keys) {
          ConfigurationSection section = config.getConfigurationSection("users." + key);
          if (section == null) {
            Bukkit.getLogger().log(Level.WARNING, "`users." + key + "` section is missing");
            continue;
          }

          User user = new User(main, key, section);
          userCache.put(user.getPlayer().getUniqueId(), user);
          userNames.put(user.getLastSeenAs().toLowerCase(Locale.ENGLISH), user);
          Bukkit.getLogger().log(Level.INFO, "Read user `" + user.getLastSeenAs() + "` from user cache");
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }

  private class UserWriter implements Runnable {
    @Override
    public void run() {
      try {
        if (!pendingWrite) {
          return;
        }
        pendingWrite = false;

        YamlConfiguration config = new YamlConfiguration();
        ConfigurationSection usersSection = config.createSection("users");
        Bukkit.getLogger().log(Level.INFO, "Writing " + userCache.size() + " users to file");
        for (Map.Entry<UUID, User> entry : userCache.entrySet()) {
          User user = entry.getValue();
          ConfigurationSection section = usersSection.createSection(entry.getKey().toString());
          section.set("lastSeenAs", user.getLastSeenAs());
          section.set("isDiscordVerified", user.isVerifiedDiscord());
          section.set("isBanned", user.isBanned());
          section.set("banReason", user.getBanReason());
          LocalDateTime banExpiration = user.getBanExpiration();
          if(banExpiration != null) {
            section.set("banExpiration", user.getBanExpiration().toString());
          }
          section.set("selectedEffect", user.getSelectedEffect() == null ? null : user.getSelectedEffect().name());
        }
        try {
          config.save(userDataFile);
        } catch (IOException ex) {
          Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }
}

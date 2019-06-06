package com.github.hornta.trollskogen;

import com.github.hornta.trollskogen.events.NewUserEvent;
import com.github.hornta.trollskogen.events.ReadUsersEvent;
import com.github.hornta.trollskogen.homes.Home;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;

public class UserManager implements Listener {
  private Main main;
  private static final long TICKS_PER_SECOND = 20;
  private Set<User> tmpBannedUsers = new HashSet<>();
  private static final long SECONDS_BETWEEN_UNBAN_CHECK = 10;
  private Map<UUID, User> userCache = new ConcurrentHashMap<>();
  private Map<String, User> nameToUser = new ConcurrentHashMap<>();
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

    Future<?> userReaderFuture = scheduledExecutor.submit(new UserReader());

    // let everyone know when all users has been read into memory
    new BukkitRunnable() {
      @Override
      public void run() {
        if(userReaderFuture.isDone()) {
          cancel();
          Bukkit.getPluginManager().callEvent(new ReadUsersEvent());
        }
      }
    }.runTaskTimer(main, 1, 1);

    new BukkitRunnable() {
      @Override
      public void run() {
        for(User user : tmpBannedUsers) {
          user.unbanIfExpired();
        }
      }
    }.runTaskTimer(main, 0, TICKS_PER_SECOND * SECONDS_BETWEEN_UNBAN_CHECK);

    scheduledExecutor.scheduleWithFixedDelay(new UserWriter(), DELAY, DELAY, TimeUnit.SECONDS);
  }

  public void shutdown() {
    scheduledExecutor.shutdown();
    new UserWriter().run();
  }

  public void load() {
    scheduledExecutor.submit(new UserReader());
  }

  public Map<UUID, User> getUsers() {
    return userCache;
  }

  User getUser(Player player) {
    return userCache.computeIfAbsent(player.getUniqueId(), (UUID k) -> {
      pendingWrite = true;
      User user = new User(main, player);
      nameToUser.put(user.getLastSeenAs(), user);
      return user;
    });
  }

  User getUser(UUID uuid) {
    return userCache.get(uuid);
  }

  User getUser(String name) {
    return nameToUser.get(name.toLowerCase(Locale.ENGLISH));
  }

  void setPendingWrite() {
    pendingWrite = true;
  }

  @EventHandler
  private void onPlayerJoin(PlayerJoinEvent event) {
    User user = main.getUser(event.getPlayer());
    user.setPlayer(event.getPlayer());

    this.main.scheduleSyncDelayedTask(() -> {
      if(!event.getPlayer().hasPlayedBefore()) {
        event.getPlayer().saveData();
        main.getMessageManager().setValue("player", event.getPlayer().getName());
        main.getMessageManager().broadcast("first-join-message");

        main.getTrollskogenConfig().setStarterInventory(user);
      }
    });
  }

  @EventHandler
  public void onPlayerLogin(PlayerLoginEvent e) {
    boolean isNewUser = !userCache.containsKey(e.getPlayer().getUniqueId());
    User user = main.getUser(e.getPlayer());

    if(isNewUser) {
      Bukkit.getPluginManager().callEvent(new NewUserEvent(user));
    }

    if(user.isBanned()) {
      e.disallow(PlayerLoginEvent.Result.KICK_BANNED, user.getBanMessage());
      return;
    }

    if(main.isMaintenance() && !main.isAllowedMaintenance(e.getPlayer())) {
      e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, main.getMessageManager().getMessage("try_join_maintenance"));
    }
  }

  @EventHandler
  private void onPlayerDisconnect(PlayerQuitEvent event) {
    User user = main.getUser(event.getPlayer());
    user.setSelectedEffect(null, true);
  }

  @EventHandler
  private void onReadUsers(ReadUsersEvent event) {
    for(User user : userCache.values()) {
      if(user.isBanned() && user.getBanExpiration() != null) {
        tmpBannedUsers.add(user);
      }
    }
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
        nameToUser.clear();
        for (String key : keys) {
          ConfigurationSection section = config.getConfigurationSection("users." + key);
          if (section == null) {
            Bukkit.getLogger().log(Level.WARNING, "`users." + key + "` section is missing");
            continue;
          }

          User user = new User(main, key, section);
          userCache.put(user.getPlayer().getUniqueId(), user);
          nameToUser.put(user.getLastSeenAs().toLowerCase(Locale.ENGLISH), user);
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
          section.set("isDiscordVerified", user.isDiscordVerified());
          section.set("isBanned", user.isBanned());
          section.set("banReason", user.getBanReason());
          LocalDateTime banExpiration = user.getBanExpiration();
          if(banExpiration != null) {
            section.set("banExpiration", user.getBanExpiration().toString());
          }
          section.set("selectedEffect", user.getSelectedEffect() == null ? null : user.getSelectedEffect().name());

          if(!user.getHomes().isEmpty()) {
            ConfigurationSection homesSection = section.createSection("homes");
            for(Home home : user.getHomes()) {
              ConfigurationSection homeSection = homesSection.createSection(home.getName());
              homeSection.set("world", home.getLocation().getWorld().getName());
              homeSection.set("x", home.getLocation().getX());
              homeSection.set("y", home.getLocation().getY());
              homeSection.set("z", home.getLocation().getZ());
              homeSection.set("pitch", home.getLocation().getPitch());
              homeSection.set("yaw", home.getLocation().getYaw());
            }
          }
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

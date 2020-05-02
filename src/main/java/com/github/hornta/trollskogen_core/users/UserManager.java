package com.github.hornta.trollskogen_core.users;

import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.DateUtils;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.MessageKey;
import com.github.hornta.trollskogen_core.bans.Ban;
import com.github.hornta.trollskogen_core.users.events.LoadUsersEvent;
import com.github.hornta.trollskogen_core.users.events.NewUserEvent;
import com.github.hornta.trollskogen_core.users.deserializers.PatchedUserObjectDeserializer;
import com.github.hornta.trollskogen_core.users.deserializers.PostedUserObjectDeserializer;
import com.github.hornta.trollskogen_core.users.deserializers.UserObjectDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.asynchttpclient.Response;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;

public class UserManager implements Listener {
  private final TrollskogenCorePlugin main;
  private final List<UserObject> users = new ArrayList<>();
  private final Map<UUID, UserObject> uuidToUser = new HashMap<>();
  private final Map<String, UserObject> nameToUser = new HashMap<>();
  private final Map<Integer, UserObject> idToUser = new HashMap<>();
  private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
  private final Set<UUID> pendingAddUser = new HashSet<>();
  private boolean hasLoadedAllUsers;

  public static DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withLocale(Locale.UK).withZone(ZoneId.systemDefault());

  public UserManager(TrollskogenCorePlugin main) {
    this.main = main;
  }

  public UserObject getUser(Player player) {
    if(!uuidToUser.containsKey(player.getUniqueId())) {
      postUser(player);
      return null;
    }
    return uuidToUser.get(player.getUniqueId());
  }

  public UserObject getUser(UUID uuid) {
    return uuidToUser.get(uuid);
  }

  public UserObject getUser(String name) {
    return nameToUser.get(name);
  }

  public UserObject getUser(int id) {
    return idToUser.get(id);
  }

  public List<UserObject> getUsers() {
    return users;
  }

  @EventHandler
  private void onPlayerJoin(PlayerJoinEvent event) {
    this.main.scheduleSyncDelayedTask(() -> {
      if(!event.getPlayer().hasPlayedBefore()) {
        event.getPlayer().saveData();
        MessageManager.setValue("player", event.getPlayer().getName());
        MessageManager.broadcast(MessageKey.FIRST_JOIN_MESSAGE);
      }
    });
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerLogin(PlayerLoginEvent e) {
    if(!main.getBanManager().hasLoadedBans() || !hasLoadedAllUsers || !TrollskogenCorePlugin.getServerReady().isReady()) {
      e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server not ready yet.");
      return;
    }

    if(!uuidToUser.containsKey(e.getPlayer().getUniqueId())) {
      postUser(e.getPlayer());
    } else {
      UserObject user = getUser(e.getPlayer());
      updateUser(user, e.getPlayer());
      Ban ban = main.getBanManager().getLongestBan(user);
      if(ban != null) {
        MessageManager.setValue("reason", ban.getReason());
        if(ban.getExpiryDate() == null) {
          e.disallow(PlayerLoginEvent.Result.KICK_BANNED, MessageManager.getMessage(MessageKey.KICKBAN_PERMANENT));
        } else {
          MessageManager.setValue("time_left", DateUtils.formatDateDiff(LocalDateTime.ofInstant(ban.getExpiryDate(), ZoneId.of("Europe/Stockholm"))));
          e.disallow(PlayerLoginEvent.Result.KICK_BANNED, MessageManager.getMessage(MessageKey.KICKBAN_TEMPORARY));
        }
        return;
      }

      if(main.isMaintenance() && !main.isAllowedMaintenance(e.getPlayer())) {
        e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "Server is under maintenance.");
      }
    }
  }

  private void postUser(Player player) {
    if(pendingAddUser.contains(player.getUniqueId())) {
      return;
    }
    pendingAddUser.add(player.getUniqueId());

    JsonObject json = new JsonObject();
    json.addProperty("minecraft_uuid", player.getUniqueId().toString());
    json.addProperty("name", player.getName());
    json.addProperty("last_join_date", formatter.format(Instant.now()));

    scheduledExecutor.submit(() -> {
      TrollskogenCorePlugin.request("POST", "/user", json, (Response response) -> {
        Gson gson = new GsonBuilder()
          .registerTypeAdapter(UserObject.class, new PostedUserObjectDeserializer())
          .create();
        UserObject user;
        try {
          user = gson.fromJson(response.getResponseBody(), UserObject.class);
        } catch (JsonSyntaxException ex) {
          Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
          return;
        }
        Bukkit.getScheduler().callSyncMethod(main, () -> {
          users.add(user);
          uuidToUser.put(user.getUuid(), user);
          nameToUser.put(user.getName(), user);
          idToUser.put(user.getId(), user);
          Bukkit.getPluginManager().callEvent(new NewUserEvent(user));
          pendingAddUser.remove(user.getUuid());
          return null;
        });
      });
    });
  }

  private void updateUser(UserObject user, Player player) {
    JsonObject json = new JsonObject();
    json.addProperty("discord_user_id", user.getDiscordUserId());
    json.addProperty("is_verified", user.isVerified());
    json.addProperty("minecraft_uuid", player.getUniqueId().toString());
    json.addProperty("name", player.getName());
    json.addProperty("verify_date", user.getFormattedVerifyDate());
    json.addProperty("verify_token", user.getVerifyToken());
    json.addProperty("verify_token_created", user.getFormattedVerifyTokenCreated());
    json.addProperty("last_join_date", formatter.format(Instant.now()));

    scheduledExecutor.submit(() -> {
      TrollskogenCorePlugin.request("PATCH", "/user/" + user.getId(), json, (Response response) -> {
        Gson gson = new GsonBuilder()
          .registerTypeAdapter(UserObject.class, new PatchedUserObjectDeserializer())
          .create();
        UserObject parsedUser;
        try {
          parsedUser = gson.fromJson(response.getResponseBody(), UserObject.class);
        } catch (Throwable ex) {
          Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
          return;
        }
        Bukkit.getScheduler().callSyncMethod(main, () -> {
          String prevName = user.getName();

          user.setDiscordUserId(parsedUser.getDiscordUserId());
          user.setLastJoinDate(parsedUser.getLastJoinDate());
          user.setName(parsedUser.getName());
          user.setUuid(parsedUser.getUuid());
          user.setVerified(parsedUser.isVerified());
          user.setVerifyDate(parsedUser.getVerifyDate());
          user.setVerifyToken(parsedUser.getVerifyToken());
          user.setVerifyTokenCreated(parsedUser.getVerifyTokenCreated());

          if(!prevName.equals(parsedUser.getName()) && user.getUuid() == parsedUser.getUuid()) {
            nameToUser.remove(prevName);
            nameToUser.put(user.getName(), user);
          }
          return null;
        });
      });
    });
  }

  public void loadAllUsers() {
    hasLoadedAllUsers = false;
    scheduledExecutor.submit(() -> {
      TrollskogenCorePlugin.request("GET", "/users", (Response response) -> {
        Gson gson = new GsonBuilder()
          .registerTypeAdapter(UserObject[].class, new UserObjectDeserializer())
          .create();
        UserObject[] parsedUsers;
        try {
          parsedUsers = gson.fromJson(response.getResponseBody(), UserObject[].class);
        } catch (Throwable ex) {
          Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
          return;
        }

        Bukkit.getScheduler().callSyncMethod(main, () -> {
          users.clear();
          uuidToUser.clear();
          nameToUser.clear();
          idToUser.clear();
          for(UserObject userObject : parsedUsers) {
            users.add(userObject);
            uuidToUser.put(userObject.getUuid(), userObject);
            nameToUser.put(userObject.getName(), userObject);
            idToUser.put(userObject.getId(), userObject);
          }
          hasLoadedAllUsers = true;
          Bukkit.getLogger().info("Loaded " + parsedUsers.length + " users");
          Bukkit.getPluginManager().callEvent(new LoadUsersEvent(this));
          return null;
        });
      });
    });
  }

  public static UserObject parseUser(JsonObject json) {
    Instant verifyDate = null;
    if(!json.get("verify_date").isJsonNull()) {
      verifyDate = Instant.parse(json.get("verify_date").getAsString());
    }
    Instant verifyTokenCreated = null;
    if(!json.get("verify_token_created").isJsonNull()) {
      verifyTokenCreated = Instant.parse(json.get("verify_token_created").getAsString());
    }
    Instant lastJoinDate = null;
    if(!json.get("last_join_date").isJsonNull()) {
      lastJoinDate = Instant.parse(json.get("last_join_date").getAsString());
    }
    String discordUserId = null;
    if(!json.get("discord_user_id").isJsonNull()) {
      discordUserId = json.get("discord_user_id").getAsString();
    }
    String verifyToken = null;
    if(!json.get("verify_token").isJsonNull()) {
      verifyToken = json.get("verify_token").getAsString();
    }
    return new UserObject(
      json.get("id").getAsInt(),
      json.get("name").getAsString(),
      UUID.fromString(json.get("minecraft_uuid").getAsString()),
      json.get("is_verified").getAsBoolean(),
      discordUserId,
      verifyToken,
      verifyTokenCreated,
      verifyDate,
      lastJoinDate
    );
  }
}

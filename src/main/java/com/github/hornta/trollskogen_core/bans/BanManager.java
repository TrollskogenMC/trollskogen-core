package com.github.hornta.trollskogen_core.bans;

import com.github.hornta.commando.CarbonArgument;
import com.github.hornta.commando.CarbonArgumentType;
import com.github.hornta.commando.ICarbonArgument;
import com.github.hornta.commando.completers.IArgumentHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.DateUtils;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.MessageKey;
import com.github.hornta.trollskogen_core.bans.commands.CommandBan;
import com.github.hornta.trollskogen_core.bans.commands.CommandBanList;
import com.github.hornta.trollskogen_core.bans.commands.CommandUnban;
import com.github.hornta.trollskogen_core.bans.commands.argumenthandlers.BannedPlayerArgumentHandler;
import com.github.hornta.trollskogen_core.bans.deserializers.BansDeserializer;
import com.github.hornta.trollskogen_core.bans.deserializers.PatchedBanDeserializer;
import com.github.hornta.trollskogen_core.bans.deserializers.PostedBanDeserializer;
import com.github.hornta.trollskogen_core.bans.events.*;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_core.users.events.LoadUsersEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.asynchttpclient.Response;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class BanManager implements Listener {
  private final TrollskogenCorePlugin main;
  private final List<Ban> bans;
  private final List<Ban> tmpBans;
  private final Map<Integer, ScheduledFuture> tmpBanSchedules;
  private final ScheduledExecutorService scheduledExecutor;
  private boolean hasLoadedBans;

  public static DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withLocale(Locale.UK).withZone(ZoneId.systemDefault());

  public BanManager(TrollskogenCorePlugin main) {
    this.main = main;
    bans = new ArrayList<>();
    tmpBans = new ArrayList<>();
    tmpBanSchedules = new HashMap<>();
    scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    hasLoadedBans = false;
  }

  public Ban getLongestBan(UserObject user) {
    Ban longestBan = null;
    for(Ban ban : bans) {
      if(ban.getExpiryDate() == null) {
        return ban;
      }

      if(longestBan == null) {
        longestBan = ban;
      } else if(ban.getExpiryDate().isAfter(longestBan.getExpiryDate())) {
        longestBan = ban;
      }
    }
    return longestBan;
  }

  public boolean isPermanentlyBanned(UserObject user) {
    for(Ban ban : bans) {
      if(ban.getExpiryDate() == null) {
        return true;
      }
    }
    return false;
  }

  public boolean hasLoadedBans() {
    return hasLoadedBans;
  }

  public List<Ban> getBans() {
    return bans;
  }

  public List<Ban> getBans(UserObject user) {
    ArrayList<Ban> userBans = new ArrayList<>();
    for(Ban ban : bans) {
      if(ban.getUserId() == user.getId()) {
        userBans.add(ban);
      }
    }
    return userBans;
  }

  @EventHandler
  void onLoadUsers(LoadUsersEvent event) {
    loadAllBans();
  }

  @EventHandler
  void onRequestUnban(RequestUnbanEvent event) {
    for(Ban ban : bans) {
      if(ban.getUserId() == event.getUser().getId()) {
        UserObject issuer = null;
        if(event.getIssuer() instanceof Player) {
          issuer = main.getUser((Player)event.getIssuer());
        }
        JsonObject json = generateBanData(ban);
        json.addProperty("is_cancelled", true);
        json.addProperty("cancelled_by", issuer != null ? issuer.getId() : null);
        json.addProperty("cancelled_date", formatter.format(Instant.now()));
        patchBan(ban, json);
      }
    }
  }

  @EventHandler
  void onRequestBan(RequestBanEvent event) {
    postBan(event);
  }

  @EventHandler
  void onBan(BanEvent event) {
    UserObject user = TrollskogenCorePlugin.getUser(event.getBan().getUserId());
    String timeLeft = null;
    if(event.getBan().getExpiryDate() != null) {
      timeLeft = DateUtils.formatDateDiff(LocalDateTime.ofInstant(event.getBan().getExpiryDate(), ZoneId.of("Europe/Stockholm")));
    }
    if(user.isOnline()) {
      MessageManager.setValue("reason", event.getBan().getReason());
      if(event.getBan().getExpiryDate() == null) {
        user.getPlayer().kickPlayer(MessageManager.getMessage(MessageKey.KICKBAN_PERMANENT));
      } else {
        MessageManager.setValue("time_left", timeLeft);
        user.getPlayer().kickPlayer(MessageManager.getMessage(MessageKey.KICKBAN_TEMPORARY));
      }
    }

    MessageManager.setValue("reason", event.getBan().getReason());
    MessageManager.setValue("player", user.getName());
    if(event.getBan().getExpiryDate() != null) {
      MessageManager.setValue("time_left", timeLeft);
    }
    MessageKey messageType = event.getBan().getExpiryDate() == null ? MessageKey.PLAYER_BAN_PERMANENT : MessageKey.PLAYER_BAN_TEMPORARY;
    String banMessage = MessageManager.getMessage(messageType);

    if(event.getIssuer() instanceof Player) {
      event.getIssuer().sendMessage(banMessage);
    }

    // always log banned to server console
    Bukkit.getLogger().log(Level.INFO, banMessage);
  }

  @EventHandler
  void onUnban(UnbanEvent event) {
    MessageManager.setValue("player", main.getUser(event.getBan().getUserId()).getName());
    MessageManager.broadcast(MessageKey.PLAYER_UNBAN);
  }

  public static void setupCommands(TrollskogenCorePlugin main, ICarbonArgument playerArg) {
    ICarbonArgument reasonArg = new CarbonArgument.Builder("reason").setType(CarbonArgumentType.STRING).catchRemaining().create();
    main
      .getCarbon()
      .addCommand("ban")
      .withHandler(new CommandBan(main))
      .withArgument(playerArg)
      .withArgument(reasonArg)
      .requiresPermission("ts.ban");

    IArgumentHandler bannedPlayerHandler = new BannedPlayerArgumentHandler();
    Bukkit.getPluginManager().registerEvents((Listener) bannedPlayerHandler, main);

    main
      .getCarbon()
      .addCommand("unban")
      .withHandler(new CommandUnban(main))
      .withArgument(new CarbonArgument.Builder("player").setHandler(bannedPlayerHandler).create())
      .requiresPermission("ts.unban");

    main
      .getCarbon()
      .addCommand("banlist")
      .withHandler(new CommandBanList(main))
      .requiresPermission("ts.banlist");
  }

  private void postBan(RequestBanEvent event) {
    JsonObject json = new JsonObject();
    json.addProperty("reason", event.getReason());
    json.addProperty("user_id", event.getUserId());
    json.addProperty("issued_by", event.getIssuedBy());
    json.addProperty("expiry_date", event.getExpiryDate() == null ? null : formatter.format(event.getExpiryDate()));

    scheduledExecutor.submit(() -> {
      TrollskogenCorePlugin.request("POST", "/ban", json, (Response response) -> {
        Gson gson = new GsonBuilder()
          .registerTypeAdapter(Ban.class, new PostedBanDeserializer())
          .create();
        Ban ban;
        try {
          ban = gson.fromJson(response.getResponseBody(), Ban.class);
        } catch (Throwable ex) {
          Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
          return;
        }
        Bukkit.getScheduler().callSyncMethod(TrollskogenCorePlugin.getPlugin(), () -> {
          bans.add(ban);
          if(ban.getExpiryDate() != null) {
            tmpBans.add(ban);
            scheduleUnban(ban);
          }
          BanEvent banEvent = new BanEvent(ban, event.getIssuer());
          Bukkit.getPluginManager().callEvent(banEvent);
          return null;
        });
      });
    });
  }

  private void patchBan(Ban ban, JsonObject json) {
    scheduledExecutor.submit(() -> {
      TrollskogenCorePlugin.request("PATCH", "/ban/" + ban.getId(), json, (Response response) -> {
        Gson gson = new GsonBuilder()
          .registerTypeAdapter(Ban.class, new PatchedBanDeserializer())
          .create();
        Ban parsedBan;
        try {
          parsedBan = gson.fromJson(response.getResponseBody(), Ban.class);
        } catch (Throwable ex) {
          Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
          return;
        }
        Bukkit.getScheduler().callSyncMethod(TrollskogenCorePlugin.getPlugin(), () -> {
          if(parsedBan.isCancelled()) {
            bans.remove(ban);
            tmpBans.remove(ban);
            if(tmpBanSchedules.containsKey(ban.getId())) {
              tmpBanSchedules.get(ban.getId()).cancel(true);
              tmpBanSchedules.remove(ban.getId());
            }
            UnbanEvent event = new UnbanEvent(ban);
            Bukkit.getPluginManager().callEvent(event);
            return null;
          }
          ban.setCancelled(parsedBan.isCancelled());
          ban.setCancelledDate(parsedBan.getCancelledDate());
          ban.setCancelledBy(parsedBan.getCancelledBy());
          ban.setExpiryDate(parsedBan.getExpiryDate());
          ban.setIssuedDate(parsedBan.getIssuedDate());
          ban.setIssuedBy(parsedBan.getIssuedBy());
          ban.setReason(parsedBan.getReason());
          ban.setUserId(parsedBan.getUserId());
          return null;
        });
      });
    });
  }

  private void loadAllBans() {
    hasLoadedBans = false;
    scheduledExecutor.submit(() -> {
      TrollskogenCorePlugin.request("GET", "/bans/active", (Response response) -> {
        Gson gson = new GsonBuilder()
          .registerTypeAdapter(Ban[].class, new BansDeserializer())
          .create();
        Ban[] parsedBans;
        try {
          parsedBans = gson.fromJson(response.getResponseBody(), Ban[].class);
        } catch (Throwable ex) {
          Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
          return;
        }

        Bukkit.getScheduler().callSyncMethod(TrollskogenCorePlugin.getPlugin(), () -> {
          bans.clear();
          tmpBans.clear();
          Collections.addAll(bans, parsedBans);
          for(Ban ban : parsedBans) {
            if(ban.getExpiryDate() != null) {
              tmpBans.add(ban);
              scheduleUnban(ban);
            }
          }
          hasLoadedBans = true;
          Bukkit.getLogger().info("Loaded " + parsedBans.length + " bans");
          Bukkit.getPluginManager().callEvent(new LoadBansEvent(this));
          return null;
        });
      });
    });
  }

  private void scheduleUnban(Ban ban) {
    long duration;
    {
      Instant now = Instant.now();
      Instant expiryDate = ban.getExpiryDate();
      duration = Duration.between(now, expiryDate).getSeconds() + 1;
      if (duration <= 0) {
        tmpBans.remove(ban);
        UnbanEvent event = new UnbanEvent(ban);
        Bukkit.getPluginManager().callEvent(event);
        return;
      }
    }
    tmpBanSchedules.put(ban.getId(), scheduledExecutor.schedule(() -> {
      Instant now = Instant.now();
      Instant expiryDate = ban.getExpiryDate();
      if(now.isAfter(expiryDate)) {
        Bukkit.getScheduler().callSyncMethod(TrollskogenCorePlugin.getPlugin(), () -> {
          bans.remove(ban);
          tmpBans.remove(ban);
          tmpBanSchedules.remove(ban.getId());
          UnbanEvent event = new UnbanEvent(ban);
          Bukkit.getPluginManager().callEvent(event);
          return null;
        });
      }
    }, duration, TimeUnit.SECONDS));
  }

  private JsonObject generateBanData(Ban ban) {
    JsonObject json = new JsonObject();
    json.addProperty("reason", ban.getReason());
    json.addProperty("user_id", ban.getUserId());
    json.addProperty("issued_by", ban.getIssuedBy());
    json.addProperty("expiry_date", ban.getExpiryDateFormatted());
    json.addProperty("cancelled_by", ban.getCancelledBy());
    json.addProperty("cancelled_date", ban.getCancelledDateFormatted());
    json.addProperty("is_cancelled", ban.isCancelled());
    json.addProperty("issued_date", ban.getIssuedDateFormatted());
    return json;
  }

  public static Ban parseBan(JsonObject json) {
    Instant issuedDate = null;
    if(!json.get("issued_date").isJsonNull()) {
      issuedDate = Instant.parse(json.get("issued_date").getAsString());
    }
    Instant expiryDate = null;
    if(!json.get("expiry_date").isJsonNull()) {
      expiryDate = Instant.parse(json.get("expiry_date").getAsString());
    }
    Instant cancelledDate = null;
    if(!json.get("cancelled_date").isJsonNull()) {
      cancelledDate = Instant.parse(json.get("cancelled_date").getAsString());
    }
    return new Ban(
      json.get("id").getAsInt(),
      issuedDate,
      json.get("reason").getAsString(),
      expiryDate,
      json.get("is_cancelled").getAsBoolean(),
      json.get("user_id").getAsInt(),
      json.get("issued_by").isJsonNull() ? null : json.get("issued_by").getAsInt(),
      json.get("cancelled_by").isJsonNull() ? null : json.get("cancelled_by").getAsInt(),
      cancelledDate
    );
  }
}

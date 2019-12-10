package com.github.hornta.trollskogen;

import com.github.hornta.carbon.Carbon;
import com.github.hornta.carbon.CarbonArgument;
import com.github.hornta.carbon.CarbonCommand;
import com.github.hornta.carbon.config.ConfigType;
import com.github.hornta.carbon.config.Configuration;
import com.github.hornta.carbon.config.ConfigurationBuilder;
import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.carbon.message.MessagesBuilder;
import com.github.hornta.carbon.message.Translation;
import com.github.hornta.carbon.message.Translations;
import com.github.hornta.sassyspawn.SassySpawn;
import com.github.hornta.trollskogen.announcements.*;
import com.github.hornta.trollskogen.bans.BanManager;
import com.github.hornta.trollskogen.commands.*;
import com.github.hornta.trollskogen.commands.argumentHandlers.*;
import com.github.hornta.trollskogen.commands.CommandHat;
import com.github.hornta.trollskogen.commands.CommandEffectReset;
import com.github.hornta.trollskogen.commands.CommandEffectUse;
import com.github.hornta.trollskogen.effects.ParticleManager;
import com.github.hornta.trollskogen.homes.*;
import com.github.hornta.trollskogen.users.UserManager;
import com.github.hornta.trollskogen.users.UserObject;
import com.github.hornta.trollskogen.websocket.WebsocketHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.query.QueryOptions;
import org.asynchttpclient.*;
import org.asynchttpclient.netty.handler.WebSocketHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.Dsl.config;

public final class Main extends JavaPlugin {
  private static Main main;
  private static AsyncHttpClient asyncHttpClient;
  private Configuration configuration;
  private AnnouncementManager announcementManager;
  private MessageManager messageManager;
  private Carbon carbon;
  private UserManager userManager;
  private ParticleManager particleManager;
  private SassySpawn sassySpawn;
  private HomeManager homeManager;
  private BanManager banManager;
  private LuckPerms luckPerms;

  private boolean isMaintenance;
  private static Gson gson = new Gson();

  @Override
  public void onEnable() {
    main = this;

    new VersionManager();

    DefaultAsyncHttpClientConfig.Builder httpConfig = config();
    asyncHttpClient = asyncHttpClient(httpConfig);

    luckPerms = LuckPermsProvider.get();

    try {
      configuration = new ConfigurationBuilder(this)
        .add(ConfigKey.LANGUAGE, "language", ConfigType.STRING, "swedish")
        .add(ConfigKey.DISCORD_VERIFIED_NUM_HOMES, "discord_verified_num_homes", ConfigType.INTEGER, 1)
        .add(ConfigKey.HOME_PERMS_DEFAULT, "home_perms.default", ConfigType.INTEGER, 1)
        .add(ConfigKey.HOME_PERMS_VIP, "home_perms.vip", ConfigType.INTEGER, 3)
        .add(ConfigKey.MAINTENANCE, "maintenance", ConfigType.LIST, Arrays.asList("hornta", "philip2096"))
        .add(ConfigKey.API_URL, "api_url", ConfigType.STRING, "http://localhost:3000")
        .add(ConfigKey.API_KEY, "api_key", ConfigType.STRING, "")
        .add(ConfigKey.ANNOUNCEMENT_INTERVAL, "announcement_interval", ConfigType.INTEGER, 1800)
        .add(ConfigKey.WS_URL, "ws_url", ConfigType.STRING, "ws://localhost:3000")
        .build();
    } catch (Exception e) {
      setEnabled(false);
      getLogger().log(Level.SEVERE, e.getMessage(), e);
      return;
    }

    messageManager = new MessagesBuilder()
      .add(MessageKey.FIRST_JOIN_MESSAGE, "first-join-message")
      .add(MessageKey.CONFIG_RELOADED_SUCCESS, "config-reloaded-success")
      .add(MessageKey.HAT_SWITCH_SUCCESS, "hat-switch-success")
      .add(MessageKey.HAT_SWITCH_DENY, "hat-switch-deny")
      .add(MessageKey.PLAYER_NOT_FOUND, "player-not-found")
      .add(MessageKey.PLAYER_OPEN_HOME_NOT_FOUND, "player_open_home_not_found")
      .add(MessageKey.COMMAND_INCORRECT_NUM_ARGUMENTS, "command_incorrect_num_arguments")
      .add(MessageKey.COMMAND_NOT_FOUND_SUGGESTIONS, "command_not_found_suggestions")
      .add(MessageKey.NO_PERMISSION, "no_permission")
      .add(MessageKey.ANNOUNCEMENT_NOT_FOUND, "announcement_not_found")
      .add(MessageKey.ANNOUNCEMENT_SET, "announcement_set")
      .add(MessageKey.ANNOUNCEMENT_DELETED, "announcement_deleted")
      .add(MessageKey.ANNOUNCEMENT_LIST, "announcement_list")
      .add(MessageKey.HELP_TITLE, "help_title")
      .add(MessageKey.ANNOUNCEMENT, "announcement")
      .add(MessageKey.SECOND, "second")
      .add(MessageKey.SECONDS, "seconds")
      .add(MessageKey.EFFECT_NOT_FOUND, "effect_not_found")
      .add(MessageKey.EFFECT_RESET, "effect_reset")
      .add(MessageKey.EFFECT_RESET_FAIL, "effect_reset_fail")
      .add(MessageKey.EFFECT_SET, "effect_set")
      .add(MessageKey.EFFECT_SET_IN_USE, "effect_set_in_use")
      .add(MessageKey.PLAYER_BAN_TEMPORARY, "player_ban_temporary")
      .add(MessageKey.PLAYER_BAN_PERMANENT, "player_ban_permanent")
      .add(MessageKey.KICKBAN_PERMANENT, "kickban_permanent")
      .add(MessageKey.KICKBAN_TEMPORARY, "kickban_temporary")
      .add(MessageKey.ERR_BAN_NO_REASON, "err_ban_no_reason")
      .add(MessageKey.PLAYER_UNBAN, "player_unban")
      .add(MessageKey.UNBAN_NOT_BANNED, "unban_not_banned")
      .add(MessageKey.BAN_LIST_PLAYER_TEMPORARY, "ban_list_player_temporary")
      .add(MessageKey.BAN_LIST_PLAYER_PERMANENT, "ban_list_player_permanent")
      .add(MessageKey.NO_PERMISSION_PERMBAN, "no_permission_permban")
      .add(MessageKey.MAX_HOMES, "max_homes")
      .add(MessageKey.HOME_MAXIMUM_USAGE, "home_maximum_usage")
      .add(MessageKey.HOME_SET, "home_set")
      .add(MessageKey.SETHOME_BAD_CHARS, "sethome_bad_chars")
      .add(MessageKey.PLAYER_NOT_SET_HOME, "player_not_set_home")
      .add(MessageKey.HOME_NOT_FOUND, "home_not_found")
      .add(MessageKey.PLAYER_HOME_NOT_FOUND, "player_home_not_found")
      .add(MessageKey.OPEN_HOME_NOT_FOUND, "open_home_not_found")
      .add(MessageKey.OPEN_HOME_HOMELESS_NOT_FOUND, "open_home_homeless_not_found")
      .add(MessageKey.HOME_DELETED, "home_deleted")
      .add(MessageKey.HOMES, "homes")
      .add(MessageKey.HOMES_HOME, "homes_home")
      .add(MessageKey.HOMES_INACTIVE_HOME, "homes_inactive_home")
      .add(MessageKey.HOME_PUBLIC, "home_public")
      .add(MessageKey.HOME_PUBLIC_DISALLOW_COMMANDS, "home_public_disallow_cmds")
      .add(MessageKey.TOGGLE_HOME_COMMANDS_ALLOW, "toggle_home_cmds_allow")
      .add(MessageKey.TOGGLE_HOME_COMMANDS_DENY, "toggle_home_cmds_deny")
      .add(MessageKey.OPEN_HOME_OPENED, "open_home_opened")
      .add(MessageKey.OPEN_HOME, "open_home")
      .add(MessageKey.OPEN_HOME_RESTRICTION, "open_home_restriction")
      .add(MessageKey.CLOSE_HOME, "close_home")
      .add(MessageKey.CLOSE_HOME_CLOSED, "close_home_closed")
      .add(MessageKey.PHOME_SAFE_TELEPORT, "phome_safe_teleport")
      .add(MessageKey.PHOME_BLOCKED_COMMAND, "phome_blocked_cmd")
      .add(MessageKey.TOGGLE_MAINTENANCE_ON, "toggle_maintenance_on")
      .add(MessageKey.TOGGLE_MAINTENANCE_OFF, "toggle_maintenance_off")
      .add(MessageKey.TRY_JOIN_MAINTENANCE, "try_join_maintenance")
      .add(MessageKey.KICK_MAINTENANCE, "kick_maintenance")
      .add(MessageKey.ALREADY_VERIFIED, "already_verified")
      .add(MessageKey.VERIFY_ERROR, "verify_error")
      .add(MessageKey.VERIFIED_SUCCESS, "verified_success")
      .add(MessageKey.VERIFIED_ON_DISCORD, "verified_on_discord")
      .build();

    Translations translations = new Translations(this, messageManager);
    Translation translation = translations.createTranslation(configuration.get(ConfigKey.LANGUAGE));
    Translation fallbackTranslation = translations.createTranslation("swedish");
    messageManager.setTranslation(translation, fallbackTranslation);

    announcementManager = new AnnouncementManager(this);
    carbon = new Carbon();

    homeManager = new HomeManager();
    userManager = new UserManager(this);
    banManager = new BanManager(this);
    particleManager = new ParticleManager(this);
    if(getServer().getPluginManager().getPlugin("SassySpawn") != null) {
      sassySpawn = (SassySpawn) getServer().getPluginManager().getPlugin("SassySpawn");
    }

    WebsocketHandler wsHandler = new WebsocketHandler(this);
    getServer().getPluginManager().registerEvents(wsHandler, this);
    getServer().getPluginManager().registerEvents(banManager, this);
    getServer().getPluginManager().registerEvents(homeManager, this);
    getServer().getPluginManager().registerEvents(userManager, this);
    getServer().getPluginManager().registerEvents(announcementManager, this);
    getServer().getPluginManager().registerEvents(new ExplodeListener(), this);
    getServer().getPluginManager().registerEvents(particleManager, this);

    PlayerArgumentHandler playerArgumentHandler = new PlayerArgumentHandler(this);
    Bukkit.getPluginManager().registerEvents(playerArgumentHandler, this);
    CarbonArgument playerArg = new CarbonArgument.Builder("player").setHandler(playerArgumentHandler).create();

    AnnouncementManager.setupCommands(this);
    HomeManager.setupCommands(this, playerArg);
    BanManager.setupCommands(this, playerArg);

    carbon.setNoPermissionHandler((CommandSender commandSender, CarbonCommand command) -> {
      MessageManager.sendMessage(commandSender, MessageKey.NO_PERMISSION);
    });

    carbon.setMissingArgumentHandler((CommandSender commandSender, CarbonCommand command) -> {
      MessageManager.setValue("usage", command.getHelpText());
      MessageManager.sendMessage(commandSender, MessageKey.COMMAND_INCORRECT_NUM_ARGUMENTS);
    });

    carbon.setMissingCommandHandler((CommandSender sender, List<CarbonCommand> suggestions) -> {
      MessageManager.setValue("suggestions", suggestions.stream()
        .map(CarbonCommand::getHelpText)
        .collect(Collectors.joining("\n")));
      MessageManager.sendMessage(sender, MessageKey.COMMAND_NOT_FOUND_SUGGESTIONS);
    });

    carbon
      .addCommand("ts reload")
      .withHandler(new CommandReload(this))
      .requiresPermission("ts.reload");

    carbon
      .addCommand("hat")
      .withHandler(new CommandHat(this))
      .requiresPermission("ts.hat")
      .preventConsoleCommandSender();

    carbon
      .addCommand("ts help")
      .withHandler(new CommandHelp(this))
      .requiresPermission("ts.help");

    carbon
      .addCommand("effect use")
      .withHandler(new CommandEffectUse(this))
      .withArgument(
        new CarbonArgument.Builder("effect").setHandler(new EffectArgumentHandler(this)).create()
      )
      .requiresPermission("ts.effect.use")
      .preventConsoleCommandSender();

    carbon
      .addCommand("effect reset")
      .withHandler(new CommandEffectReset(this))
      .requiresPermission("ts.effect.reset")
      .preventConsoleCommandSender();

    carbon
      .addCommand("maintenance")
      .withHandler(new CommandMaintenance(this))
      .requiresPermission("ts.maintenance");

    carbon
      .addCommand("verify")
      .withHandler(new CommandVerify(this))
      .requiresPermission("ts.verify")
      .preventConsoleCommandSender();

    carbon
      .addCommand("effect")
      .withHandler((CommandSender sender, String[] args, int typedArgs) -> {
        sender.sendMessage("Success " + args.length);
      });
  }

  public int scheduleSyncDelayedTask(final Runnable run) {
    return this.getServer().getScheduler().scheduleSyncDelayedTask(this, run);
  }

  public AnnouncementManager getAnnouncementManager() {
    return announcementManager;
  }

  public Carbon getCarbon() {
    return carbon;
  }

  public UserManager getUserManager() {
    return userManager;
  }

  public static Plugin getPlugin() {
    return main;
  }

  public static CompletableFuture<Void> request(String method, String path, JsonObject body, Consumer<Response> handler) {
    RequestBuilder builder = new RequestBuilder()
      .addHeader("API-key", (String)main.getConfiguration().get(ConfigKey.API_KEY))
      .addHeader("Content-Type", "application/json")
      .setMethod(method)
      .setUrl(main.getConfiguration().get(ConfigKey.API_URL) + path);

    if(body != null) {
      builder = builder.setBody(body.toString().getBytes(StandardCharsets.UTF_8));
    }

    if(body != null) {
      Bukkit.getLogger().info(method + " " + path + " " + body.toString());
    } else {
      Bukkit.getLogger().info(method + " " + path);
    }

    return asyncHttpClient.prepareRequest(builder.build())
      .execute()
      .toCompletableFuture()
      .thenApply((Response response) -> {
        gson.fromJson(response.getResponseBody(), Object.class);

        if(response.getStatusCode() != 200 && response.getStatusCode() != 201) {
          throw new IllegalArgumentException("Received bad http status");
        }
        return response;
      })
      .thenAccept(handler);
  }

  public static CompletableFuture<Void> request(String method, String path, Consumer<Response> handler) {
    return request(method, path, null, handler);
  }

  public UserObject getUser(String name) {
    return userManager.getUser(name);
  }

  public UserObject getUser(UUID uuid) {
    return userManager.getUser(uuid);
  }

  public UserObject getUser(Player player) {
    return userManager.getUser(player);
  }

  public UserObject getUser(int id) {
    return userManager.getUser(id);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    return carbon.handleCommand(sender, command, args);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    return carbon.handleAutoComplete(sender, command, args);
  }

  public String getSeconds(int amount) {
    return MessageManager.getMessage(amount > 1 ? MessageKey.SECONDS : MessageKey.SECOND);
  }

  public boolean isMaintenance() {
    return isMaintenance;
  }

  public void toggleMaintenance() {
    isMaintenance = !isMaintenance;
  }

  public boolean isAllowedMaintenance(Player player) {
    List<String> allowedDuringMaintenance = configuration.get(ConfigKey.MAINTENANCE);
    for(String playerName : allowedDuringMaintenance) {
      if(player.getName().equalsIgnoreCase(playerName)) {
        return true;
      }
    }
    return false;
  }

  public SassySpawn getSassySpawn() {
    return sassySpawn;
  }

  public HomeManager getHomeManager() {
    return homeManager;
  }

  public BanManager getBanManager() {
    return banManager;
  }

  public int getMaxHomes(UserObject user) {
    String numHomesString = luckPerms
      .getUserManager()
      .getUser(user.getUuid())
      .getCachedData()
      .getMetaData(QueryOptions.nonContextual())
      .getMetaValue("numHomes");
    int numHomes = 0;
    try {
      numHomes += Integer.parseInt(numHomesString);
    } catch (NumberFormatException e) {
    }

    if(user.isVerified()) {
      numHomes += (int)(configuration.get(ConfigKey.DISCORD_VERIFIED_NUM_HOMES));
    }

    return numHomes;
  }

  public Configuration getConfiguration() {
    return configuration;
  }
}

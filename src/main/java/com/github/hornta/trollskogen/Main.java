package com.github.hornta.trollskogen;

import com.github.hornta.carbon.Carbon;
import com.github.hornta.carbon.CarbonArgument;
import com.github.hornta.carbon.CarbonArgumentType;
import com.github.hornta.carbon.CarbonCommand;
import com.github.hornta.sassyspawn.SassySpawn;
import com.github.hornta.trollskogen.announcements.*;
import com.github.hornta.trollskogen.commands.*;
import com.github.hornta.trollskogen.commands.argumentHandlers.*;
import com.github.hornta.trollskogen.commands.CommandHat;
import com.github.hornta.trollskogen.commands.CommandEffectReset;
import com.github.hornta.trollskogen.commands.CommandEffectUse;
import com.github.hornta.trollskogen.effects.ParticleManager;
import com.github.hornta.trollskogen.homes.*;
import com.github.hornta.trollskogen.messagemanager.MessageManager;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.Dsl.config;

public final class Main extends JavaPlugin {
  private static Main main;
  private static TrollskogenConfig trollskogenConfig;
  private static AsyncHttpClient asyncHttpClient;
  private Announcements announcements;
  private MessageManager messageManager;
  private Carbon carbon;
  private UserManager userManager;
  private ParticleManager particleManager;
  private SassySpawn sassySpawn;
  private HomeManager homeManager;

  private boolean isMaintenance;

  @Override
  public void onEnable() {
    main = this;

    new VersionManager();

    DefaultAsyncHttpClientConfig.Builder httpConfig = config();
    httpConfig.setConnectTimeout(1000);
    httpConfig.setRequestTimeout(1000);
    asyncHttpClient = asyncHttpClient(httpConfig);

    announcements = new Announcements(this);
    messageManager = new MessageManager(this);
    carbon = new Carbon();
    trollskogenConfig = new TrollskogenConfig(this);

    homeManager = new HomeManager();
    getServer().getPluginManager().registerEvents(homeManager, this);

    userManager = new UserManager(this);
    particleManager = new ParticleManager(this);
    if(getServer().getPluginManager().getPlugin("SassySpawn") != null) {
      sassySpawn = (SassySpawn) getServer().getPluginManager().getPlugin("SassySpawn");
    }

    getServer().getPluginManager().registerEvents(userManager, this);
    getServer().getPluginManager().registerEvents(new ExplodeListener(), this);
    getServer().getPluginManager().registerEvents(particleManager, this);

    Announcements.setupCommands(this);

    PlayerArgumentHandler playerArgumentHandler = new PlayerArgumentHandler(this);
    Bukkit.getPluginManager().registerEvents(playerArgumentHandler, this);

    carbon.setNoPermissionHandler((CommandSender commandSender, CarbonCommand command) -> {
      main.getMessageManager().sendMessage(commandSender, "no_permission");
    });

    carbon.setMissingArgumentHandler((CommandSender commandSender, CarbonCommand command) -> {
      messageManager.setValue("usages", command.getHelpText());
      messageManager.sendMessage(commandSender, "command_incorrect_num_arguments");
    });

    carbon.setMissingCommandHandler((CommandSender sender, List<CarbonCommand> suggestions) -> {
      messageManager.setValue("command_suggestions", suggestions.stream()
        .map(CarbonCommand::getHelpText)
        .collect(Collectors.joining("\n")));
      messageManager.sendMessage(sender, "command_not_found_suggestions");
    });

    carbon
      .addCommand("ts setstarterkit")
      .withHandler(new CommandSetStarterKit(this))
      .requiresPermission("ts.setstarterkit")
      .preventConsoleCommandSender();

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

    CarbonArgument reasonArg = new CarbonArgument.Builder("reason").setType(CarbonArgumentType.STRING).catchRemaining().create();
    CarbonArgument playerArg = new CarbonArgument.Builder("player").setHandler(playerArgumentHandler).create();
    carbon
      .addCommand("ban")
      .withHandler(new CommandBan(this, false))
      .withArgument(playerArg)
      .withArgument(reasonArg)
      .requiresPermission("ts.ban");

    carbon
      .addCommand("tmpban")
      .withHandler(new CommandBan(this, true))
      .withArgument(playerArg)
      .withArgument(
        new CarbonArgument.Builder("time")
        .setType(CarbonArgumentType.DURATION)
        .create()
      )
      .withArgument(reasonArg)
      .requiresPermission("ts.ban");

    BannedPlayerArgumentHandler bannedPlayerHandler = new BannedPlayerArgumentHandler(this);
    Bukkit.getPluginManager().registerEvents(bannedPlayerHandler, this);

    carbon
      .addCommand("unban")
      .withHandler(new CommandUnban(this))
      .withArgument(new CarbonArgument.Builder("player").setHandler(bannedPlayerHandler).create())
      .requiresPermission("ts.unban");

    carbon
      .addCommand("banlist")
      .withHandler(new CommandBanList(this))
      .requiresPermission("ts.banlist");

    HomeArgumentHandler homeArgumentHandler = new HomeArgumentHandler(this);
    CarbonArgument homeArgument = new CarbonArgument.Builder("home")
      .setHandler(homeArgumentHandler)
      .setDefaultValue(Player.class, (CommandSender sender, String[] args) -> getUser(sender).getFirstHomeName())
      .create();

    carbon
      .addCommand("home")
      .withArgument(homeArgument)
      .withHandler(new CommandHome(this))
      .requiresPermission("ts.home")
      .preventConsoleCommandSender();

    carbon
      .addCommand("sethome")
      .withArgument(
        new CarbonArgument.Builder("home")
          .setPattern(Pattern.compile("[a-z0-9_]+", Pattern.CASE_INSENSITIVE))
          .setDefaultValue(Player.class, Home.DEFAULT_HOME_NAME)
          .create()
      )
      .withHandler(new CommandSetHome(this))
      .requiresPermission("ts.sethome")
      .preventConsoleCommandSender();

    carbon
      .addCommand("delhome")
      .withArgument(
        new CarbonArgument.Builder("home")
        .setHandler(homeArgumentHandler)
        .create()
      )
      .withHandler(new CommandDelHome(this))
      .requiresPermission("ts.delhome")
      .preventConsoleCommandSender();

    carbon
      .addCommand("homes")
      .withHandler(new CommandHomes(this))
      .requiresPermission("ts.homes")
      .preventConsoleCommandSender();

    CarbonArgument playerHomeArgumentHandler = new CarbonArgument.Builder("home")
      .setHandler(new PlayerHomeArgumentHandler(this))
      .dependsOn(playerArg)
      .create();

    carbon
      .addCommand("ahome")
      .withHandler(new CommandAHome(this))
      .withArgument(playerArg)
      .withArgument(playerHomeArgumentHandler)
      .requiresPermission("ts.ahome")
      .preventConsoleCommandSender();

    carbon
      .addCommand("openhome")
      .withHandler(new CommandOpenHome(this))
      .withArgument(homeArgument)
      .requiresPermission("ts.openhome")
      .preventConsoleCommandSender();

    carbon
      .addCommand("closehome")
      .withHandler(new CommandCloseHome(this))
      .withArgument(homeArgument)
      .requiresPermission("ts.closehome")
      .preventConsoleCommandSender();

    carbon
      .addCommand("togglehomecmds")
      .withHandler(new CommandHomeToggleCommands(this))
      .withArgument(homeArgument)
      .requiresPermission("ts.togglehomecmds")
      .preventConsoleCommandSender();

    OpenHomePlayersArgumentHandler openHomePlayersArgumentHandler = new OpenHomePlayersArgumentHandler(this);
    getServer().getPluginManager().registerEvents(openHomePlayersArgumentHandler, this);

    CarbonArgument openHomePlayerArgument = new CarbonArgument.Builder("player")
      .setHandler(openHomePlayersArgumentHandler)
      .create();


    CommandPHome phome = new CommandPHome(this);
    getServer().getPluginManager().registerEvents(phome, this);
    carbon
      .addCommand("phome")
      .withHandler(phome)
      .withArgument(openHomePlayerArgument)
      .withArgument(
        new CarbonArgument.Builder("home")
        .dependsOn(openHomePlayerArgument)
          .setHandler(new PlayerOpenHomeArgumentHandler(this))
        .setDefaultValue(CommandSender.class, (CommandSender sender, String[] args) -> {
          User user = main.getUser(args[0]);
          for(Home home : user.getHomes()) {
            if(home.isPublic()) {
              return home.getName();
            }
          }

          return "";
        })
        .create()
      )
      .requiresPermission("ts.phome")
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
      .addCommand("callevent")
      .withArgument(
        new CarbonArgument.Builder("event")
        .create()
      )
      .withArgument(
        new CarbonArgument.Builder("args")
        .catchRemaining()
        .create()
      )
      .withHandler(new CommandCallEvent(this))
      .preventPlayerCommandSender();

    carbon
      .addCommand("effect")
      .withHandler((CommandSender sender, String[] args, int typedArgs) -> {
        sender.sendMessage("Success " + args.length);
      });
  }

  @Override
  public void onDisable() {
    announcements.save();
    userManager.shutdown();
  }

  public int scheduleSyncDelayedTask(final Runnable run) {
    return this.getServer().getScheduler().scheduleSyncDelayedTask(this, run);
  }

  public Announcements getAnnouncements() {
    return announcements;
  }

  public MessageManager getMessageManager() {
    return messageManager;
  }

  public Carbon getCarbon() {
    return carbon;
  }

  public UserManager getUserManager() {
    return userManager;
  }

  public ParticleManager getParticleManager() {
    return particleManager;
  }

  public static Plugin getPlugin() {
    return main;
  }

  public static TrollskogenConfig getTrollskogenConfig() {
    return trollskogenConfig;
  }

  public static AsyncHttpClient getAsyncHttpClient() {
    return asyncHttpClient;
  }

  public User getUser(String name) {
    return userManager.getUser(name);
  }

  public User getUser(UUID uuid) {
    return userManager.getUser(uuid);
  }

  public User getUser(Player player) {
    return userManager.getUser(player);
  }

  public User getUser(CommandSender commandSender) {
    if(commandSender instanceof Player) {
      return userManager.getUser((Player)commandSender);
    }
    return null;
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
    return messageManager.getMessage(amount > 1 ? "seconds" : "second");
  }

  public boolean isMaintenance() {
    return isMaintenance;
  }

  public void toggleMaintenance() {
    isMaintenance = !isMaintenance;
  }

  public boolean isAllowedMaintenance(Player player) {
    for(String playerName : getTrollskogenConfig().getAllowedMaintenance()) {
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
}

package com.github.hornta.trollskogen;

import com.github.hornta.trollskogen.announcements.*;
import com.github.hornta.trollskogen.announcements.commands.*;
import com.github.hornta.trollskogen.commands.*;
import com.github.hornta.trollskogen.completers.*;
import com.github.hornta.trollskogen.effects.commands.CommandHat;
import com.github.hornta.trollskogen.effects.EffectCompleter;
import com.github.hornta.trollskogen.effects.EffectValidator;
import com.github.hornta.trollskogen.effects.commands.CommandEffectReset;
import com.github.hornta.trollskogen.effects.commands.CommandEffectUse;
import com.github.hornta.trollskogen.effects.ParticleManager;
import com.github.hornta.trollskogen.homes.*;
import com.github.hornta.trollskogen.homes.commands.*;
import com.github.hornta.trollskogen.racing.Racing;
import com.github.hornta.trollskogen.validators.*;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import se.hornta.carbon.Carbon;
import se.hornta.carbon.DefaultArgument;
import se.hornta.carbon.MessageManager;

import java.util.*;
import java.util.regex.Pattern;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.Dsl.config;

public final class Main extends JavaPlugin {
  private Announcements announcements;
  private MessageManager messageManager;
  private Carbon carbon;
  private TrollskogenConfig trollskogenConfig;
  private UserManager userManager;
  private ParticleManager particleManager;
  private AsyncHttpClient asyncHttpClient;
  private Racing racing;
  private SongManager songManager;

  private boolean isMaintenance;

  @Override
  public void onEnable() {
    if (!Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
      this.getLogger().severe("*** NoteBlockAPI is not installed or not enabled. ***");
      this.getLogger().severe("*** This plugin will be disabled. ***");
      this.setEnabled(false);
      return;
    }

    new VersionManager();

    DefaultAsyncHttpClientConfig.Builder config = config();
    config.setConnectTimeout(1000);
    config.setRequestTimeout(1000);
    asyncHttpClient = asyncHttpClient(config);

    announcements = new Announcements(this);
    messageManager = new MessageManager(this);
    carbon = new Carbon(messageManager);
    carbon.setNoPermissionMessage(messageManager.getMessage("no_permission"));
    trollskogenConfig = new TrollskogenConfig(this);
    userManager = new UserManager(this);
    particleManager = new ParticleManager(this);
    racing = new Racing(this);
    songManager = new SongManager(this);

    getServer().getPluginManager().registerEvents(new ExplodeListener(), this);
    getServer().getPluginManager().registerEvents(userManager, this);
    getServer().getPluginManager().registerEvents(particleManager, this);
    getServer().getPluginManager().registerEvents(racing, this);

    Announcements.setupCommands(this);

    carbon
      .addCommand("ts", "setstarterkit")
      .withHandler(new CommandSetStarterKit(this))
      .requiresPermission("ts.setstarterkit")
      .setNumberOfArguments(0)
      .setHelpText("/ts setstarterkit")
      .preventConsoleCommandSender();

    carbon
      .addCommand("ts", "reload")
      .withHandler(new CommandReload(this))
      .setNumberOfArguments(0)
      .setHelpText("/ts reload")
      .requiresPermission("ts.reload");

    carbon
      .addCommand("hat")
      .withHandler(new CommandHat(this))
      .setNumberOfArguments(0)
      .requiresPermission("ts.hat")
      .setHelpText("/hat")
      .preventConsoleCommandSender();

    carbon
      .addCommand("ts", "help")
      .withHandler(new CommandHelp(this))
      .setNumberOfArguments(0)
      .requiresPermission("ts.help");

    carbon
      .addCommand("effect", "use")
      .withHandler(new CommandEffectUse(this))
      .setHelpText("/effect use <effect>")
      .requiresPermission("ts.effect.use")
      .setNumberOfArguments(1)
      .validateArgument(0, new EffectValidator(this))
      .setTabComplete(0, new EffectCompleter(this))
      .preventConsoleCommandSender();

    carbon
      .addCommand("effect", "reset")
      .withHandler(new CommandEffectReset(this))
      .setHelpText("/effect reset")
      .requiresPermission("ts.effect.reset")
      .setNumberOfArguments(0)
      .preventConsoleCommandSender();

    PlayerExistValidator playerExistValidator = new PlayerExistValidator(this);
    PlayerCompleter playerCompleter = new PlayerCompleter(this);
    carbon
      .addCommand("ban")
      .withHandler(new CommandBan(this))
      .setHelpText(new String[] {
        "/ban <player> <reason>",
        "/ban <player> <time> <reason>"
      })
      .requiresPermission("ts.ban")
      .setMinNumberOfArguments(2)
      .validateArgument(0, playerExistValidator)
      .setTabComplete(0, playerCompleter);

    carbon
      .addCommand("unban")
      .withHandler(new CommandUnban(this))
      .setHelpText("/unban <player>")
      .requiresPermission("ts.unban")
      .setNumberOfArguments(1)
      .validateArgument(0, playerExistValidator)
      .setTabComplete(0, playerCompleter, playerCompleter::getBanned);

    carbon
      .addCommand("banlist")
      .withHandler(new CommandBanList(this))
      .setHelpText("/banlist")
      .requiresPermission("ts.banlist")
      .setNumberOfArguments(0);

    HomeCompleter homeCompleter = new HomeCompleter(this);
    HomeExistValidator homeExistValidator = new HomeExistValidator(this);

    carbon
      .addCommand("home")
      .withHandler(new CommandHome(this))
      .setHelpText("/home <home>")
      .requiresPermission("ts.home")
      .setNumberOfArguments(1)
      .withDefaultArgument(new DefaultArgument((CommandSender sender) -> getUser(sender).getFirstHomeName()))
      .setTabComplete(0, homeCompleter)
      .validateArgument(0, homeExistValidator)
      .preventConsoleCommandSender();

    RegexValidator setHomeNameValidator = new RegexValidator(this, Pattern.compile("[a-z0-9_]+", Pattern.CASE_INSENSITIVE), "sethome_bad_chars");
    carbon
      .addCommand("sethome")
      .withHandler(new CommandSetHome(this))
      .setHelpText("/sethome <home>")
      .requiresPermission("ts.sethome")
      .setNumberOfArguments(1)
      .withDefaultArgument(new DefaultArgument(Home.DEFAULT_HOME_NAME))
      .setTabComplete(0, homeCompleter)
      .validateArgument(0, setHomeNameValidator)
      .preventConsoleCommandSender();

    carbon
      .addCommand("delhome")
      .withHandler(new CommandDelHome(this))
      .setHelpText("/delhome <home>")
      .requiresPermission("ts.delhome")
      .setNumberOfArguments(1)
      .setTabComplete(0, homeCompleter)
      .validateArgument(0, homeExistValidator)
      .preventConsoleCommandSender();

    carbon
      .addCommand("homes")
      .withHandler(new CommandHomes(this))
      .setHelpText("/homes")
      .requiresPermission("ts.homes")
      .setNumberOfArguments(0)
      .preventConsoleCommandSender();

    carbon
      .addCommand("maintenance")
      .withHandler(new CommandMaintenance(this))
      .setHelpText("/maintenance")
      .requiresPermission("ts.maintenance")
      .setNumberOfArguments(0);

    carbon
      .addCommand("phome")
      .withHandler(new CommandPHome(this))
      .setHelpText("/phome <player> <home>")
      .requiresPermission("ts.phome")
      .setNumberOfArguments(2)
      .validateArgument(0, playerExistValidator)
      .setTabComplete(0, playerCompleter)
      .validateArgument(new int[] {0, 1}, homeExistValidator, homeExistValidator::testPlayerHome)
      .setTabComplete(new int[] {0, 1}, homeCompleter, homeCompleter::getPlayerHomes)
      .preventConsoleCommandSender();

    carbon
      .addCommand("verify")
      .withHandler(new CommandVerify(this))
      .setHelpText("/verify")
      .requiresPermission("ts.verify")
      .setNumberOfArguments(0)
      .preventConsoleCommandSender();

    carbon
      .addCommand("callevent")
      .withHandler(new CommandCallEvent(this))
      .setHelpText("/callevent <event> [args]")
      .setMinNumberOfArguments(1)
      .preventPlayerCommandSender();

    carbon
      .addCommand("playsong")
      .withHandler(new CommandPlaySong(this))
      .setHelpText("/playsong <song>")
      .setNumberOfArguments(1)
      .validateArgument(0, new SongExistValidator(this))
      .setTabComplete(0, new SongCompleter(this))
      .requiresPermission("ts.playsong")
      .preventConsoleCommandSender();

    carbon
      .addCommand("stopsong")
      .withHandler(new CommandStopSong(this))
      .setHelpText("/stopsong <song>")
      .requiresPermission("ts.stopsong")
      .preventConsoleCommandSender();
  }

  @Override
  public void onDisable() {
    announcements.save();
    userManager.shutdown();
    racing.shutdown();
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

  public TrollskogenConfig getTrollskogenConfig() {
    return trollskogenConfig;
  }

  public UserManager getUserManager() {
    return userManager;
  }

  public ParticleManager getParticleManager() {
    return particleManager;
  }

  public Racing getRacing() {
    return racing;
  }

  public SongManager getSongManager() {
    return songManager;
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
    return carbon.getCommandManager().handleCommand(sender, command, label, args);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    return carbon.getCommandManager().handleAutoComplete(sender, command, args);
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

  public AsyncHttpClient getAsyncHttpClient() {
    return asyncHttpClient;
  }
}

package com.github.hornta.trollskogen;

import com.github.hornta.Carbon;
import com.github.hornta.CarbonCommand;
import com.github.hornta.trollskogen.announcements.*;
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
import com.github.hornta.trollskogen.messagemanager.MessageManager;
import com.github.hornta.trollskogen.racing.Racing;
import com.github.hornta.trollskogen.validators.PlayerExistValidator;
import com.github.hornta.trollskogen.validators.RegexValidator;
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
  private static Racing racing;
  private static TrollskogenConfig trollskogenConfig;
  private static AsyncHttpClient asyncHttpClient;
  private Announcements announcements;
  private MessageManager messageManager;
  private Carbon carbon;
  private UserManager userManager;
  private ParticleManager particleManager;
  private SongManager songManager;

  private boolean isMaintenance;

  @Override
  public void onEnable() {
    main = this;
    getPlugin().g

    if (!Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
      this.getLogger().severe("*** NoteBlockAPI is not installed or not enabled. ***");
      this.getLogger().severe("*** This plugin will be disabled. ***");
      this.setEnabled(false);
      return;
    }

    new VersionManager();

    DefaultAsyncHttpClientConfig.Builder httpConfig = config();
    httpConfig.setConnectTimeout(1000);
    httpConfig.setRequestTimeout(1000);
    asyncHttpClient = asyncHttpClient(httpConfig);

    announcements = new Announcements(this);
    messageManager = new MessageManager(this);
    carbon = new Carbon();
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
    Racing.setupCommands(this);

    carbon.setNoPermissionHandler((CommandSender sender, CarbonCommand command) -> {
      main.getMessageManager().sendMessage(sender, "no_permission");
    });

    carbon.setMissingArgumentHandler((CommandSender sender, CarbonCommand command) -> {
      messageManager.setValue("usages", String.join("\n", command.getHelpTexts().toArray(new String[0])));
      messageManager.sendMessage(sender, "command_incorrect_num_arguments");
    });

    carbon.setMissingCommandHandler((CommandSender sender, List<CarbonCommand> suggestions) -> {
      messageManager.setValue("command_suggestions", suggestions.stream()
        .map(CarbonCommand::getHelpTexts)
        .flatMap(List::stream)
        .collect(Collectors.joining("\n")));
      messageManager.sendMessage(sender, "command_not_found_suggestions");
    });

    carbon
      .addCommand("ts setstarterkit")
      .withHandler(new CommandSetStarterKit(this))
      .requiresPermission("ts.setstarterkit")
      .setNumberOfArguments(0)
      .addHelpText("/ts setstarterkit")
      .preventConsoleCommandSender();

    carbon
      .addCommand("ts reload")
      .withHandler(new CommandReload(this))
      .setNumberOfArguments(0)
      .addHelpText("/ts reload")
      .requiresPermission("ts.reload");

    carbon
      .addCommand("hat")
      .withHandler(new CommandHat(this))
      .setNumberOfArguments(0)
      .requiresPermission("ts.hat")
      .addHelpText("/hat")
      .preventConsoleCommandSender();

    carbon
      .addCommand("ts help")
      .withHandler(new CommandHelp(this))
      .setNumberOfArguments(0)
      .requiresPermission("ts.help");

    carbon
      .addCommand("effect use")
      .withHandler(new CommandEffectUse(this))
      .addHelpText("/effect use <effect>")
      .requiresPermission("ts.effect.use")
      .setNumberOfArguments(1)
      .validateArgument(0, new EffectValidator(this))
      .setTabComplete(0, new EffectCompleter())
      .preventConsoleCommandSender();

    carbon
      .addCommand("effect reset")
      .withHandler(new CommandEffectReset(this))
      .addHelpText("/effect reset")
      .requiresPermission("ts.effect.reset")
      .setNumberOfArguments(0)
      .preventConsoleCommandSender();

    PlayerExistValidator playerExistValidator = new PlayerExistValidator(this);
    PlayerCompleter playerCompleter = new PlayerCompleter(this);
    carbon
      .addCommand("ban")
      .withHandler(new CommandBan(this))
      .addHelpTexts(new String[] {
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
      .addHelpText("/unban <player>")
      .requiresPermission("ts.unban")
      .setNumberOfArguments(1)
      .validateArgument(0, playerExistValidator)
      .setTabComplete(0, new BannedPlayerCompleter(this));

    carbon
      .addCommand("banlist")
      .withHandler(new CommandBanList(this))
      .addHelpText("/banlist")
      .requiresPermission("ts.banlist")
      .setNumberOfArguments(0);

    HomeCompleter homeCompleter = new HomeCompleter(this);
    HomeExistValidator homeExistValidator = new HomeExistValidator(this);

    carbon
      .addCommand("home")
      .withHandler(new CommandHome(this))
      .addHelpText("/home <home>")
      .requiresPermission("ts.home")
      .setNumberOfArguments(1)
      .withDefaultArgument((CommandSender sender) -> getUser(sender).getFirstHomeName())
      .setTabComplete(0, homeCompleter)
      .validateArgument(0, homeExistValidator)
      .preventConsoleCommandSender();

    RegexValidator setHomeNameValidator = new RegexValidator(this, Pattern.compile("[a-z0-9_]+", Pattern.CASE_INSENSITIVE), "sethome_bad_chars");
    carbon
      .addCommand("sethome")
      .withHandler(new CommandSetHome(this))
      .addHelpText("/sethome <home>")
      .requiresPermission("ts.sethome")
      .setNumberOfArguments(1)
      .withDefaultArgument(Home.DEFAULT_HOME_NAME)
      .setTabComplete(0, homeCompleter)
      .validateArgument(0, setHomeNameValidator)
      .preventConsoleCommandSender();

    carbon
      .addCommand("delhome")
      .withHandler(new CommandDelHome(this))
      .addHelpText("/delhome <home>")
      .requiresPermission("ts.delhome")
      .setNumberOfArguments(1)
      .setTabComplete(0, homeCompleter)
      .validateArgument(0, homeExistValidator)
      .preventConsoleCommandSender();

    carbon
      .addCommand("homes")
      .withHandler(new CommandHomes(this))
      .addHelpText("/homes")
      .requiresPermission("ts.homes")
      .setNumberOfArguments(0)
      .preventConsoleCommandSender();

    carbon
      .addCommand("maintenance")
      .withHandler(new CommandMaintenance(this))
      .addHelpText("/maintenance")
      .requiresPermission("ts.maintenance")
      .setNumberOfArguments(0);

    carbon
      .addCommand("phome")
      .withHandler(new CommandPHome(this))
      .addHelpText("/phome <player> <home>")
      .requiresPermission("ts.phome")
      .setNumberOfArguments(2)
      .validateArgument(0, playerExistValidator)
      .setTabComplete(0, playerCompleter)
      .validateArgument(new int[] {0, 1}, new PlayerHomeExistValidator(this))
      .setTabComplete(new int[] {0, 1}, new PlayerHomeCompleter(this))
      .preventConsoleCommandSender();

    carbon
      .addCommand("verify")
      .withHandler(new CommandVerify(this))
      .addHelpText("/verify")
      .requiresPermission("ts.verify")
      .setNumberOfArguments(0)
      .preventConsoleCommandSender();

    carbon
      .addCommand("callevent")
      .withHandler(new CommandCallEvent(this))
      .addHelpText("/callevent <event> [args]")
      .setMinNumberOfArguments(1)
      .preventPlayerCommandSender();

    carbon
      .addCommand("effect")
      .withHandler((CommandSender sender, String[] args) -> {
        sender.sendMessage("Success " + args.length);
      });
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

  public UserManager getUserManager() {
    return userManager;
  }

  public ParticleManager getParticleManager() {
    return particleManager;
  }

  public static Racing getRacing() {
    return racing;
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
    return carbon.getCommandManager().handleCommand(sender, command, args);
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

}

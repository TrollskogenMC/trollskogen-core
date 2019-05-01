package com.github.hornta.trollskogen;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import se.hornta.carbon.Carbon;
import se.hornta.carbon.DefaultArgument;
import se.hornta.carbon.MessageManager;

import java.util.*;
import java.util.regex.Pattern;

public final class Main extends JavaPlugin {
  private Announcements announcements;
  private MessageManager messageManager;
  private Carbon carbon;
  private TrollskogenConfig trollskogenConfig;
  private UserManager userManager;
  private ParticleManager particleManager;

  private boolean isMaintenance = false;

  @Override
  public void onEnable() {
    announcements = new Announcements(this);
    messageManager = new MessageManager(this);
    carbon = new Carbon(messageManager);
    carbon.setNoPermissionMessage(messageManager.getMessage("no_permission"));
    trollskogenConfig = new TrollskogenConfig(this);
    userManager = new UserManager(this);
    particleManager = new ParticleManager(this);

    getServer().getPluginManager().registerEvents(new ExplodeListener(), this);
    getServer().getPluginManager().registerEvents(userManager, this);
    getServer().getPluginManager().registerEvents(particleManager, this);

    AnnouncementExistValidator announcementExistsValidator = new AnnouncementExistValidator(this);
    AnnouncementCompleter announcementCompleter = new AnnouncementCompleter(this);

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
      .addCommand("announcement")
      .withHandler(new CommandAnnouncement(this))
      .setNumberOfArguments(0)
      .requiresPermission("ts.announcement")
      .setHelpText("/announcement");

    carbon
      .addCommand("announcement", "enable")
      .withHandler(new CommandAnnouncementEnable(this))
      .setNumberOfArguments(0)
      .requiresPermission("ts.announcement.enable")
      .setHelpText("/announcement enable");

    carbon
      .addCommand("announcement", "disable")
      .withHandler(new CommandAnnouncementDisable(this))
      .setNumberOfArguments(0)
      .requiresPermission("ts.announcement.disable")
      .setHelpText("/announcement disable");

    carbon
      .addCommand("announcement", "set")
      .withHandler(new CommandAnnouncementSet(this))
      .setMinNumberOfArguments(2)
      .setHelpText("/announcement set <id> <message>")
      .requiresPermission("ts.announcement.set")
      .setTabComplete(0, announcementCompleter);

    carbon
      .addCommand("announcement", "delete")
      .withHandler(new CommandAnnouncementDelete(this))
      .setNumberOfArguments(1)
      .setHelpText("/announcement delete <id>")
      .requiresPermission("ts.announcement.delete")
      .setTabComplete(0, announcementCompleter)
      .validateArgument(0, announcementExistsValidator);

    carbon
      .addCommand("announcement", "list")
      .withHandler(new CommandAnnouncementList(this))
      .setNumberOfArguments(0)
      .setHelpText("/announcement list")
      .requiresPermission("ts.announcement.list");

    carbon
      .addCommand("announcement", "read")
      .withHandler(new CommandAnnouncementRead(this))
      .setNumberOfArguments(1)
      .setHelpText("/announcement read <id>")
      .requiresPermission("ts.announcement.read")
      .setTabComplete(0, announcementCompleter)
      .validateArgument(0, announcementExistsValidator);

    carbon
      .addCommand("announcement", "interval")
      .withHandler(new CommandAnnouncementInterval(this))
      .setNumberOfArguments(0)
      .setHelpText("/announcement interval")
      .requiresPermission("ts.announcement.interval");

    carbon
      .addCommand("announcement", "interval", "set")
      .withHandler(new CommandAnnouncementIntervalSet(this))
      .setHelpText("/announcement interval set <seconds>")
      .requiresPermission("ts.announcement.interval.set")
      .setNumberOfArguments(1)
      .validateArgument(0, new NumberInRangeValidator(this, 1, Integer.MAX_VALUE));

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
      .addCommand("migrateessentials")
      .withHandler(new CommandMigrateEssentials(this))
      .setHelpText("/migrateessentials")
      .requiresPermission("ts.migrateessentials")
      .setNumberOfArguments(0);
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

  public TrollskogenConfig getTrollskogenConfig() {
    return trollskogenConfig;
  }

  public UserManager getUserManager() {
    return userManager;
  }

  public ParticleManager getParticleManager() {
    return particleManager;
  }

  public User getUser(String name) {
    return userManager.getUser(name);
  }

  public User getUser(Player player) {
    return userManager.getUser(player.getUniqueId());
  }

  public User getUser(CommandSender commandSender) {
    if(commandSender instanceof Player) {
      return userManager.getUser(((Player) commandSender).getUniqueId());
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
}

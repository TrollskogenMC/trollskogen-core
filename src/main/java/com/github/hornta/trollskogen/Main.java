package com.github.hornta.trollskogen;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import se.hornta.carbon.Carbon;
import se.hornta.carbon.MessageManager;

import java.util.List;

public final class Main extends JavaPlugin {
  private Carbon carbon;
  private MessageManager messageManager ;
  private TrollskogenConfig trollskogenConfig;
  private Announcements announcements;

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new ExplodeListener(), this);
    getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

    announcements = new Announcements(this);
    messageManager = new MessageManager(this);
    carbon = new Carbon(messageManager);
    carbon.setNoPermissionMessage(messageManager.getMessage("no_permission"));
    trollskogenConfig = new TrollskogenConfig(this);

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
  }

  @Override
  public void onDisable() {
    announcements.save();
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
}

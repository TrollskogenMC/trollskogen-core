package com.github.hornta.trollskogen_core.announcements.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandAnnouncementRead implements ICommandHandler {
  private final TrollskogenCorePlugin main;
  public CommandAnnouncementRead(TrollskogenCorePlugin main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getAnnouncementManager().getAnnouncement(args[0]).getMessage()));
  }
}

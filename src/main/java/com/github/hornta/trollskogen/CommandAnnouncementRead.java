package com.github.hornta.trollskogen;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

public class CommandAnnouncementRead implements ICommandHandler {
  private Main main;
  CommandAnnouncementRead(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getAnnouncements().getAnnouncement(args[0])));
  }
}

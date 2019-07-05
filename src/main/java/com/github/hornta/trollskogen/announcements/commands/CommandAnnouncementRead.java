package com.github.hornta.trollskogen.announcements.commands;

import com.github.hornta.trollskogen.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import com.github.hornta.ICommandHandler;

public class CommandAnnouncementRead implements ICommandHandler {
  private Main main;
  public CommandAnnouncementRead(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getAnnouncements().getAnnouncement(args[0])));
  }
}

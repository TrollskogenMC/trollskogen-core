package com.github.hornta.trollskogen.announcements.commands;

import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.announcements.Announcement;
import com.github.hornta.trollskogen.announcements.events.RequestDeleteAnnouncementEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;

public class CommandAnnouncementDelete implements ICommandHandler {
  private Main main;
  public CommandAnnouncementDelete(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    Announcement announcement = main.getAnnouncementManager().getAnnouncement(args[0]);
    RequestDeleteAnnouncementEvent event = new RequestDeleteAnnouncementEvent(announcement);
    Bukkit.getPluginManager().callEvent(event);

    MessageManager.setValue("announcement_id", args[0]);
    MessageManager.sendMessage(commandSender, MessageKey.ANNOUNCEMENT_DELETED);
  }
}

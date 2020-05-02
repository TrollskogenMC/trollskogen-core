package com.github.hornta.trollskogen_core.announcements.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.MessageKey;
import com.github.hornta.trollskogen_core.announcements.Announcement;
import com.github.hornta.trollskogen_core.announcements.events.RequestDeleteAnnouncementEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandAnnouncementDelete implements ICommandHandler {
  private final TrollskogenCorePlugin main;
  public CommandAnnouncementDelete(TrollskogenCorePlugin main) {
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

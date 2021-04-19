package com.github.hornta.trollskogen_core.announcements.commands;

import com.github.hornta.commando.ICommandHandler;
import se.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.MessageKey;
import com.github.hornta.trollskogen_core.announcements.events.RequestSetAnnouncementEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandAnnouncementSet implements ICommandHandler {
  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    RequestSetAnnouncementEvent event = new RequestSetAnnouncementEvent(args[0], args[1]);
    Bukkit.getPluginManager().callEvent(event);

    MessageManager.setValue("announcement_id", args[0]);
    MessageManager.setValue("announcement_message", args[1]);
    MessageManager.sendMessage(commandSender, MessageKey.ANNOUNCEMENT_SET);
  }
}

package com.github.hornta.trollskogen.announcements.commands;

import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.announcements.events.RequestSetAnnouncementEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;

public class CommandAnnouncementSet implements ICommandHandler {
  private Main main;
  public CommandAnnouncementSet(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    RequestSetAnnouncementEvent event = new RequestSetAnnouncementEvent(args[0], args[1]);
    Bukkit.getPluginManager().callEvent(event);

    MessageManager.setValue("announcement_id", args[0]);
    MessageManager.setValue("announcement_message", args[1]);
    MessageManager.sendMessage(commandSender, MessageKey.ANNOUNCEMENT_SET);
  }
}

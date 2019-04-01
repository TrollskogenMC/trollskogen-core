package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

public class CommandAnnouncementDelete implements ICommandHandler {
  private Main main;
  CommandAnnouncementDelete(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    main.getAnnouncements().setAnnouncement(args[0], null);
    main.getMessageManager().setValue("announcement_id", args[0]);
    main.getMessageManager().sendMessage(commandSender, "announcement_deleted");
  }
}

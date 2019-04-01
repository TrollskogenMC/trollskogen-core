package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

public class CommandAnnouncementList implements ICommandHandler {
  private Main main;
  CommandAnnouncementList(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    main.getMessageManager().setValue("announcements", String.join(", ", main.getAnnouncements().getAnnouncementIds()));
    main.getMessageManager().sendMessage(commandSender, "announcement_list");
  }
}

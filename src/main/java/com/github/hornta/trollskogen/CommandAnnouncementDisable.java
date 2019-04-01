package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

public class CommandAnnouncementDisable implements ICommandHandler {
  private Main main;
  CommandAnnouncementDisable(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    main.getAnnouncements().setEnabled(false);
    main.getMessageManager().sendMessage(commandSender, "disabled");
  }
}

package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

import java.text.DecimalFormat;

public class CommandAnnouncementEnable implements ICommandHandler {
  private Main main;
  CommandAnnouncementEnable(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    main.getAnnouncements().setEnabled(true);
    main.getMessageManager().sendMessage(commandSender, "enabled");
  }
}

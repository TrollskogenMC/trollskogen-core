package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

import java.text.DecimalFormat;

public class CommandAnnouncementIntervalSet implements ICommandHandler {
  private Main main;
  CommandAnnouncementIntervalSet(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    long interval = Long.parseLong(args[0]);
    main.getAnnouncements().setInterval(interval);
    main.getMessageManager().setValue("second", main.getSeconds((int)interval));
    main.getMessageManager().setValue("interval", new DecimalFormat("###,###.#").format(interval));
    main.getMessageManager().sendMessage(commandSender, "interval_set_success");
  }
}

package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

import java.text.DecimalFormat;

public class CommandAnnouncementInterval implements ICommandHandler {
  private Main main;
  CommandAnnouncementInterval(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    long interval = main.getAnnouncements().getInterval();
    main.getMessageManager().setValue("second", main.getSeconds((int)interval));
    main.getMessageManager().setValue("interval", new DecimalFormat("###,###.#").format(interval));
    main.getMessageManager().sendMessage(commandSender, "interval");
  }
}

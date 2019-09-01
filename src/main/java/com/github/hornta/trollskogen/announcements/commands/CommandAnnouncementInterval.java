package com.github.hornta.trollskogen.announcements.commands;

import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;

import java.text.DecimalFormat;

public class CommandAnnouncementInterval implements ICommandHandler {
  private Main main;
  public CommandAnnouncementInterval(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    long interval = main.getAnnouncements().getInterval();
    main.getMessageManager().setValue("second", main.getSeconds((int)interval));
    main.getMessageManager().setValue("interval", new DecimalFormat("###,###.#").format(interval));
    main.getMessageManager().sendMessage(commandSender, "interval");
  }
}

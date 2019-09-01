package com.github.hornta.trollskogen.announcements.commands;

import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;

public class CommandAnnouncementList implements ICommandHandler {
  private Main main;
  public CommandAnnouncementList(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    main.getMessageManager().setValue("announcements", String.join(", ", main.getAnnouncements().getAnnouncementIds()));
    main.getMessageManager().sendMessage(commandSender, "announcement_list");
  }
}

package com.github.hornta.trollskogen.announcements.commands;

import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;

public class CommandAnnouncementDelete implements ICommandHandler {
  private Main main;
  public CommandAnnouncementDelete(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    main.getAnnouncements().setAnnouncement(args[0], null);
    main.getMessageManager().setValue("announcement_id", args[0]);
    main.getMessageManager().sendMessage(commandSender, "announcement_deleted");
  }
}

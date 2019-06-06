package com.github.hornta.trollskogen.announcements.commands;

import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

import java.util.Arrays;

public class CommandAnnouncementSet implements ICommandHandler {
  private Main main;
  public CommandAnnouncementSet(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
    main.getAnnouncements().setAnnouncement(args[0], message);
    main.getMessageManager().setValue("announcement_id", args[0]);
    main.getMessageManager().setValue("announcement_message", message);
    main.getMessageManager().sendMessage(commandSender, "announcement_set");
  }
}

package com.github.hornta.trollskogen.announcements.commands;

import com.github.hornta.trollskogen.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CommandAnnouncementSet implements ICommandHandler {
  private Main main;
  public CommandAnnouncementSet(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    main.getAnnouncements().setAnnouncement(args[0], args[1]);
    main.getMessageManager().setValue("announcement_id", args[0]);
    main.getMessageManager().setValue("announcement_message", args[1]);
    main.getMessageManager().sendMessage(commandSender, "announcement_set");
  }
}

package com.github.hornta.trollskogen.announcements.commands;

import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.announcements.Announcement;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;

import java.util.stream.Collectors;

public class CommandAnnouncementList implements ICommandHandler {
  private Main main;
  public CommandAnnouncementList(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    MessageManager.setValue(
      "announcements",
      main.getAnnouncementManager().getAnnouncements().stream().map(Announcement::getMessage).collect(Collectors.joining(", "))
    );
    MessageManager.sendMessage(commandSender, MessageKey.ANNOUNCEMENT_LIST);
  }
}

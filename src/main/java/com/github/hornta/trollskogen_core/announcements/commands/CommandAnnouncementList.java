package com.github.hornta.trollskogen_core.announcements.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.MessageKey;
import com.github.hornta.trollskogen_core.announcements.Announcement;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

public class CommandAnnouncementList implements ICommandHandler {
  private final TrollskogenCorePlugin main;
  public CommandAnnouncementList(TrollskogenCorePlugin main) {
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

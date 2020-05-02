package com.github.hornta.trollskogen_core.announcements.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.ConfigKey;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.MessageKey;
import com.github.hornta.trollskogen_core.announcements.Announcement;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;
import java.util.stream.Collectors;

public class CommandAnnouncement implements ICommandHandler {
  private final TrollskogenCorePlugin main;
  public CommandAnnouncement(TrollskogenCorePlugin main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    long interval = Integer.toUnsignedLong(TrollskogenCorePlugin.getConfiguration().get(ConfigKey.ANNOUNCEMENT_INTERVAL));

    MessageManager.setValue("second", main.getSeconds((int)interval));
    MessageManager.setValue("interval", new DecimalFormat("###,###.#").format(interval));
    MessageManager.setValue("announcements", String.join(", ", main.getAnnouncementManager().getAnnouncements().stream().map(Announcement::getMessage).collect(Collectors.joining(", "))));
    MessageManager.sendMessage(commandSender, MessageKey.ANNOUNCEMENT);
  }
}

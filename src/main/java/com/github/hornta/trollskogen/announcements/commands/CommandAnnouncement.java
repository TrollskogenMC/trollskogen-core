package com.github.hornta.trollskogen.announcements.commands;

import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.ConfigKey;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.announcements.Announcement;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;

import java.text.DecimalFormat;
import java.util.stream.Collectors;

public class CommandAnnouncement implements ICommandHandler {
  private Main main;
  public CommandAnnouncement(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    long interval = Integer.toUnsignedLong(main.getConfiguration().get(ConfigKey.ANNOUNCEMENT_INTERVAL));

    MessageManager.setValue("second", main.getSeconds((int)interval));
    MessageManager.setValue("interval", new DecimalFormat("###,###.#").format(interval));
    MessageManager.setValue("announcements", String.join(", ", main.getAnnouncementManager().getAnnouncements().stream().map(Announcement::getMessage).collect(Collectors.joining(", "))));
    MessageManager.sendMessage(commandSender, MessageKey.ANNOUNCEMENT);
  }
}

package com.github.hornta.trollskogen.announcements.commands.argumentHandlers;

import com.github.hornta.carbon.ValidationResult;
import com.github.hornta.carbon.completers.IArgumentHandler;
import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.announcements.Announcement;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnouncementArgumentHandler implements IArgumentHandler {
  private Main plugin;

  public AnnouncementArgumentHandler(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
    return plugin
      .getAnnouncementManager()
      .getAnnouncements()
      .stream()
      .map(Announcement::getName)
      .filter(name -> name.toLowerCase(Locale.ENGLISH).startsWith(argument.toLowerCase(Locale.ENGLISH)))
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  public boolean test(Set<String> items, String argument) {
    return items.contains(argument);
  }

  @Override
  public void whenInvalid(ValidationResult validationResult) {
    MessageManager.setValue("announcement_id", validationResult.getValue());
    MessageManager.sendMessage(validationResult.getCommandSender(), MessageKey.ANNOUNCEMENT_NOT_FOUND);
  }
}

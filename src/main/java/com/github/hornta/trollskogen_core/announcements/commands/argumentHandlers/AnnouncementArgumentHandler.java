package com.github.hornta.trollskogen_core.announcements.commands.argumentHandlers;

import com.github.hornta.commando.ValidationResult;
import com.github.hornta.commando.completers.IArgumentHandler;
import se.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.MessageKey;
import com.github.hornta.trollskogen_core.announcements.Announcement;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnouncementArgumentHandler implements IArgumentHandler {
  private final TrollskogenCorePlugin plugin;

  public AnnouncementArgumentHandler(TrollskogenCorePlugin plugin) {
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

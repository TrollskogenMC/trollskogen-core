package com.github.hornta.trollskogen.announcements.commands.argumentHandlers;

import com.github.hornta.carbon.ValidationResult;
import com.github.hornta.carbon.completers.IArgumentHandler;
import com.github.hornta.trollskogen.Main;
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
    return plugin.getAnnouncements().getAnnouncementIds().stream()
      .filter(id -> id.toLowerCase(Locale.ENGLISH).startsWith(argument.toLowerCase(Locale.ENGLISH)))
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  public boolean test(Set<String> items, String argument) {
    return items.contains(argument);
  }

  @Override
  public void whenInvalid(ValidationResult validationResult) {
    plugin.getMessageManager().setValue("announcement_id", validationResult.getValue());
    plugin.getMessageManager().sendMessage(validationResult.getCommandSender(), "announcement_not_found");
  }
}

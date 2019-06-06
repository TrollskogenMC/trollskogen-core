package com.github.hornta.trollskogen.announcements;

import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.BaseTabCompleter;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AnnouncementCompleter extends BaseTabCompleter {
  private Main plugin;

  public AnnouncementCompleter(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public List<String> getItems(CommandSender sender, String[] arguments) {
    return plugin.getAnnouncements().getAnnouncementIds().stream()
      .filter(id -> id.toLowerCase(Locale.ENGLISH).startsWith(arguments[0].toLowerCase(Locale.ENGLISH)))
      .collect(Collectors.toList());
  }

  @Override
  public List<String> toSuggestions(CommandSender sender, List<String> items) {
    return items;
  }
}

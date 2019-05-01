package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.BaseTabCompleter;

import java.util.List;
import java.util.stream.Collectors;

public class AnnouncementCompleter extends BaseTabCompleter<String> {
  private Main plugin;

  public AnnouncementCompleter(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public List<String> getItems(CommandSender sender, String argument) {
    return plugin.getAnnouncements().getAnnouncementIds().stream()
      .filter(id -> id.toLowerCase().startsWith(argument.toLowerCase()))
      .collect(Collectors.toList());
  }

  @Override
  public List<String> toSuggestions(CommandSender sender, List<String> items) {
    return items;
  }
}

package com.github.hornta.trollskogen;

import se.hornta.carbon.BaseTabCompleter;

import java.util.List;
import java.util.stream.Collectors;

public class AnnouncementCompleter extends BaseTabCompleter<String> {
  private Main plugin;

  public AnnouncementCompleter(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public List<String> getItems(String argument) {
    return plugin.getAnnouncements().getAnnouncementIds().stream()
      .filter(id -> id.toLowerCase().startsWith(argument.toLowerCase()))
      .collect(Collectors.toList());
  }

  @Override
  public List<String> toSuggestions(List<String> items) {
    return items;
  }
}

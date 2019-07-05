package com.github.hornta.trollskogen.announcements;

import com.github.hornta.BaseTabCompleter;
import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AnnouncementCompleter implements BaseTabCompleter {
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
}

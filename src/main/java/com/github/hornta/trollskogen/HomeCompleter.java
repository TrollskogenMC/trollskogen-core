package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.BaseTabCompleter;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HomeCompleter extends BaseTabCompleter<Home> {
  private Main main;

  HomeCompleter(Main main) {
    this.main = main;
  }

  @Override
  public List<Home> getItems(CommandSender sender, String argument) {
    User user = main.getUser(sender);
    return user.getHomes()
      .stream()
      .filter(h -> h.getName().toLowerCase(Locale.ENGLISH).startsWith(argument.toLowerCase(Locale.ENGLISH)))
      .collect(Collectors.toList());
  }

  @Override
  public List<String> toSuggestions(CommandSender sender, List<Home> items) {
    return items.stream().map(h -> h.getName()).collect(Collectors.toList());
  }
}

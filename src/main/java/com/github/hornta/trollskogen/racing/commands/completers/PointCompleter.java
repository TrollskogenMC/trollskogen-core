package com.github.hornta.trollskogen.racing.commands.completers;

import com.github.hornta.trollskogen.racing.Racing;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.BaseTabCompleter;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PointCompleter extends BaseTabCompleter {
  private Racing racing;

  public PointCompleter(Racing plugin) {
    this.racing = plugin;
  }

  @Override
  public List<String> getItems(CommandSender sender, String[] arguments) {
    return racing.getRace(arguments[0]).getCheckpoints().stream()
      .filter(race -> String.valueOf(race.getPosition()).toLowerCase(Locale.ENGLISH).startsWith(arguments[1].toLowerCase(Locale.ENGLISH)))
      .map(cp -> String.valueOf(cp.getPosition()))
      .collect(Collectors.toList());
  }

  @Override
  public List<String> toSuggestions(CommandSender sender, List<String> items) {
    return items;
  }
}

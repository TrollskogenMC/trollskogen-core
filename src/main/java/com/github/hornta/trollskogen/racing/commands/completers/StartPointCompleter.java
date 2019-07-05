package com.github.hornta.trollskogen.racing.commands.completers;

import com.github.hornta.BaseTabCompleter;
import com.github.hornta.trollskogen.racing.Racing;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class StartPointCompleter implements BaseTabCompleter {
  private Racing racing;

  public StartPointCompleter(Racing plugin) {
    this.racing = plugin;
  }

  @Override
  public List<String> getItems(CommandSender sender, String[] arguments) {
    return racing.getRace(arguments[0]).getStartPoints().stream()
      .filter(point -> String.valueOf(point.getPosition()).toLowerCase(Locale.ENGLISH).startsWith(arguments[1].toLowerCase(Locale.ENGLISH)))
      .map(p -> String.valueOf(p.getPosition()))
      .collect(Collectors.toList());
  }
}

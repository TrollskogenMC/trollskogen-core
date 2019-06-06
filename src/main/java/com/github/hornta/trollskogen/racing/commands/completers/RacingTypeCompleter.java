package com.github.hornta.trollskogen.racing.commands.completers;

import com.github.hornta.trollskogen.racing.enums.RacingType;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.BaseTabCompleter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RacingTypeCompleter extends BaseTabCompleter {

  @Override
  public List<String> getItems(CommandSender sender, String[] arguments) {
    return Arrays.stream(RacingType.values())
      .map(RacingType::name)
      .filter(type -> type.toLowerCase(Locale.ENGLISH).startsWith(arguments[0].toLowerCase(Locale.ENGLISH)))
      .collect(Collectors.toList());
  }

  @Override
  public List<String> toSuggestions(CommandSender sender, List<String> items) {
    return items;
  }
}

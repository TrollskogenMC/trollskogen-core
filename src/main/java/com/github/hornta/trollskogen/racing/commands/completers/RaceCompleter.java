package com.github.hornta.trollskogen.racing.commands.completers;

import com.github.hornta.BaseTabCompleter;
import com.github.hornta.trollskogen.racing.Racing;
import com.github.hornta.trollskogen.racing.objects.Race;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RaceCompleter implements BaseTabCompleter {
  private Racing racing;

  public RaceCompleter(Racing racing) {
    this.racing = racing;
  }

  @Override
  public List<String> getItems(CommandSender sender, String[] arguments) {
    return racing.getRaces().stream()
      .filter(race -> race.getName().toLowerCase(Locale.ENGLISH).startsWith(arguments[0].toLowerCase(Locale.ENGLISH)))
      .map(Race::getName)
      .collect(Collectors.toList());
  }
}

package com.github.hornta.trollskogen.completers;

import com.github.hornta.trollskogen.Home;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.BaseTabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HomeCompleter extends BaseTabCompleter {
  private Main main;

  public HomeCompleter(Main main) {
    this.main = main;
  }

  @Override
  public List<String> getItems(CommandSender sender, String[] arguments) {
    return getUserHomes(main.getUser(sender), arguments[0]);
  }

  public List<String> getPlayerHomes(CommandSender sender, String[] arguments) {
    return getUserHomes(main.getUser(arguments[0]), arguments[1]);
  }

  private List<String> getUserHomes(User user, String startsWith) {
    if(user == null) {
      return Collections.emptyList();
    }

    return user
      .getHomes()
      .stream()
      .filter(h -> h.getName().toLowerCase(Locale.ENGLISH).startsWith(startsWith.toLowerCase(Locale.ENGLISH)))
      .map(Home::getName)
      .collect(Collectors.toList());
  }

  @Override
  public List<String> toSuggestions(CommandSender sender, List<String> items) {
    return items;
  }
}

package com.github.hornta.trollskogen.homes;

import com.github.hornta.BaseTabCompleter;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PlayerHomeCompleter implements BaseTabCompleter {
  private Main main;

  public PlayerHomeCompleter(Main main) {
    this.main = main;
  }

  @Override
  public List<String> getItems(CommandSender sender, String[] arguments) {
    return getUserHomes(main.getUser(arguments[0]), arguments[1]);
  }

  private static List<String> getUserHomes(User user, String startsWith) {
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
}

package com.github.hornta.trollskogen;

import se.hornta.carbon.BaseTabCompleter;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerCompleter extends BaseTabCompleter<User> {
  private Main main;
  private Predicate<User> filter;

  public PlayerCompleter(Main main) {
    this.main = main;
  }

  public PlayerCompleter(Main main, Predicate<User> filter) {
    this.main = main;
    this.filter = filter;
  }

  @Override
  public List<User> getItems(String argument) {
    Stream<User> userStream = main.getUserManager()
      .getUsers()
      .values()
      .stream()
      .filter(user -> user.getPlayer().getName().toLowerCase(Locale.ENGLISH).startsWith(argument.toLowerCase(Locale.ENGLISH)));

    if(filter != null) {
      userStream = userStream.filter(filter);
    }

    return userStream.collect(Collectors.toList());
  }

  @Override
  public List<String> toSuggestions(List<User> items) {
    return items.stream().map(user -> user.getPlayer().getName()).collect(Collectors.toList());
  }
}

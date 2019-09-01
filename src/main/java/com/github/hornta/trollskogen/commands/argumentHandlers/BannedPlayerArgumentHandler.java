package com.github.hornta.trollskogen.commands.argumentHandlers;

import com.github.hornta.completers.IArgumentHandler;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.PrefixMatcher;
import com.github.hornta.trollskogen.User;
import com.github.hornta.trollskogen.events.NewUserEvent;
import com.github.hornta.trollskogen.events.ReadUsersEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class PlayerArgumentHandler implements IArgumentHandler, Listener {
  private Main main;
  private Set<User> parsedUsers = new HashSet<>();
  private PrefixMatcher allUsernames = new PrefixMatcher();

  public PlayerArgumentHandler(Main main) {
    this.main = main;
    Bukkit.getServer().getPluginManager().registerEvents(this, main);
  }

  @EventHandler
  private void onReadUsers(ReadUsersEvent event) {
    for(User user : main.getUserManager().getUsers().values()) {
      parseUser(user);
    }
  }

  @EventHandler
  private void onNewUser(NewUserEvent event) {
    parseUser(event.getUser());
  }

  private void parseUser(User user) {
    if(parsedUsers.contains(user)) {
      return;
    }

    allUsernames.insert(user.getLastSeenAs());
    parsedUsers.add(user);
  }

  @Override
  public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
    return allUsernames.find(argument);
  }

  @Override
  public boolean test(Set<String> items, String argument) {
    return false;
  }
}

package com.github.hornta.trollskogen.completers;

import com.github.hornta.BaseTabCompleter;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.PrefixMatcher;
import com.github.hornta.trollskogen.User;
import com.github.hornta.trollskogen.events.BanUserEvent;
import com.github.hornta.trollskogen.events.NewUserEvent;
import com.github.hornta.trollskogen.events.ReadUsersEvent;
import com.github.hornta.trollskogen.events.UnbanUserEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BannedPlayerCompleter implements BaseTabCompleter, Listener {
  private Main main;

  // keep track of which users has already been processed
  private Set<User> parsedUsers = new HashSet<>();
  private PrefixMatcher bannedUsernames = new PrefixMatcher();

  public BannedPlayerCompleter(Main main) {
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

  @EventHandler
  private void onBanUser(BanUserEvent event) {
    bannedUsernames.insert(event.getUser().getLastSeenAs());
  }

  @EventHandler
  private void onUnbanUser(UnbanUserEvent event) {
    bannedUsernames.delete(event.getUser().getLastSeenAs());
  }

  private void parseUser(User user) {
    if(parsedUsers.contains(user)) {
      return;
    }

    if(user.isBanned()) {
      bannedUsernames.insert(user.getLastSeenAs());
    }

    parsedUsers.add(user);
  }

  @Override
  public List<String> getItems(CommandSender sender, String[] arguments) {
    return bannedUsernames.find(arguments[0]);
  }
}

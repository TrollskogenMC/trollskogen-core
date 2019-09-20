package com.github.hornta.trollskogen.commands.argumentHandlers;

import com.github.hornta.carbon.ValidationResult;
import com.github.hornta.carbon.completers.IArgumentHandler;
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
import java.util.Set;

public class BannedPlayerArgumentHandler implements IArgumentHandler, Listener {
  private Main main;
  private Set<User> parsedUsers = new HashSet<>();
  private PrefixMatcher bannedUsernames = new PrefixMatcher();

  public BannedPlayerArgumentHandler(Main main) {
    this.main = main;
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
  public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
    return bannedUsernames.find(argument);
  }

  @Override
  public boolean test(Set<String> items, String argument) {
    return items.contains(argument);
  }

  @Override
  public void whenInvalid(ValidationResult validationResult) {
    main.getMessageManager().sendMessage(validationResult.getCommandSender(), "player-not-found");
  }
}

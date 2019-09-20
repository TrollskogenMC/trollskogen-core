package com.github.hornta.trollskogen.commands.argumentHandlers;

import com.github.hornta.carbon.ValidationResult;
import com.github.hornta.carbon.completers.IArgumentHandler;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.PrefixMatcher;
import com.github.hornta.trollskogen.User;
import com.github.hornta.trollskogen.events.*;
import com.github.hornta.trollskogen.homes.Home;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class OpenHomePlayersArgumentHandler implements IArgumentHandler, Listener {
  private Main main;
  private PrefixMatcher allUsernames;

  public OpenHomePlayersArgumentHandler(Main main) {
    this.main = main;
    this.allUsernames = new PrefixMatcher();
  }

  @EventHandler
  void onReadUser(ReadUsersEvent event) {
    for(User user : main.getUserManager().getUsers().values()) {
      if(user.hasOpenHomes() && !user.isBanned()) {
        allUsernames.insert(user.getLastSeenAs());
      }
    }
  }

  @EventHandler
  void onOpenHome(OpenHomeEvent event) {
    allUsernames.insert(event.getUser().getLastSeenAs());
  }

  @EventHandler
  void onCloseHome(CloseHomeEvent event) {
    if(!event.getUser().hasOpenHomes()) {
      allUsernames.delete(event.getUser().getLastSeenAs());
    }
  }

  @EventHandler
  void onBanUser(BanUserEvent event) {
    allUsernames.delete(event.getUser().getLastSeenAs());
  }

  @EventHandler
  void onUnbanUser(UnbanUserEvent event) {
    if(event.getUser().hasOpenHomes()) {
      allUsernames.insert(event.getUser().getLastSeenAs());
    }
  }

  @Override
  public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
    return allUsernames.find(argument).stream().filter((String username) -> {
      User user = main.getUser(sender);
      return !username.equals(user.getLastSeenAs());
    }).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  public boolean test(Set<String> items, String argument) {
    return items.contains(argument);
  }

  @Override
  public void whenInvalid(ValidationResult validationResult) {
    main.getMessageManager().sendMessage(validationResult.getCommandSender(), "player_open_home_not_found");
  }
}

package com.github.hornta.trollskogen.commands.argumentHandlers;

import com.github.hornta.carbon.ValidationResult;
import com.github.hornta.carbon.completers.IArgumentHandler;
import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.PrefixMatcher;
import com.github.hornta.trollskogen.users.UserObject;
import com.github.hornta.trollskogen.homes.events.CloseHomeEvent;
import com.github.hornta.trollskogen.homes.events.LoadHomesEvent;
import com.github.hornta.trollskogen.homes.events.OpenHomeEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.LinkedHashSet;
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
  void onLoadHomes(LoadHomesEvent event) {
    for(UserObject user : main.getUserManager().getUsers()) {
      if(main.getHomeManager().hasOpenHomes(user)) {
        allUsernames.insert(user.getName());
      }
    }
  }

  @EventHandler
  void onOpenHome(OpenHomeEvent event) {
    UserObject user = main.getUser(event.getHome().getOwner());
    allUsernames.insert(user.getName());
  }

  @EventHandler
  void onCloseHome(CloseHomeEvent event) {
    UserObject user = main.getUser(event.getHome().getOwner());
    if(!main.getHomeManager().hasOpenHomes(user)) {
      allUsernames.delete(user.getName());
    }
  }

  @Override
  public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
    return allUsernames.find(argument).stream().filter((String username) -> {
      UserObject user = main.getUser((Player) sender);
      return !username.equals(user.getName());
    }).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  public boolean test(Set<String> items, String argument) {
    return items.contains(argument);
  }

  @Override
  public void whenInvalid(ValidationResult validationResult) {
    MessageManager.sendMessage(validationResult.getCommandSender(), MessageKey.PLAYER_OPEN_HOME_NOT_FOUND);
  }
}

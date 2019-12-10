package com.github.hornta.trollskogen.commands.argumentHandlers;

import com.github.hornta.carbon.ValidationResult;
import com.github.hornta.carbon.completers.IArgumentHandler;
import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.PrefixMatcher;
import com.github.hornta.trollskogen.users.UserObject;
import com.github.hornta.trollskogen.users.events.LoadUsersEvent;
import com.github.hornta.trollskogen.users.events.NewUserEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class PlayerArgumentHandler implements IArgumentHandler, Listener {
  private Main main;
  private Set<UserObject> parsedUsers = new HashSet<>();
  private PrefixMatcher allUsernames = new PrefixMatcher();

  public PlayerArgumentHandler(Main main) {
    this.main = main;
    Bukkit.getServer().getPluginManager().registerEvents(this, main);
  }

  @EventHandler
  private void onReadUsers(LoadUsersEvent event) {
    for(UserObject user : main.getUserManager().getUsers()) {
      parseUser(user);
    }
  }

  @EventHandler
  private void onNewUser(NewUserEvent event) {
    parseUser(event.getUserObject());
  }

  private void parseUser(UserObject user) {
    if(parsedUsers.contains(user)) {
      return;
    }

    allUsernames.insert(user.getName());
    parsedUsers.add(user);
  }

  @Override
  public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
    return allUsernames.find(argument);
  }

  @Override
  public boolean test(Set<String> items, String argument) {
    return items.contains(argument);
  }

  @Override
  public void whenInvalid(ValidationResult validationResult) {
    MessageManager.sendMessage(validationResult.getCommandSender(), MessageKey.PLAYER_NOT_FOUND);
  }
}

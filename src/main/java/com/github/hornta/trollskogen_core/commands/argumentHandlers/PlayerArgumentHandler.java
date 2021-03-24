package com.github.hornta.trollskogen_core.commands.argumentHandlers;

import com.github.hornta.commando.ValidationResult;
import com.github.hornta.commando.completers.IArgumentHandler;
import se.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.MessageKey;
import com.github.hornta.trollskogen_core.PrefixMatcher;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_core.users.events.LoadUsersEvent;
import com.github.hornta.trollskogen_core.users.events.NewUserEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class PlayerArgumentHandler implements IArgumentHandler, Listener {
  private final Set<UserObject> parsedUsers = new HashSet<>();
  private final PrefixMatcher allUsernames = new PrefixMatcher();

  public PlayerArgumentHandler(TrollskogenCorePlugin main) {
    Bukkit.getServer().getPluginManager().registerEvents(this, main);
  }

  @EventHandler
  private void onReadUsers(LoadUsersEvent event) {
    for (var user : TrollskogenCorePlugin.getUserManager().getUsers()) {
      parseUser(user);
    }
  }

  @EventHandler
  private void onNewUser(NewUserEvent event) {
    parseUser(event.getUserObject());
  }

  private void parseUser(UserObject user) {
    if (parsedUsers.contains(user)) {
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

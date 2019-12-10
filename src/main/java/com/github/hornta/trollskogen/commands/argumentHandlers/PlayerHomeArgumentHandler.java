package com.github.hornta.trollskogen.commands.argumentHandlers;

import com.github.hornta.carbon.ValidationResult;
import com.github.hornta.carbon.completers.IArgumentHandler;
import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.users.UserObject;
import com.github.hornta.trollskogen.homes.Home;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerHomeArgumentHandler implements IArgumentHandler {
  private Main main;

  public PlayerHomeArgumentHandler(Main main) {
    this.main = main;
  }

  @Override
  public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
    UserObject user = main.getUser(prevArgs[0]);

    return main
      .getHomeManager()
      .getHomes(user.getId())
      .stream()
      .filter(h -> h.getName().toLowerCase(Locale.ENGLISH).startsWith(argument.toLowerCase(Locale.ENGLISH)))
      .map(Home::getName)
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  public boolean test(Set<String> items, String argument) {
    return items.contains(argument);
  }

  @Override
  public void whenInvalid(ValidationResult validationResult) {
    MessageManager.setValue("player_name", validationResult.getPrevArgs()[0]);
    MessageManager.setValue("home_name", validationResult.getValue());
    MessageManager.sendMessage(validationResult.getCommandSender(), MessageKey.PLAYER_HOME_NOT_FOUND);
  }
}

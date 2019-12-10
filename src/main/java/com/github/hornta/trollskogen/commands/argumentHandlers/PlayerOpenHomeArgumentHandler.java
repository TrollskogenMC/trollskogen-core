package com.github.hornta.trollskogen.commands.argumentHandlers;

import com.github.hornta.carbon.ValidationResult;
import com.github.hornta.carbon.completers.IArgumentHandler;
import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.users.UserObject;
import com.github.hornta.trollskogen.homes.Home;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerOpenHomeArgumentHandler implements IArgumentHandler {
  private Main main;

  public PlayerOpenHomeArgumentHandler(Main main) {
    this.main = main;
  }

  @Override
  public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
    UserObject user = main.getUser(prevArgs[0]);

    return main
      .getHomeManager()
      .getHomes(user.getId())
      .stream()
      .filter(Home::isPublic)
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
    UserObject user = main.getUser(validationResult.getPrevArgs()[0]);

    MessageKey message = MessageKey.OPEN_HOME_NOT_FOUND;
    if(main.getHomeManager().getHomes(user.getId()).isEmpty()) {
      message = MessageKey.OPEN_HOME_HOMELESS_NOT_FOUND;
    }

    MessageManager.sendMessage(validationResult.getCommandSender(), message);
  }
}

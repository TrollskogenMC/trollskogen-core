package com.github.hornta.trollskogen.commands.argumentHandlers;

import com.github.hornta.carbon.ValidationResult;
import com.github.hornta.carbon.completers.IArgumentHandler;
import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.users.UserObject;
import com.github.hornta.trollskogen.homes.Home;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class HomeArgumentHandler implements IArgumentHandler {
  private Main main;

  public HomeArgumentHandler(Main main) {
    this.main = main;
  }

  @Override
  public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
    UserObject user = main.getUser((Player) sender);
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
    MessageKey message = MessageKey.HOME_NOT_FOUND;

    UserObject user = main.getUser((Player) validationResult.getCommandSender());
    if(main.getHomeManager().getHomes(user.getId()).isEmpty()) {
      message = MessageKey.PLAYER_NOT_SET_HOME;
    }

    MessageManager.setValue("home_name", validationResult.getValue());
    MessageManager.sendMessage(validationResult.getCommandSender(), message);
  }
}

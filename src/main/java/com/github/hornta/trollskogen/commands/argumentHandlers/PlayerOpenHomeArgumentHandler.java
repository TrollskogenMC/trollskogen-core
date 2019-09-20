package com.github.hornta.trollskogen.commands.argumentHandlers;

import com.github.hornta.carbon.ValidationResult;
import com.github.hornta.carbon.completers.IArgumentHandler;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
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
    User user = main.getUser(prevArgs[0]);

    return user
      .getHomes()
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
    main.getMessageManager().setValue("player_name", validationResult.getPrevArgs()[0]);
    main.getMessageManager().setValue("home_name", validationResult.getValue());
    User user = main.getUser(validationResult.getPrevArgs()[0]);

    String message = "open_home_not_found";
    if(user.getHomes().isEmpty()) {
      message = "open_home_homeless_not_found";
    }

    main.getMessageManager().sendMessage(validationResult.getCommandSender(), message);
  }
}

package com.github.hornta.trollskogen.commands.argumentHandlers;

import com.github.hornta.carbon.ValidationResult;
import com.github.hornta.carbon.completers.IArgumentHandler;
import com.github.hornta.trollskogen.Main;
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
    Player player = (Player) sender;
    return main.getUser(player.getUniqueId())
      .getHomes()
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
    String message = "home_not_found";

    if(main.getUser(validationResult.getCommandSender()).getHomes().isEmpty()) {
      message = "player_not_set_home";
    }

    main.getMessageManager().setValue("home_name", validationResult.getValue());

    main.getMessageManager().sendMessage(validationResult.getCommandSender(), message);
  }
}

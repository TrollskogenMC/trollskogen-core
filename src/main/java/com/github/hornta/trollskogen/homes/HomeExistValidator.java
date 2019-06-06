package com.github.hornta.trollskogen.homes;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ValidationHandler;

import java.util.List;

public class HomeExistValidator extends ValidationHandler {
  private Main main;

  public HomeExistValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(CommandSender commandSender, String[] args) {
    User user = main.getUser(commandSender);
    return user.getHome(args[0]) != null;
  }

  public boolean testPlayerHome(CommandSender sender, String[] arguments) {
    User user = main.getUser(arguments[0]);
    return user.getHome(arguments[1]) != null;
  }

  @Override
  public String getErrorMessage(CommandSender commandSender, String[] arguments) {
    if(arguments.length == 2) {
      return "player_home_not_found";
    }

    if(main.getUser(commandSender).getHomes().isEmpty()) {
      return "player_not_set_home";
    }

    return "home_not_found";
  }

  @Override
  public void setMessageValues(CommandSender commandSender, String[] args) {
    if(args.length == 2) {
      main.getMessageManager().setValue("player_name", args[0]);
      main.getMessageManager().setValue("home_name", args[1]);
    } else {
      main.getMessageManager().setValue("home_name", args[0]);
    }
  }
}

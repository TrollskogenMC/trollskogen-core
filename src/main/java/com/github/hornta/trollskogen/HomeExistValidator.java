package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.ArgumentValidator;

public class HomeExistValidator extends ArgumentValidator {
  private Main main;

  HomeExistValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(CommandSender commandSender, String arg) {
    User user = main.getUser(commandSender);
    return user.getHome(arg) != null;
  }

  @Override
  public String getErrorMessage(CommandSender commandSender) {
    if(main.getUser(commandSender).getHomes().isEmpty()) {
      return "player_not_set_home";
    }

    return "home_not_found";
  }

  @Override
  public void setMessageValues(CommandSender commandSender, String arg) {
    main.getMessageManager().setValue("home_name", arg);
  }
}

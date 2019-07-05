package com.github.hornta.trollskogen.homes;

import com.github.hornta.ValidationHandler;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import org.bukkit.command.CommandSender;

public class HomeExistValidator implements ValidationHandler {
  private Main main;

  public HomeExistValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(CommandSender commandSender, String[] args) {
    User user = main.getUser(commandSender);
    return user.getHome(args[0]) != null;
  }

  @Override
  public void whenInvalid(CommandSender commandSender, String[] args) {
    String message = "home_not_found";

    if(main.getUser(commandSender).getHomes().isEmpty()) {
      message = "player_not_set_home";
    }

    main.getMessageManager().setValue("home_name", args[0]);

    main.getMessageManager().sendMessage(commandSender, message);
  }
}

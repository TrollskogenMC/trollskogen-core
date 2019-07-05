package com.github.hornta.trollskogen.homes;

import com.github.hornta.ValidationHandler;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import org.bukkit.command.CommandSender;

public class PlayerHomeExistValidator implements ValidationHandler {
  private Main main;

  public PlayerHomeExistValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(CommandSender commandSender, String[] args) {
    User user = main.getUser(args[0]);
    return user.getHome(args[1]) != null;
  }

  @Override
  public void whenInvalid(CommandSender commandSender, String[] args) {
    main.getMessageManager().setValue("player_name", args[0]);
    main.getMessageManager().setValue("home_name", args[1]);
    main.getMessageManager().sendMessage(commandSender, "player_home_not_found");
  }
}

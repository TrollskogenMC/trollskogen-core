package com.github.hornta.trollskogen.commands;

import com.github.hornta.carbon.ICommandHandler;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import com.github.hornta.trollskogen.events.CloseHomeEvent;
import com.github.hornta.trollskogen.events.OpenHomeEvent;
import com.github.hornta.trollskogen.homes.Home;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandCloseHome implements ICommandHandler {
  private Main main;

  public CommandCloseHome(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int i) {
    User user = main.getUser(commandSender);
    Home home = user.getHome(args[0]);

    if(!home.isPublic()) {
      main.getMessageManager().setValue("home_name", home.getName());
      main.getMessageManager().sendMessage(commandSender, "close_home_closed");
      return;
    }

    user.closeHome(home);
    Bukkit.getPluginManager().callEvent(new CloseHomeEvent(user, home));
    main.getMessageManager().setValue("home_name", home.getName());
    main.getMessageManager().sendMessage(commandSender, "close_home");
  }
}

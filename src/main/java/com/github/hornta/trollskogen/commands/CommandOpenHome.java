package com.github.hornta.trollskogen.commands;

import com.github.hornta.carbon.ICommandHandler;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import com.github.hornta.trollskogen.events.OpenHomeEvent;
import com.github.hornta.trollskogen.homes.Home;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandOpenHome implements ICommandHandler {
  private Main main;

  public CommandOpenHome(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int i) {
    User user = main.getUser(commandSender);
    Home home = user.getHome(args[0]);

    if(home.isPublic()) {
      main.getMessageManager().setValue("home_name", home.getName());
      main.getMessageManager().sendMessage(commandSender, "open_home_opened");
      return;
    }

    if(main.getUser(commandSender).getHomes().stream().filter((Home h) -> ((Home) h).isPublic()).count() == 1) {
      main.getMessageManager().setValue("max_open_homes", 1);
      main.getMessageManager().sendMessage(commandSender, "open_home_restriction");
      return;
    }

    user.openHome(home);
    Bukkit.getPluginManager().callEvent(new OpenHomeEvent(user, home));
    main.getMessageManager().setValue("home_name", home.getName());
    main.getMessageManager().sendMessage(commandSender, "open_home");
  }
}

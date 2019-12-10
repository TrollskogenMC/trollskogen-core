package com.github.hornta.trollskogen.homes.commands;

import com.github.hornta.carbon.ICommandHandler;
import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.users.UserObject;
import com.github.hornta.trollskogen.homes.Home;
import com.github.hornta.trollskogen.homes.events.RequestOpenHomeEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class CommandOpenHome implements ICommandHandler {
  private Main main;

  public CommandOpenHome(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int i) {
    UserObject user = main.getUser((Player) commandSender);
    Home home = main.getHomeManager().getHome(args[0], user.getId());

    if(home.isPublic()) {
      MessageManager.setValue("home_name", home.getName());
      MessageManager.sendMessage(commandSender, MessageKey.OPEN_HOME_OPENED);
      return;
    }

    if(main.getHomeManager().getHomes(user.getId()).stream().filter(Home::isPublic).count() == 1) {
      MessageManager.setValue("max_open_homes", 1);
      MessageManager.sendMessage(commandSender, MessageKey.OPEN_HOME_RESTRICTION);
      return;
    }

    Event event = new RequestOpenHomeEvent(home);
    Bukkit.getPluginManager().callEvent(event);

    // TODO: Callback for messages
    MessageManager.setValue("home_name", home.getName());
    MessageManager.sendMessage(commandSender, MessageKey.OPEN_HOME);
  }
}

package com.github.hornta.trollskogen.homes.commands;

import com.github.hornta.carbon.ICommandHandler;
import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.users.UserObject;
import com.github.hornta.trollskogen.homes.Home;
import com.github.hornta.trollskogen.homes.events.RequestToggleCommandsEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class CommandHomeToggleCommands implements ICommandHandler {
  private Main main;

  public CommandHomeToggleCommands(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int i) {
    UserObject user = main.getUser((Player) commandSender);
    Home home = main.getHomeManager().getHome(args[0], user.getId());
    Event event = new RequestToggleCommandsEvent(home);
    Bukkit.getPluginManager().callEvent(event);
    if(home.isAllowCommands()) {
      MessageManager.sendMessage(commandSender, MessageKey.TOGGLE_HOME_COMMANDS_ALLOW);
    } else {
      MessageManager.sendMessage(commandSender, MessageKey.TOGGLE_HOME_COMMANDS_DENY);
    }
  }
}

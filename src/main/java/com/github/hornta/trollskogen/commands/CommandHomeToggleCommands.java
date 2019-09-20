package com.github.hornta.trollskogen.commands;

import com.github.hornta.carbon.ICommandHandler;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import com.github.hornta.trollskogen.events.OpenHomeEvent;
import com.github.hornta.trollskogen.homes.Home;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandHomeToggleCommands implements ICommandHandler {
  private Main main;

  public CommandHomeToggleCommands(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int i) {
    User user = main.getUser(commandSender);
    Home home = user.getHome(args[0]);

    home.toggleAllowCommands();

    if(home.isAllowCommands()) {
      main.getMessageManager().sendMessage(commandSender, "toggle_home_cmds_allow");
    } else {
      main.getMessageManager().sendMessage(commandSender, "toggle_home_cmds_deny");
    }
  }
}

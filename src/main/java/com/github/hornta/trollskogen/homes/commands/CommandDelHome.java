package com.github.hornta.trollskogen.homes.commands;

import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.users.UserObject;
import com.github.hornta.trollskogen.homes.Home;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.homes.events.RequestDeleteHomeEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;
import org.bukkit.entity.Player;

public class CommandDelHome implements ICommandHandler {
  private Main main;

  public CommandDelHome(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    UserObject user = main.getUser((Player)sender);
    Home home = main.getHomeManager().getHome(args[0], user.getId());
    Bukkit.getPluginManager().callEvent(new RequestDeleteHomeEvent(home));
    MessageManager.setValue("home_name", home.getName());
    MessageManager.sendMessage(sender, MessageKey.HOME_DELETED);
  }
}

package com.github.hornta.trollskogen.homes.commands;

import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.users.UserObject;
import com.github.hornta.trollskogen.homes.Home;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.homes.events.RequestSetHomeEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.hornta.carbon.ICommandHandler;
import org.bukkit.event.Event;

public class CommandSetHome implements ICommandHandler {
  private Main main;

  public CommandSetHome(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    Player player = (Player)sender;
    UserObject user = main.getUser(player);
    Home existingHome = main.getHomeManager().getHome(args[0], user.getId());

    int maxHomes = main.getMaxHomes(user);
    if(existingHome == null && main.getHomeManager().getHomes(user.getId()).size() == maxHomes) {
      MessageManager.setValue("num_homes", maxHomes);
      MessageManager.sendMessage(sender, MessageKey.MAX_HOMES);
      return;
    }

    Event event = new RequestSetHomeEvent(args[0], player.getLocation(), user);
    Bukkit.getPluginManager().callEvent(event);
    MessageManager.sendMessage(sender, MessageKey.HOME_SET);
  }
}

package com.github.hornta.trollskogen.commands;

import com.github.hornta.trollskogen.homes.Home;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import com.github.hornta.carbon.ICommandHandler;

public class CommandAHome implements ICommandHandler {
  private Main main;

  public CommandAHome(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    User user = main.getUser(args[0]);
    Home home = user.getHome(args[1]);
    ((Player)sender).teleport(home.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
  }
}

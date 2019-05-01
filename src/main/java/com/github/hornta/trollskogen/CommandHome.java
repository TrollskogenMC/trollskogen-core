package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import se.hornta.carbon.ICommandHandler;

public class CommandHome implements ICommandHandler {
  private Main main;

  CommandHome(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args) {
    User user = main.getUser(sender);

    Home home = user.getHome(args[0]);

    if(user.getHomes().indexOf(home) >= user.getMaxHomes()) {
      main.getMessageManager().sendMessage(sender, "home_maximum_usage");
      return;
    }

    ((Player)user.getPlayer()).teleport(home.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
  }
}

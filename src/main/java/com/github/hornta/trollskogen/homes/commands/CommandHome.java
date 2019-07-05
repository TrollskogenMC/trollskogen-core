package com.github.hornta.trollskogen.homes.commands;

import com.github.hornta.trollskogen.homes.Home;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import com.github.hornta.ICommandHandler;

public class CommandHome implements ICommandHandler {
  private Main main;

  public CommandHome(Main main) {
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

package com.github.hornta.trollskogen.homes.commands;

import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.users.UserObject;
import com.github.hornta.trollskogen.homes.Home;
import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import com.github.hornta.carbon.ICommandHandler;

public class CommandHome implements ICommandHandler {
  private Main main;

  public CommandHome(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    UserObject user = main.getUser((Player) sender);
    Home home = main.getHomeManager().getHome(args[0], user.getId());

    if(main.getHomeManager().getHomes(user.getId()).indexOf(home) >= main.getMaxHomes(user)) {
      MessageManager.sendMessage(sender, MessageKey.HOME_MAXIMUM_USAGE);
      return;
    }

    ((Player) sender).teleport(home.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
  }
}

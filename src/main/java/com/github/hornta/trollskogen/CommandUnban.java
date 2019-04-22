package com.github.hornta.trollskogen;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

import java.util.logging.Level;

public class CommandUnban implements ICommandHandler {
  private Main main;

  CommandUnban(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    User user = main.getUser(args[0]);
    if(!user.isBanned()) {
      main.getMessageManager().setValue("player", user.getLastSeenAs());
      main.getMessageManager().sendMessage(commandSender, "unban_not_banned");
      return;
    }

    user.unban();

    main.getMessageManager().setValue("player", user.getLastSeenAs());
    String unbanMessage = main.getMessageManager().getMessage("player_unban");
    Bukkit.broadcastMessage(unbanMessage);
    Bukkit.getLogger().log(Level.INFO, unbanMessage);
  }
}

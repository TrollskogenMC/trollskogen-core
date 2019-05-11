package com.github.hornta.trollskogen.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import com.github.hornta.trollskogen.events.UserVerifiedEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

import java.util.UUID;
import java.util.logging.Level;

public class CommandCallEvent implements ICommandHandler {
  private Main main;

  public CommandCallEvent(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args) {
    Class eventClass;
    try {
      eventClass = Class.forName(args[0]);
    } catch (ClassNotFoundException ex) {
      Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
      return;
    }

    if(eventClass.equals(UserVerifiedEvent.class) && args.length == 3) {
      User user = main.getUser(UUID.fromString(args[1]));
      boolean isVerified;

      switch (args[2]) {
        case "true":
          isVerified = true;
          break;
        case "false":
          isVerified = false;
          break;
        default:
          Bukkit.getLogger().log(Level.SEVERE, "Received unexpected value for `isVerified`: " + args[2]);
          return;
      }
      Bukkit.getPluginManager().callEvent(new UserVerifiedEvent(user, isVerified));
    }
  }
}

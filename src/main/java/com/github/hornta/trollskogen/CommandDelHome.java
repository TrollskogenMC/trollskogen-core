package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

public class CommandDelHome implements ICommandHandler {
  private Main main;

  CommandDelHome(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args) {
    Home home = main.getUser(sender).deleteHome(args[0]);
    main.getMessageManager().setValue("home_name", home.getName());
    main.getMessageManager().sendMessage(sender, "home_deleted");
  }
}

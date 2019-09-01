package com.github.hornta.trollskogen.commands;

import com.github.hornta.trollskogen.homes.Home;
import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;

public class CommandDelHome implements ICommandHandler {
  private Main main;

  public CommandDelHome(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    Home home = main.getUser(sender).deleteHome(args[0]);
    main.getMessageManager().setValue("home_name", home.getName());
    main.getMessageManager().sendMessage(sender, "home_deleted");
  }
}

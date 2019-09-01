package com.github.hornta.trollskogen.commands;

import com.github.hornta.carbon.ICommandHandler;
import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetStarterKit implements ICommandHandler {
  private Main main;

  public CommandSetStarterKit(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] strings, int typedArgs) {
    Player player = (Player)commandSender;
    main.getTrollskogenConfig().setStarterKit(player.getInventory());
    main.getMessageManager().sendMessage(commandSender, "setstarterkit-success");
  }
}

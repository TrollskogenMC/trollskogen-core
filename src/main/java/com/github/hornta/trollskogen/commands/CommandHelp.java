package com.github.hornta.trollskogen.commands;

import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandHelp implements ICommandHandler {
  private Main plugin;

  public CommandHelp(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    Player player = null;
    if(sender instanceof Player) {
      player = (Player)sender;
    }

    List<String> helpTexts = plugin.getCarbon().getHelpTexts(player);
    plugin.getMessageManager().sendMessage(sender, "help_title");
    for (String helpText : helpTexts) {
      sender.sendMessage(plugin.getMessageManager().transformColors(helpText));
    }
  }
}

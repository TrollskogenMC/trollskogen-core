package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

import java.util.List;

public class CommandHelp implements ICommandHandler {
  private Main plugin;

  public CommandHelp(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public void handle(CommandSender sender, String[] args) {
    List<String> helpTexts = plugin.getCarbon().getCommandManager().getAllHelpTexts();
    plugin.getMessageManager().sendMessage(sender, "help_title");
    for (String helpText : helpTexts) {
      sender.sendMessage(plugin.getMessageManager().transformColors(helpText));
    }
  }
}

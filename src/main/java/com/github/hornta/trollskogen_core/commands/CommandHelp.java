package com.github.hornta.trollskogen_core.commands;

import com.github.hornta.commando.ICommandHandler;
import se.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.MessageKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandHelp implements ICommandHandler {
  private final TrollskogenCorePlugin plugin;

  public CommandHelp(TrollskogenCorePlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    Player player = null;
    if(sender instanceof Player) {
      player = (Player)sender;
    }

    List<String> helpTexts = plugin.getCarbon().getHelpTexts(player);
    MessageManager.sendMessage(sender, MessageKey.HELP_TITLE);
    for (String helpText : helpTexts) {
      sender.sendMessage(helpText);
    }
  }
}

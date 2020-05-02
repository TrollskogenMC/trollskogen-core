package com.github.hornta.trollskogen_core.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.MessageKey;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMaintenance implements ICommandHandler {
  private final TrollskogenCorePlugin main;

  public CommandMaintenance(TrollskogenCorePlugin main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    main.toggleMaintenance();
    if(main.isMaintenance()) {
      for(Player player : Bukkit.getOnlinePlayers()) {
        if(!main.isAllowedMaintenance(player)) {
          player.kickPlayer(MessageManager.getMessage(MessageKey.KICK_MAINTENANCE));
        }
      }

      MessageManager.sendMessage(sender, MessageKey.TOGGLE_MAINTENANCE_ON);
    } else {
      MessageManager.sendMessage(sender, MessageKey.TOGGLE_MAINTENANCE_OFF);
    }
  }
}

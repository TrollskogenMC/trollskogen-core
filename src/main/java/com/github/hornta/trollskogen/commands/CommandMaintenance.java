package com.github.hornta.trollskogen.commands;

import com.github.hornta.trollskogen.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

public class CommandMaintenance implements ICommandHandler {
  private Main main;

  public CommandMaintenance(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args) {
    main.toggleMaintenance();
    if(main.isMaintenance()) {
      for(Player player : Bukkit.getOnlinePlayers()) {
        if(!main.isAllowedMaintenance(player)) {
          player.kickPlayer(main.getMessageManager().getMessage("kick_maintenance"));
        }
      }

      main.getMessageManager().sendMessage("toggle_maintenance_on", sender);
    } else {
      main.getMessageManager().sendMessage("toggle_maintenance_off", sender);
    }
  }
}

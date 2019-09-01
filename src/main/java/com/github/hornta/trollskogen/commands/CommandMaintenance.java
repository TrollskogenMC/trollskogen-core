package com.github.hornta.trollskogen.commands;

import com.github.hornta.carbon.ICommandHandler;
import com.github.hornta.trollskogen.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMaintenance implements ICommandHandler {
  private Main main;

  public CommandMaintenance(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    main.toggleMaintenance();
    if(main.isMaintenance()) {
      for(Player player : Bukkit.getOnlinePlayers()) {
        if(!main.isAllowedMaintenance(player)) {
          player.kickPlayer(main.getMessageManager().getMessage("kick_maintenance"));
        }
      }

      main.getMessageManager().sendMessage(sender, "toggle_maintenance_on");
    } else {
      main.getMessageManager().sendMessage(sender, "toggle_maintenance_off");
    }
  }
}

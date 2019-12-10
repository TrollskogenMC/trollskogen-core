package com.github.hornta.trollskogen.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.users.UserObject;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;
import org.bukkit.entity.Player;

public class CommandEffectReset implements ICommandHandler {
  private Main main;

  public CommandEffectReset(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    UserObject user = main.getUser((Player) commandSender);
    /*if(user.setSelectedEffect(null, false)) {
      MessageManager.sendMessage(commandSender, "effect_reset");
    } else {
      MessageManager.sendMessage(commandSender, "effect_reset_fail");
    }*/
  }
}

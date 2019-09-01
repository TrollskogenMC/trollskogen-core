package com.github.hornta.trollskogen.effects.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.hornta.ICommandHandler;

public class CommandEffectReset implements ICommandHandler {
  private Main main;

  public CommandEffectReset(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    User user = main.getUser(commandSender);
    if(user.setSelectedEffect(null, false)) {
      main.getMessageManager().sendMessage(commandSender, "effect_reset");
    } else {
      main.getMessageManager().sendMessage(commandSender, "effect_reset_fail");
    }
  }
}

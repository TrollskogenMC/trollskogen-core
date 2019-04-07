package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

public class CommandEffectReset implements ICommandHandler {
  private Main main;

  CommandEffectReset(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    ParticleEffect particleEffect = main.getParticleManager().reset((Player)commandSender);
    if(particleEffect == null) {
      main.getMessageManager().sendMessage(commandSender, "effect_reset_fail");
    } else {
      main.getMessageManager().sendMessage(commandSender, "effect_reset");
    }
  }
}

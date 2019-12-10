package com.github.hornta.trollskogen.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.effects.ParticleEffect;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;

public class CommandEffectUse implements ICommandHandler {
  private Main main;

  public CommandEffectUse(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    ParticleEffect particleEffect = ParticleEffect.getParticleEffect(args[0]);
    /*User user = main.getUser(commandSender);
    if(user.setSelectedEffect(particleEffect, false)) {
      MessageManager.setValue("effect_id", WordUtils.capitalizeFully(particleEffect.name(), new char[]{'_'}));
      MessageManager.sendMessage(commandSender, "effect_set");
    } else {
      MessageManager.sendMessage(commandSender, "effect_set_in_use");
    }*/
  }
}

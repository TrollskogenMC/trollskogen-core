package com.github.hornta.trollskogen.effects.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.effects.ParticleEffect;
import com.github.hornta.trollskogen.User;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

public class CommandEffectUse implements ICommandHandler {
  private Main main;

  public CommandEffectUse(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    ParticleEffect particleEffect = ParticleEffect.getParticleEffect(args[0]);
    User user = main.getUser(commandSender);
    if(user.setSelectedEffect(particleEffect, false)) {
      main.getMessageManager().setValue("effect_id", WordUtils.capitalizeFully(particleEffect.name(), new char[]{'_'}));
      main.getMessageManager().sendMessage(commandSender, "effect_set");
    } else {
      main.getMessageManager().sendMessage(commandSender, "effect_set_in_use");
    }
  }
}

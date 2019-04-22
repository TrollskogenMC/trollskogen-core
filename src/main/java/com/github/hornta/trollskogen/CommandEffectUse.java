package com.github.hornta.trollskogen;

import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

public class CommandEffectUse implements ICommandHandler {
  private Main main;

  CommandEffectUse(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    ParticleEffect particleEffect = ParticleEffect.getParticleEffect(args[0]);
    User user = main.getUser(commandSender);
    if(user.setSelectedEffect(particleEffect)) {
      main.getMessageManager().setValue("effect_id", WordUtils.capitalizeFully(particleEffect.name(), new char[]{'_'}));
      main.getMessageManager().sendMessage(commandSender, "effect_set");
    } else {
      main.getMessageManager().sendMessage(commandSender, "effect_set_in_use");
    }
  }
}

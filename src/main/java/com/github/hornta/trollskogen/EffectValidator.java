package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.ArgumentValidator;

public class EffectValidator extends ArgumentValidator {
  private Main main;

  EffectValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(CommandSender sender, String arg) {
    return ParticleEffect.getParticleEffect(arg) != null;
  }

  @Override
  public void setMessageValues(CommandSender sender, String s) {
    main.getMessageManager().setValue("effect_id", s);
  }

  @Override
  public String getErrorMessage(CommandSender commandSender) {
    return "effect_not_found";
  }
}

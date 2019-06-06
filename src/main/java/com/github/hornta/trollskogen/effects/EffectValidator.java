package com.github.hornta.trollskogen.effects;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.effects.ParticleEffect;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ValidationHandler;

public class EffectValidator extends ValidationHandler {
  private Main main;

  public EffectValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(CommandSender sender, String[] arguments) {
    return ParticleEffect.getParticleEffect(arguments[0]) != null;
  }

  @Override
  public void setMessageValues(CommandSender sender, String[] arguments) {
    main.getMessageManager().setValue("effect_id", arguments[0]);
  }

  @Override
  public String getErrorMessage(CommandSender commandSender, String[] args) {
    return "effect_not_found";
  }
}

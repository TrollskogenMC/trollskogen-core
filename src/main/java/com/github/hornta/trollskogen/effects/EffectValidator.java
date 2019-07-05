package com.github.hornta.trollskogen.effects;

import com.github.hornta.ValidationHandler;
import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;

public class EffectValidator implements ValidationHandler {
  private Main main;

  public EffectValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(CommandSender sender, String[] arguments) {
    return ParticleEffect.getParticleEffect(arguments[0]) != null;
  }

  @Override
  public void whenInvalid(CommandSender sender, String[] arguments) {
    main.getMessageManager().setValue("effect_id", arguments[0]);
    main.getMessageManager().sendMessage(sender, "effect_not_found");
  }
}

package com.github.hornta.trollskogen;

import se.hornta.carbon.ArgumentValidator;

public class EffectValidator extends ArgumentValidator {
  private Main main;

  EffectValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(String arg) {
    return ParticleEffect.getParticleEffect(arg) != null;
  }

  @Override
  public void setMessageValues(String s) {
    main.getMessageManager().setValue("effect_id", s);
  }

  @Override
  public String getErrorMessage() {
    return "effect_not_found";
  }
}

package com.github.hornta.trollskogen;

import se.hornta.carbon.ArgumentValidator;

public class PlayerExistValidator extends ArgumentValidator {
  private Main main;

  public PlayerExistValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(String arg) {
    return main.getUserManager().getUsers().values().stream()
      .anyMatch(p -> p.getLastSeenAs().equals(arg));
  }

  @Override
  public void setMessageValues(String s) {
  }

  @Override
  public String getErrorMessage() {
    return "player-not-found";
  }
}

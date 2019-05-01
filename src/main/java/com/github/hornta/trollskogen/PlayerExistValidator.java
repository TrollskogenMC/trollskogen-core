package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.ArgumentValidator;

public class PlayerExistValidator extends ArgumentValidator {
  private Main main;

  public PlayerExistValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(CommandSender sender, String arg) {
    return main.getUserManager().getUsers().values().stream()
      .anyMatch(p -> p.getLastSeenAs().equals(arg));
  }

  @Override
  public void setMessageValues(CommandSender sender, String s) {
  }

  @Override
  public String getErrorMessage(CommandSender commandSender) {
    return "player-not-found";
  }
}

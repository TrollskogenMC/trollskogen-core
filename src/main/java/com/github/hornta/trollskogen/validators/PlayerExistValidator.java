package com.github.hornta.trollskogen.validators;

import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ValidationHandler;

public class PlayerExistValidator extends ValidationHandler {
  private Main main;

  public PlayerExistValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(CommandSender sender, String[] args) {
    return main.getUserManager().getUsers().values().stream()
      .anyMatch(p -> p.getLastSeenAs().equals(args[0]));
  }

  @Override
  public void setMessageValues(CommandSender sender, String[] s) {
  }

  @Override
  public String getErrorMessage(CommandSender commandSender, String[] args) {
    return "player-not-found";
  }
}

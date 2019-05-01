package com.github.hornta.trollskogen;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ArgumentValidator;

import java.util.UUID;

public class UUIDExistValidator extends ArgumentValidator {
  private Main main;

  public UUIDExistValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(CommandSender sender, String arg) {
    try {
      UUID uuid = UUID.fromString(arg);
      Bukkit.getOfflinePlayer(uuid);
    } catch (IllegalArgumentException e) {
      return false;
    }
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

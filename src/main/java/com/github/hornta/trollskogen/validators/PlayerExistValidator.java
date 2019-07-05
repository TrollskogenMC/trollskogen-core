package com.github.hornta.trollskogen.validators;

import com.github.hornta.ValidationHandler;
import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;

public class PlayerExistValidator implements ValidationHandler {
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
  public void whenInvalid(CommandSender commandSender, String[] args) {
    main.getMessageManager().sendMessage(commandSender, "player-not-found");
  }
}

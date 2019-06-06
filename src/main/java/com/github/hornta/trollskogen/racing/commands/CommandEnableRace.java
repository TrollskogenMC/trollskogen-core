package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

import java.util.HashMap;

public class CommandEnableRace implements ICommandHandler {
  private Main main;
  public CommandEnableRace(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    Race race = main.getRacing().getRace(args[0]);

    if(race.isEditing()) {
      main.getMessageManager().sendMessage(commandSender, "error_enable_race_editing");
      return;
    }

    if(race.isEnabled()) {
      main.getMessageManager().sendMessage(commandSender, "error_enable_race_enabled");
      return;
    }

    race.setEnabled(true);

    main.getRacing().updateRace(race, () -> {
      main.getMessageManager().setValue("race_name", race.getName());
      main.getMessageManager().sendMessage(commandSender, "success_race_enabled");
    });
  }
}

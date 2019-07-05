package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.HandleRequest;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.enums.RaceState;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.google.gson.JsonElement;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.hornta.ICommandHandler;

import java.util.HashMap;

public class CommandDisableRace implements ICommandHandler {
  private Main main;
  public CommandDisableRace(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    Race race = main.getRacing().getRace(args[0]);

    if(race.getState() != RaceState.IDLE) {
      main.getMessageManager().sendMessage(commandSender, "error_disable_race_not_idle");
      return;
    }

    if(!race.isEnabled()) {
      main.getMessageManager().sendMessage(commandSender, "error_disable_race_disabled");
      return;
    }

    race.setEnabled(false);

    main.getRacing().updateRace(race, () -> {
      main.getMessageManager().setValue("race_name", race.getName());
      main.getMessageManager().sendMessage(commandSender, "success_race_disabled");
    });
  }
}

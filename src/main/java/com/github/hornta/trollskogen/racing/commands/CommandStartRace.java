package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.enums.RaceState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.hornta.ICommandHandler;

public class CommandStartRace implements ICommandHandler {
  private Main main;

  public CommandStartRace(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    Race race = Main.getRacing().getRace(args[0]);

    if(!race.isEnabled()) {
      main.getMessageManager().sendMessage(commandSender, "race_is_disabled");
      return;
    }

    if(race.getState() != RaceState.IDLE) {
      main.getMessageManager().sendMessage(commandSender,  "error_start_race_started");
      return;
    }

    if(race.getStartPoints().size() < Main.getTrollskogenConfig().getRequiredStartPoints()) {
      main.getMessageManager().sendMessage(commandSender, "error_start_race_start_points");
      return;
    }

    if(race.getCheckpoints().isEmpty()) {
      main.getMessageManager().sendMessage(commandSender, "error_start_race_check_points");
      return;
    }

    race.start((Player) commandSender);
  }
}

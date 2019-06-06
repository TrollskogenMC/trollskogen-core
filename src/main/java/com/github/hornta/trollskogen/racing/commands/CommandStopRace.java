package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.enums.RaceState;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

public class CommandStopRace implements ICommandHandler {
  private Main main;
  public CommandStopRace(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    Race race = main.getRacing().getRace(args[0]);
    if(race.getState() == RaceState.IDLE) {
      main.getMessageManager().sendMessage(commandSender, "error_stop_race_state");
      return;
    }

    race.stop();
    main.getMessageManager().broadcast("success_stop_race");
  }
}

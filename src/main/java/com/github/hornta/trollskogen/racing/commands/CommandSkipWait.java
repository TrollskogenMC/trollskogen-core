package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.enums.RaceState;
import com.github.hornta.trollskogen.racing.objects.Race;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.hornta.ICommandHandler;

public class CommandSkipWait implements ICommandHandler {
  private Main main;

  public CommandSkipWait(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    Race race = main.getRacing().getRace(args[0]);

    if(race.getState() != RaceState.PREPARING) {
      main.getMessageManager().sendMessage(commandSender,  "error_skipwait_not_started");
      return;
    }

    race.skipToCountdown();
  }
}

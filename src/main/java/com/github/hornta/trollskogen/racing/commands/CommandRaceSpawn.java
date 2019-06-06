package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

public class CommandRaceSpawn implements ICommandHandler {
  private Main main;
  public CommandRaceSpawn(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    Race race = main.getRacing().getRace(args[0]);
    if(race.isEditing() && !commandSender.hasPermission("ts.race.spawn.editing")) {
      main.getMessageManager().sendMessage(commandSender, "race_is_editing");
      return;
    }

    if(!race.isEnabled() && !commandSender.hasPermission("ts.race.spawn.disabled")) {
      main.getMessageManager().sendMessage(commandSender, "race_is_disabled");
      return;
    }

    ((Player)commandSender).teleport(race.getSpawn().add(0.5, 0, 0.5));
  }
}

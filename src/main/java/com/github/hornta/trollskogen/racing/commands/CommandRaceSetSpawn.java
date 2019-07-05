package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.hornta.ICommandHandler;

import java.util.HashMap;

public class CommandRaceSetSpawn implements ICommandHandler {
  private Main main;
  public CommandRaceSetSpawn(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    Race race = main.getRacing().getRace(args[0]);

    if(!race.isEditing()) {
      main.getMessageManager().setValue("race_name", race.getName());
      main.getMessageManager().sendMessage(commandSender, "edit_no_edit_mode");
      return;
    }

    race.setSpawn(((Player) commandSender).getLocation());

    main.getRacing().updateRace(race, () -> {
      main.getMessageManager().setValue("race_name", race.getName());
      main.getMessageManager().setValue("x", race.getSpawn().getX());
      main.getMessageManager().setValue("y", race.getSpawn().getY());
      main.getMessageManager().setValue("z", race.getSpawn().getZ());
      main.getMessageManager().setValue("world", race.getSpawn().getWorld().getName());
      main.getMessageManager().sendMessage(commandSender, "race_spawn_set");
    });
  }
}

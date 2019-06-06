package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.objects.RaceCheckpoint;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

import java.util.HashMap;
import java.util.List;

public class CommandRaceAddPoint implements ICommandHandler {
  private Main main;
  public CommandRaceAddPoint(Main main) {
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

    Player player = (Player)commandSender;

    Location location = player.getLocation().clone();
    location.setX(location.getBlockX());
    location.setY(location.getBlockY());
    location.setZ(location.getBlockZ());

    main.getRacing().addPoint(location, race, (RaceCheckpoint checkPoint) -> {
      main.getMessageManager().setValue("race_name", race.getName());
      main.getMessageManager().setValue("position", checkPoint.getPosition());
      main.getMessageManager().sendMessage(player, "success_race_setpoint");
    });
  }
}

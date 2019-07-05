package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.ICommandHandler;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.events.AddRaceStartPointEvent;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.objects.RaceStartPoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.hornta.ICommandHandler;

public class CommandAddStart implements ICommandHandler {
  private Main main;
  public CommandAddStart(Main main) {
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
    if(race.getCheckpoint(player.getLocation()) != null) {
      main.getMessageManager().sendMessage(commandSender, "error_add_start_occupied");
      return;
    }

    Location location = player.getLocation().clone();
    location.setX(location.getBlockX());
    location.setY(location.getBlockY());
    location.setZ(location.getBlockZ());

    main.getRacing().addRaceStart(location, race, (RaceStartPoint startPoint) -> {
      main.getMessageManager().setValue("race_name", race.getName());
      main.getMessageManager().setValue("position", startPoint.getPosition());
      main.getMessageManager().sendMessage(player, "race_start_point_set");
      Bukkit.getPluginManager().callEvent(new AddRaceStartPointEvent(race, startPoint));
    });
  }
}

package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.events.AddRaceStartPointEvent;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.objects.RaceStartPoint;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

import java.util.HashMap;

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

    main.getRacing().addRaceStart(player.getLocation(), race, (RaceStartPoint startPoint) -> {
      main.getMessageManager().setValue("race_name", race.getName());
      main.getMessageManager().setValue("position", startPoint.getPosition());
      main.getMessageManager().sendMessage(player, "race_start_point_set");
      Bukkit.getPluginManager().callEvent(new AddRaceStartPointEvent(race, startPoint));
    });
  }
}

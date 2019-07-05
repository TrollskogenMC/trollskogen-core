package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.objects.RaceStartPoint;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.hornta.ICommandHandler;

import java.util.HashMap;

public class CommandDelStart implements ICommandHandler {
  private Main main;
  public CommandDelStart(Main main) {
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

    Player player = (Player) commandSender;
    RaceStartPoint startPoint = race.getStartPoint(Integer.parseInt(args[1]));

    main.getRacing().deleteRaceStart(race, startPoint, () -> {
      HashMap<String, String> values = new HashMap<>();
      values.put("race_name", race.getName());
      values.put("position", String.valueOf(startPoint.getPosition()));
      main.getMessageManager().sendMessage(player, "race_start_point_deleted", values);
    });
  }
}

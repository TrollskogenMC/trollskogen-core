package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.objects.RaceCheckpoint;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

import java.util.HashMap;

public class CommandRaceDelPoint implements ICommandHandler {
  private Main main;
  public CommandRaceDelPoint(Main main) {
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

    RaceCheckpoint checkpoint = race.getCheckpoint(Integer.parseInt(args[1]));
    main.getRacing().deletePoint(race, checkpoint, () -> {
      main.getMessageManager().setValue("race_name", race.getName());
      main.getMessageManager().setValue("position", checkpoint.getPosition());

      main.getMessageManager().sendMessage(commandSender, "delete_point_success");
    });
  }
}

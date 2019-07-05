package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.HandleRequest;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.events.ChangeRaceNameEvent;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.google.gson.JsonElement;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.hornta.ICommandHandler;

import java.util.HashMap;

public class CommandSetRaceName implements ICommandHandler {
  private Main main;
  public CommandSetRaceName(Main main) {
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

    String oldName = race.getName();
    race.setName(args[1]);

    main.getRacing().updateRace(race, () -> {
      Bukkit.getPluginManager().callEvent(new ChangeRaceNameEvent(race, oldName));
      main.getMessageManager().setValue("old_name", oldName);
      main.getMessageManager().setValue("new_name", race.getName());
      main.getMessageManager().sendMessage(commandSender, "race_name_set");
    });
  }
}

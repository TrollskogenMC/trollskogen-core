package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.events.EditingRaceEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import com.github.hornta.ICommandHandler;

public class CommandStartEditRace implements ICommandHandler {
  private Main main;
  public CommandStartEditRace(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    Race race = main.getRacing().getRace(args[0]);

    if(race.isEditing()) {
      main.getMessageManager().sendMessage(commandSender, "error_start_edit_already_editing");
      return;
    }

    if(race.isEnabled()) {
      main.getMessageManager().sendMessage(commandSender, "error_start_edit_enabled");
      return;
    }

    race.setEditing(true);

    main.getRacing().updateRace(race, () -> {
      Bukkit.getPluginManager().callEvent(new EditingRaceEvent(race, true));
      main.getMessageManager().sendMessage(commandSender, "start_edit_success");
    });
  }
}

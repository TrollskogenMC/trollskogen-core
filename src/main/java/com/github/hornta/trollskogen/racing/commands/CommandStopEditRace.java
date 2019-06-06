package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.events.EditingRaceEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

public class CommandStopEditRace implements ICommandHandler {
  private Main main;
  public CommandStopEditRace(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    Race race = main.getRacing().getRace(args[0]);
    if(!race.isEditing()) {
      main.getMessageManager().sendMessage(commandSender, "stop_edit_already_editing");
      return;
    }

    race.setEditing(false);

    main.getRacing().updateRace(race,() -> {
      Bukkit.getPluginManager().callEvent(new EditingRaceEvent(race, false));
      main.getMessageManager().sendMessage(commandSender, "stop_edit_success");
    });
  }
}

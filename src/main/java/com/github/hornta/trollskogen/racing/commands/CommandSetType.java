package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.enums.RacingType;
import org.bukkit.command.CommandSender;
import com.github.hornta.ICommandHandler;

public class CommandSetType implements ICommandHandler {
  private Main main;
  public CommandSetType(Main main) {
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

    if(race.getType() == RacingType.fromString(args[1])) {
      main.getMessageManager().setValue("type", race.getType().toString());
      main.getMessageManager().sendMessage(commandSender, "error_set_type_nochange");
      return;
    }

    RacingType oldType = race.getType();
    race.setType(RacingType.fromString(args[1]));

    main.getRacing().updateRace(race, () -> {
      main.getMessageManager().setValue("old_type", oldType.toString());
      main.getMessageManager().setValue("new_type", race.getType().toString());
      main.getMessageManager().sendMessage(commandSender, "race_set_type_success");
    });
  }
}


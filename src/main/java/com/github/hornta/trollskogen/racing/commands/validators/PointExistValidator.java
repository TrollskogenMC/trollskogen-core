package com.github.hornta.trollskogen.racing.commands.validators;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.objects.RaceCheckpoint;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ValidationHandler;

public class PointExistValidator extends ValidationHandler {
  private Main main;
  private boolean shouldExist;

  public PointExistValidator(Main main, boolean shouldExist) {
    this.main = main;
    this.shouldExist = shouldExist;
  }

  @Override
  public void setMessageValues(CommandSender commandSender, String[] arguments) {
    main.getMessageManager().setValue("race_name", arguments[0]);
    main.getMessageManager().setValue("position", arguments[1]);
  }

  @Override
  public boolean test(CommandSender commandSender, String[] arguments) {
    int position;
    try {
      position = Integer.parseInt(arguments[1]);
    } catch(NumberFormatException e) {
      return false;
    }
    Race race = main.getRacing().getRace(arguments[0]);
    boolean hasPoint = raceHasPoint(race, position);
    return (shouldExist && hasPoint) || (!shouldExist && !hasPoint);
  }

  private static boolean raceHasPoint(Race race, int position) {
    for(RaceCheckpoint checkpoint : race.getCheckpoints()) {
      if(checkpoint.getPosition() == position) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String getErrorMessage(CommandSender commandSender, String[] arguments) {
    if(this.shouldExist) {
      return "race_point_not_found";
    } else {
      return "race_point_already_exist";
    }
  }
}

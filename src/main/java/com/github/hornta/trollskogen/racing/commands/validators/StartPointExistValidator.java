package com.github.hornta.trollskogen.racing.commands.validators;

import com.github.hornta.ValidationHandler;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.objects.RaceStartPoint;
import org.bukkit.command.CommandSender;

public class StartPointExistValidator implements ValidationHandler {
  private Main main;
  private boolean shouldExist;

  public StartPointExistValidator(Main main, boolean shouldExist) {
    this.main = main;
    this.shouldExist = shouldExist;
  }

  @Override
  public boolean test(CommandSender commandSender, String[] arguments) {
    Race race = main.getRacing().getRace(arguments[0]);
    int position = Integer.parseInt(arguments[1]);
    boolean hasPoint = raceHasPoint(race, position);
    return (shouldExist && hasPoint) || (!shouldExist && !hasPoint);
  }

  private static boolean raceHasPoint(Race race, int position) {
    for(RaceStartPoint point : race.getStartPoints()) {
      if(point.getPosition() == position) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void whenInvalid(CommandSender sender, String[] arguments) {
    String message;
    if(this.shouldExist) {
      message = "race_start_point_not_found";
    } else {
      message = "race_start_point_already_exist";
    }
    main.getMessageManager().setValue("race_name", arguments[0]);
    main.getMessageManager().setValue("position", arguments[1]);
    main.getMessageManager().sendMessage(sender, message);
  }
}

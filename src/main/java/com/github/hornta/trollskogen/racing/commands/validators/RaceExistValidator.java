package com.github.hornta.trollskogen.racing.commands.validators;

import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ValidationHandler;

public class RaceExistValidator extends ValidationHandler {
  private Main main;
  private boolean shouldExist;

  public RaceExistValidator(Main main, boolean shouldExist) {
    this.main = main;
    this.shouldExist = shouldExist;
  }

  @Override
  public void setMessageValues(CommandSender commandSender, String[] arguments) {
    main.getMessageManager().setValue("race_name", arguments[0]);
  }

  @Override
  public boolean test(CommandSender commandSender, String[] arguments) {
    if(shouldExist) {
      return main.getRacing().hasRace(arguments[0]);
    } else {
      return !main.getRacing().hasRace(arguments[0]);
    }
  }

  @Override
  public String getErrorMessage(CommandSender commandSender, String[] arguments) {
    if(this.shouldExist) {
      return "race_not_found";
    } else {
      return "race_already_exist";
    }
  }
}

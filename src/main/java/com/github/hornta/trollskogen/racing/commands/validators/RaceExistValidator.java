package com.github.hornta.trollskogen.racing.commands.validators;

import com.github.hornta.ValidationHandler;
import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;

public class RaceExistValidator implements ValidationHandler {
  private Main main;
  private boolean shouldExist;

  public RaceExistValidator(Main main, boolean shouldExist) {
    this.main = main;
    this.shouldExist = shouldExist;
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
  public void whenInvalid(CommandSender sender, String[] args) {
    String message;
    if(this.shouldExist) {
      message = "race_not_found";
    } else {
      message = "race_already_exist";
    }
    main.getMessageManager().setValue("race_name", args[0]);
    main.getMessageManager().sendMessage(sender,message);
  }
}

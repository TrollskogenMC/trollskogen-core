package com.github.hornta.trollskogen.racing.commands.validators;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.enums.RacingType;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ValidationHandler;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RacingTypeValidator extends ValidationHandler {
  private Main main;

  public RacingTypeValidator(Main main) {
    this.main = main;
  }

  @Override
  public void setMessageValues(CommandSender commandSender, String[] arguments) {
    main.getMessageManager().setValue("type", arguments[0]);
    main.getMessageManager().setValue("types", Arrays.stream(RacingType.values()).map(RacingType::name).collect(Collectors.joining(", ")));
  }

  @Override
  public boolean test(CommandSender commandSender, String[] arguments) {
    RacingType type = RacingType.fromString(arguments[0]);
    return type != null;
  }

  @Override
  public String getErrorMessage(CommandSender commandSender, String[] arguments) {
    return "race_type_not_found";
  }
}

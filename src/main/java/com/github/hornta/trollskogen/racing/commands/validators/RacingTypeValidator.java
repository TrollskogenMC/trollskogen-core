package com.github.hornta.trollskogen.racing.commands.validators;

import com.github.hornta.ValidationHandler;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.enums.RacingType;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RacingTypeValidator implements ValidationHandler {
  private Main main;

  public RacingTypeValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(CommandSender commandSender, String[] arguments) {
    RacingType type = RacingType.fromString(arguments[0]);
    return type != null;
  }

  @Override
  public void whenInvalid(CommandSender commandSender, String[] args) {
    main.getMessageManager().setValue("type", args[0]);
    main.getMessageManager().setValue("types", Arrays.stream(RacingType.values()).map(RacingType::name).collect(Collectors.joining(", ")));
    main.getMessageManager().sendMessage(commandSender, "race_type_not_found");
  }
}

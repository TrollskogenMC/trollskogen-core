package com.github.hornta.trollskogen.validators;

import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ValidationHandler;

import java.text.DecimalFormat;

public class NumberInRangeValidator extends ValidationHandler {
  private Main main;
  private int min;
  private int max;

  public NumberInRangeValidator(Main main, int min, int max) {
    this.main = main;
    this.min = min;
    this.max = max;
  }

  @Override
  public boolean test(CommandSender sender, String[] args) {
    int num;
    try {
      num = Integer.parseInt(args[0]);
    } catch(NumberFormatException e) {
      return false;
    }

    if(num < min || num > max) {
      return false;
    }

    return true;
  }

  @Override
  public void setMessageValues(CommandSender sender, String[] args) {
    DecimalFormat formatter = new DecimalFormat("###,###.#");
    main.getMessageManager().setValue("min", formatter.format(min));
    main.getMessageManager().setValue("max", formatter.format(max));
  }

  @Override
  public String getErrorMessage(CommandSender commandSender, String[] args) {
    return "validate_number_range";
  }
}

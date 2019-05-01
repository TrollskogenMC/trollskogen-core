package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.ArgumentValidator;

import java.util.regex.Pattern;

public class RegexValidator extends ArgumentValidator {
  private Main main;
  private Pattern pattern;
  private String errorMessage;

  RegexValidator(Main main, Pattern pattern, String errorMessage) {
    this.main = main;
    this.pattern = pattern;
    this.errorMessage = errorMessage;
  }

  @Override
  public boolean test(CommandSender sender, String arg) {
    return pattern.matcher(arg).matches();
  }

  @Override
  public void setMessageValues(CommandSender sender, String s) {

  }

  @Override
  public String getErrorMessage(CommandSender commandSender) {
    return errorMessage;
  }
}

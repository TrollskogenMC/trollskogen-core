package com.github.hornta.trollskogen.validators;

import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ValidationHandler;

import java.util.regex.Pattern;

public class RegexValidator extends ValidationHandler {
  private Main main;
  private Pattern pattern;
  private String errorMessage;

  public RegexValidator(Main main, Pattern pattern, String errorMessage) {
    this.main = main;
    this.pattern = pattern;
    this.errorMessage = errorMessage;
  }

  @Override
  public boolean test(CommandSender sender, String[] args) {
    return pattern.matcher(args[0]).matches();
  }

  @Override
  public void setMessageValues(CommandSender sender, String[] args) {

  }

  @Override
  public String getErrorMessage(CommandSender commandSender, String[] args) {
    return errorMessage;
  }
}

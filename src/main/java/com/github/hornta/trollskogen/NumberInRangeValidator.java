package com.github.hornta.trollskogen;

import se.hornta.carbon.ArgumentValidator;

import java.text.DecimalFormat;

public class NumberInRangeValidator extends ArgumentValidator {
  private Main main;
  private int min;
  private int max;

  public NumberInRangeValidator(Main main, int min, int max) {
    this.main = main;
    this.min = min;
    this.max = max;
  }

  @Override
  public boolean test(String arg) {
    int num;
    try {
      num = Integer.parseInt(arg);
    } catch(NumberFormatException e) {
      return false;
    }

    if(num < min || num > max) {
      return false;
    }

    return true;
  }

  @Override
  public void setMessageValues(String s) {
    DecimalFormat formatter = new DecimalFormat("###,###.#");
    main.getMessageManager().setValue("min", formatter.format(min));
    main.getMessageManager().setValue("max", formatter.format(max));
  }

  @Override
  public String getErrorMessage() {
    return "validate_number_range";
  }
}

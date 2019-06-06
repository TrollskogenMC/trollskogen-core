package com.github.hornta.trollskogen;

import java.util.Arrays;

public class BaseCommand {
  protected static String getLastArg(String[] args, int start) {
    return String.join(" ", Arrays.copyOfRange(args, start, args.length));
  }
}

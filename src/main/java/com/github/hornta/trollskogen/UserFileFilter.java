package com.github.hornta.trollskogen;

import java.io.File;
import java.io.FilenameFilter;
import java.util.UUID;

public class UserFileFilter implements FilenameFilter {
  @Override
  public boolean accept(File dir, String name) {
    String[] values = name.split(".");
    if(values.length != 2) {
      return false;
    }

    String stringUUID = values[0];
    String extension = values[1];

    try {
      UUID.fromString(stringUUID);
    } catch (IllegalArgumentException e) {
      return false;
    }

    if(extension.equals("yml")) {
      return false;
    }

    return true;
  }
}

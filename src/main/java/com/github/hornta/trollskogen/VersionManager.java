package com.github.hornta.trollskogen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

public class VersionManager {
  private static final Pattern BUKKIT_VERSION_PATTERN = Pattern.compile("v\\d+_\\d+_R\\d+");
  private static final Pattern MINECRAFT_VERSION_PATTERN = Pattern.compile("(\\(MC: )([\\d.]+)(\\))");

  public VersionManager() {
    if (getBukkitVersion() != null) {
      ServerVersion.setServerVersion(ServerVersion.valueOf(getBukkitVersion()));
    } else {
      ServerVersion.setServerVersion(ServerVersion.valueOfSpigotRelease(getMinecraftVersion()));
    }
  }

  public static boolean is1_13OrAbove() {
    return is1_13Version() || is1_14OrAbove();
  }

  public static boolean is1_14OrAbove() {
    return is1_14Version();
  }

  public static boolean is1_13Version() {
    return is1_13_R1Version() || is1_13_R2Version();
  }

  private static boolean is1_14Version() {
    return is1_14_R1Version();
  }

  private static boolean is1_13_R1Version() {
    return ServerVersion.getServerVersion() == ServerVersion.v1_13_R1;
  }

  private static boolean is1_13_R2Version() {
    return ServerVersion.getServerVersion() == ServerVersion.v1_13_R2;
  }

  private static boolean is1_14_R1Version() {
    return ServerVersion.getServerVersion() == ServerVersion.v1_14_R1;
  }

  private static String getBukkitVersion() {
    Matcher matcher = BUKKIT_VERSION_PATTERN.matcher(Bukkit.getServer().getClass().getPackage().getName());
    if (matcher.find()) {
      return matcher.group();
    }
    return null;
  }

  private static String getMinecraftVersion() {
    Matcher matcher = MINECRAFT_VERSION_PATTERN.matcher(Bukkit.getVersion());
    if (matcher.find()) {
      return matcher.group(2);
    }
    return null;
  }

  private static int compare(String reference, String comparison) {
    String[] referenceSplit = reference.split("\\.");
    String[] comparisonSplit = comparison.split("\\.");

    int longest = Math.max(referenceSplit.length, comparisonSplit.length);

    int[] referenceNumbersArray = new int[longest];
    int[] comparisonNumbersArray = new int[longest];
    for (int i = 0; i < referenceSplit.length; i++) {
      referenceNumbersArray[i] = Integer.parseInt(referenceSplit[i]);
    }
    for (int i = 0; i < comparisonSplit.length; i++) {
      comparisonNumbersArray[i] = Integer.parseInt(comparisonSplit[i]);
    }
    for (int i = 0; i < longest; i++) {
      int diff = referenceNumbersArray[i] - comparisonNumbersArray[i];
      if (diff > 0) {
        return 1;
      }
      if (diff < 0) {
        return -1;
      }
    }
    return 0;
  }

  private static boolean isVersionGreaterEqual(String reference, String thanWhat) {
    return compare(reference, thanWhat) >= 0;
  }

  private static boolean isVersionLessEqual(String reference, String thanWhat) {
    return compare(reference, thanWhat) <= 0;
  }

  public static boolean isVersionBetweenEqual(String reference, String lowest, String highest) {
    return (isVersionGreaterEqual(reference, lowest)) && (isVersionLessEqual(reference, highest));
  }

  public static boolean isClassExists(String className) {
    try {
      Class.forName(className);
      return true;
    } catch (Throwable localThrowable) {}
    return false;
  }
}

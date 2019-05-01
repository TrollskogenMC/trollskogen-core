package com.github.hornta.trollskogen;

import org.bukkit.Location;

public class Home {
  private String name;
  private Location location;

  public static final String DEFAULT_HOME_NAME = "home";

  Home(String name, Location location) {
    this.name = name;
    this.location = location;
  }

  public String getName() {
    return name;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }
}

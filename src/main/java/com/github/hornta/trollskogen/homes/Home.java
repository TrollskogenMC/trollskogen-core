package com.github.hornta.trollskogen.homes;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import org.bukkit.Location;

import java.util.UUID;

public class Home {
  private String name;
  private Location location;
  private boolean isPublic;
  private Point geometry;
  private UUID owner;
  private boolean allowCommands;

  public static final String DEFAULT_HOME_NAME = "home";

  public Home(String name, Location location, boolean isPublic, UUID owner, boolean allowCommands) {
    this.name = name;
    this.location = location;
    this.isPublic = isPublic;
    this.geometry = Geometries.point(location.getX(), location.getZ());
    this.owner = owner;
    this.allowCommands = allowCommands;
  }

  public boolean isPublic() {
    return isPublic;
  }

  public void setPublic(boolean aPublic) {
    isPublic = aPublic;
  }

  public String getName() {
    return name;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
    this.geometry = Geometries.point(location.getX(), location.getZ());
  }

  public Point getGeometry() {
    return geometry;
  }

  public UUID getOwner() {
    return owner;
  }

  public boolean isAllowCommands() {
    return allowCommands;
  }

  public void toggleAllowCommands() {
    allowCommands = !allowCommands;
  }
}

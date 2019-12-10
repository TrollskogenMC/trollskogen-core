package com.github.hornta.trollskogen.homes;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import org.bukkit.Location;

public class Home {
  private int id;
  private String name;
  private Location location;
  private boolean isPublic;
  private Point geometry;
  private int owner;
  private boolean allowCommands;

  public static final String DEFAULT_HOME_NAME = "home";

  public Home(int id, String name, Location location, boolean isPublic, int owner, boolean allowCommands) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.isPublic = isPublic;
    this.geometry = Geometries.point(location.getX(), location.getZ());
    this.owner = owner;
    this.allowCommands = allowCommands;
  }

  public int getId() {
    return id;
  }

  public boolean isPublic() {
    return isPublic;
  }

  public void setPublic(boolean aPublic) {
    isPublic = aPublic;
  }

  public void setAllowCommands(boolean allowCommands) {
    this.allowCommands = allowCommands;
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

  public int getOwner() {
    return owner;
  }

  public boolean isAllowCommands() {
    return allowCommands;
  }
}

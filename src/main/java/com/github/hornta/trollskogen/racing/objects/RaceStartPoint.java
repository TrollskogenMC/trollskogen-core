package com.github.hornta.trollskogen.racing.objects;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Location;

public class RaceStartPoint {
  private int id;
  private Location location;
  private int position;
  private Hologram hologram;

  public RaceStartPoint(int id, Location location, int position) {
    this.id = id;
    this.location = location;
    this.position = position;
  }

  public int getId() {
    return id;
  }

  public Location getLocation() {
    return location.clone();
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public void setHologram(Hologram hologram) {
    this.hologram = hologram;
  }

  public Hologram getHologram() {
    return hologram;
  }
}

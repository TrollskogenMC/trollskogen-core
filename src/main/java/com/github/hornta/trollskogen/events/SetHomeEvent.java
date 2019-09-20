package com.github.hornta.trollskogen.events;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.hornta.trollskogen.homes.Home;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class SetHomeEvent extends Event {
  private Home home;
  private Point previousGeometry;
  private static final HandlerList HANDLERS = new HandlerList();

  public SetHomeEvent(Home home, Point previousGeometry) {
    this.home = home;
    this.previousGeometry = previousGeometry;
  }

  public Home getHome() {
    return home;
  }

  public Point getPreviousGeometry() {
    return previousGeometry;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}

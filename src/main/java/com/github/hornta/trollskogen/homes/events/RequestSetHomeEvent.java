package com.github.hornta.trollskogen.homes.events;

import com.github.hornta.trollskogen.users.UserObject;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RequestSetHomeEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private String name;
  private Location location;
  private UserObject user;

  public RequestSetHomeEvent(String name, Location location, UserObject user) {
    this.name = name;
    this.location = location;
    this.user = user;
  }

  public String getName() {
    return name;
  }

  public Location getLocation() {
    return location;
  }

  public UserObject getUser() {
    return user;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}

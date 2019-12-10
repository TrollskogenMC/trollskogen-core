package com.github.hornta.trollskogen.homes.events;

import com.github.hornta.trollskogen.homes.HomeManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LoadHomesEvent extends Event {
  private HomeManager homeManager;
  private static final HandlerList HANDLERS = new HandlerList();

  public LoadHomesEvent(HomeManager homeManager) {
    this.homeManager = homeManager;
  }

  public HomeManager getHomeManager() {
    return homeManager;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}

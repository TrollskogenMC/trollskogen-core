package com.github.hornta.trollskogen.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BaseTrollEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}




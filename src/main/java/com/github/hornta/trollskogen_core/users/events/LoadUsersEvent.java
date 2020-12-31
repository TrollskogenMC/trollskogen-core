package com.github.hornta.trollskogen_core.users.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LoadUsersEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }
}

package com.github.hornta.trollskogen.events;

import com.github.hornta.trollskogen.homes.Home;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DeleteHomeEvent extends Event {
  private Home home;
  private static final HandlerList HANDLERS = new HandlerList();

  public DeleteHomeEvent(Home home) {
    this.home = home;
  }

  public Home getHome() {
    return home;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}

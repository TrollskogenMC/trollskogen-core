package com.github.hornta.trollskogen.events;

import com.github.hornta.trollskogen.User;
import com.github.hornta.trollskogen.homes.Home;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CloseHomeEvent extends Event {
  private User user;
  private Home home;
  private static final HandlerList HANDLERS = new HandlerList();

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public CloseHomeEvent(User user, Home home) {
    this.user = user;
    this.home = home;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  public User getUser() {
    return user;
  }

  public Home getHome() {
    return home;
  }
}

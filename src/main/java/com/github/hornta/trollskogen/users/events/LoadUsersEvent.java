package com.github.hornta.trollskogen.users.events;

import com.github.hornta.trollskogen.users.UserManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LoadUsersEvent extends Event {
  private UserManager userManager;
  private static final HandlerList HANDLERS = new HandlerList();

  public LoadUsersEvent(UserManager userManager) {
    this.userManager = userManager;
  }

  public UserManager getUserManager() {
    return userManager;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}

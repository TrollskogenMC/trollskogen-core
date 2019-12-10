package com.github.hornta.trollskogen.users.events;

import com.github.hornta.trollskogen.users.UserObject;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UserVerifiedEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private UserObject user;

  public UserVerifiedEvent(UserObject user) {
    this.user = user;
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

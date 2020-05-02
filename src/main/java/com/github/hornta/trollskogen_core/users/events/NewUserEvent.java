package com.github.hornta.trollskogen_core.users.events;

import com.github.hornta.trollskogen_core.users.UserObject;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NewUserEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private UserObject userObject;

  public NewUserEvent(UserObject userObject) {
    this.userObject = userObject;
  }

  public UserObject getUserObject() {
    return userObject;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}

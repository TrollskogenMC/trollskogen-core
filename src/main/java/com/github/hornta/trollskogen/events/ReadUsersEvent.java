package com.github.hornta.trollskogen.events;

import com.github.hornta.trollskogen.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Map;
import java.util.UUID;

public class ReadUsersEvent extends Event {
  private Map<UUID, User> users;
  private static final HandlerList HANDLERS = new HandlerList();

  public ReadUsersEvent(Map<UUID, User> users) {
    this.users = users;
  }

  public Map<UUID, User> getUsers() {
    return users;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}

package com.github.hornta.trollskogen_core.announcements.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RequestSetAnnouncementEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private final String name;
  private final String text;

  public RequestSetAnnouncementEvent(String name, String text) {
    this.name = name;
    this.text = text;
  }

  public String getName() {
    return name;
  }

  public String getText() {
    return text;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}

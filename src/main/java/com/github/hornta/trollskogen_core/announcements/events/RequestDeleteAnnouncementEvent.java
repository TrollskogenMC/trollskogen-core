package com.github.hornta.trollskogen_core.announcements.events;

import com.github.hornta.trollskogen_core.announcements.Announcement;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RequestDeleteAnnouncementEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private final Announcement announcement;

  public RequestDeleteAnnouncementEvent(Announcement announcement) {
    this.announcement = announcement;
  }

  public Announcement getAnnouncement() {
    return announcement;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}

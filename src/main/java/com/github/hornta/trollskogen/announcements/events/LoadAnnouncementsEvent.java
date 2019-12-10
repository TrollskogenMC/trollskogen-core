package com.github.hornta.trollskogen.announcements.events;

import com.github.hornta.trollskogen.announcements.AnnouncementManager;
import com.github.hornta.trollskogen.bans.BanManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LoadAnnouncementsEvent extends Event {
  private AnnouncementManager announcementManager;
  private static final HandlerList HANDLERS = new HandlerList();

  public LoadAnnouncementsEvent(AnnouncementManager announcementManager) {
    this.announcementManager = announcementManager;
  }

  public AnnouncementManager getAnnouncementManager() {
    return announcementManager;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}

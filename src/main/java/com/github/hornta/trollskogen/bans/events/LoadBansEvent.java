package com.github.hornta.trollskogen.bans.events;

import com.github.hornta.trollskogen.bans.BanManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LoadBansEvent extends Event {
  private BanManager banManager;
  private static final HandlerList HANDLERS = new HandlerList();

  public LoadBansEvent(BanManager banManager) {
    this.banManager = banManager;
  }

  public BanManager getBanManager() {
    return banManager;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}

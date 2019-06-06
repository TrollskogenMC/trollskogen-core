package com.github.hornta.trollskogen.racing.events;

import com.github.hornta.trollskogen.racing.objects.Race;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RaceEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private Race race;

  RaceEvent(Race race) {
    this.race = race;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  public Race getRace() {
    return race;
  }
}




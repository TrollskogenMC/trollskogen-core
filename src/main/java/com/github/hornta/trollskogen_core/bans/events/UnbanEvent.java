package com.github.hornta.trollskogen_core.bans.events;

import com.github.hornta.trollskogen_core.bans.Ban;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UnbanEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private final Ban ban;

  public UnbanEvent(Ban ban) {
    this.ban = ban;
  }

  public Ban getBan() {
    return ban;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}

package com.github.hornta.trollskogen_core.bans.events;

import com.github.hornta.trollskogen_core.bans.Ban;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BanEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private final Ban ban;
  private final CommandSender issuer;

  public BanEvent(Ban ban, CommandSender issuer) {
    this.ban = ban;
    this.issuer = issuer;
  }

  public Ban getBan() {
    return ban;
  }

  public CommandSender getIssuer() {
    return issuer;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}

package com.github.hornta.trollskogen.bans.events;

import com.github.hornta.trollskogen.bans.Ban;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BanEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private Ban ban;
  private CommandSender issuer;

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

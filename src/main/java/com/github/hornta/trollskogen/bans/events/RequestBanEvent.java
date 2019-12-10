package com.github.hornta.trollskogen.bans.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.time.Instant;

public class RequestBanEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private Instant expiryDate;
  private String reason;
  private int userId;
  private Integer issuedBy;
  private CommandSender issuer;

  public RequestBanEvent(Instant expiryDate, String reason, int userId, Integer issuedBy, CommandSender issuer) {
    this.expiryDate = expiryDate;
    this.reason = reason;
    this.userId = userId;
    this.issuedBy = issuedBy;
    this.issuer = issuer;
  }

  public Instant getExpiryDate() {
    return expiryDate;
  }

  public String getReason() {
    return reason;
  }

  public int getUserId() {
    return userId;
  }

  public Integer getIssuedBy() {
    return issuedBy;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  public CommandSender getIssuer() {
    return issuer;
  }
}

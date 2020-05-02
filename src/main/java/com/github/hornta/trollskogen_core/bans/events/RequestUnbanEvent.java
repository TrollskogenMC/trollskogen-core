package com.github.hornta.trollskogen_core.bans.events;

import com.github.hornta.trollskogen_core.users.UserObject;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RequestUnbanEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private final UserObject user;
  private final CommandSender issuer;

  public RequestUnbanEvent(UserObject user, CommandSender issuer) {
    this.user = user;
    this.issuer = issuer;
  }

  public UserObject getUser() {
    return user;
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

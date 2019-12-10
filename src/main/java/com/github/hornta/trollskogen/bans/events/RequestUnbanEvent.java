package com.github.hornta.trollskogen.bans.events;

import com.github.hornta.trollskogen.users.UserObject;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RequestUnbanEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private UserObject user;
  private CommandSender issuer;

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

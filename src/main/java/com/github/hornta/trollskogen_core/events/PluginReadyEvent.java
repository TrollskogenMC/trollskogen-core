package com.github.hornta.trollskogen_core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginReadyEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private final JavaPlugin plugin;

  public PluginReadyEvent(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  public JavaPlugin getPlugin() {
    return plugin;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}

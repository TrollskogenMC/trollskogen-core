package com.github.hornta.trollskogen_core;

import com.github.hornta.trollskogen_core.events.PluginReadyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class ServerReady implements Listener {
  private final Map<JavaPlugin, Boolean> pluginsToWaitFor;
  private boolean isReady;

  ServerReady() {
    this.pluginsToWaitFor = new HashMap<>();
    this.isReady = false;
  }

  public void waitFor(JavaPlugin plugin) {
    pluginsToWaitFor.put(plugin, false);
  }

  @EventHandler
  public void onPluginReady(PluginReadyEvent event) {
    if(isReady) {
      return;
    }

    pluginsToWaitFor.put(event.getPlugin(), true);

    var allPluginsReady = true;
    for(var ready : pluginsToWaitFor.values()) {
      if(!ready) {
        allPluginsReady = false;
        break;
      }
    }

    if(allPluginsReady) {
      isReady = true;
    }
  }

  public boolean isReady() {
    return pluginsToWaitFor.isEmpty() || isReady;
  }
}

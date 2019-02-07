package com.github.hornta.trollskogen;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplodeListener implements Listener {
  @EventHandler
  public void onEntityExplode(EntityExplodeEvent event) {
    if(event.getEntityType() == EntityType.CREEPER) {
      event.setCancelled(true);
    }
  }
}

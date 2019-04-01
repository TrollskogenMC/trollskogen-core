package com.github.hornta.trollskogen;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
  private Main main;

  public PlayerListener(Main main) {
    this.main = main;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    this.main.scheduleSyncDelayedTask(() -> {
      if(!event.getPlayer().hasPlayedBefore()) {
        event.getPlayer().saveData();
        main.getMessageManager().setValue("player", event.getPlayer().getName());
        main.getMessageManager().broadcast("first-join-message");

        event.getPlayer().getInventory().setContents(main.getTrollskogenConfig().getStarterInventory());
      }
    });
  }
}

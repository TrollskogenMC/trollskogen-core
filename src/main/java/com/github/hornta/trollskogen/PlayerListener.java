package com.github.hornta.trollskogen;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
  private Main main;

  public PlayerListener(Main main) {
    this.main = main;
  }

  @EventHandler
  public void onPlayerLogin(PlayerLoginEvent e) {
    User user = main.getUser(e.getPlayer());
    if(user.isBanned()) {
      e.disallow(PlayerLoginEvent.Result.KICK_BANNED, user.getBanMessage());
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    User user = main.getUser(event.getPlayer());
    user.setPlayer(event.getPlayer());

    this.main.scheduleSyncDelayedTask(() -> {
      if(!event.getPlayer().hasPlayedBefore()) {
        event.getPlayer().saveData();
        main.getMessageManager().setValue("player", event.getPlayer().getName());
        main.getMessageManager().broadcast("first-join-message");

        event.getPlayer().getInventory().setContents(main.getTrollskogenConfig().getStarterInventory());
      }
    });
  }

  @EventHandler
  void onPlayerDisconnect(PlayerQuitEvent event) {
    User user = main.getUser(event.getPlayer());
    user.setSelectedEffect(null);
  }
}

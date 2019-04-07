package com.github.hornta.trollskogen;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class PlayerManager {
  private Main main;
  private Map<UUID, TrollPlayer> playerCache;

  PlayerManager(Main main) {
    this.main = main;
    playerCache = new HashMap<>();
  }

  TrollPlayer createTrollPlayer(Player player) {
    TrollPlayer trollPlayer = new TrollPlayer(main, player);
    playerCache.put(player.getUniqueId(), trollPlayer);
    return trollPlayer;
  }

  void init() {
    for(Player player : Bukkit.getOnlinePlayers()) {
      createTrollPlayer(player);
    }
  }
}

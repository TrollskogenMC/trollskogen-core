package com.github.hornta.trollskogen;

import org.bukkit.entity.Player;

public class TrollPlayer {
  private Main main;
  private Player player;

  TrollPlayer(Main main, Player player) {
    this.main = main;
    this.player = player;
  }

  public Player getPlayer() {
    return player;
  }
}

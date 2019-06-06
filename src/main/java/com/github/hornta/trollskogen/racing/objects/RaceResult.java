package com.github.hornta.trollskogen.racing.objects;

import org.bukkit.entity.Player;

public class RaceResult {
  private Race race;
  private Player player;
  private int position;
  private int time;

  RaceResult(Race race, Player player, int position, int time) {
    this.race = race;
    this.player = player;
    this.position = position;
    this.time = time;
  }

  public Race getRace() {
    return race;
  }

  public Player getPlayer() {
    return player;
  }

  public int getPosition() {
    return position;
  }

  public int getTime() {
    return time;
  }
}

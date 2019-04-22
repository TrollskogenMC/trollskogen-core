package com.github.hornta.trollskogen;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

class ParticleManager implements Listener {
  private static final long TICKS_BETWEEN_TASK = 2L;
  private Main main;
  private HashMap<Player, ParticleEffect> playerParticles = new HashMap<>();

  ParticleManager(Main main) {
    this.main = main;
  }

  ParticleEffect reset(Player player) {
    return playerParticles.remove(player);
  }

  ParticleEffect getParticleEffect(Player player) {
    return playerParticles.get(player);
  }

  void useParticle(Player player, ParticleEffect particleEffect) {
    playerParticles.put(player, particleEffect);
    EffectTask task = new EffectTask(this, player, particleEffect);
    task.runTaskTimerAsynchronously(main, 0L, TICKS_BETWEEN_TASK);
  }
}

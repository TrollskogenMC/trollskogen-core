package com.github.hornta.trollskogen.effects;

import com.github.hornta.trollskogen.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class ParticleManager implements Listener {
  private static final long TICKS_BETWEEN_TASK = 2L;
  private Main main;
  private HashMap<Player, ParticleEffect> playerParticles = new HashMap<>();

  public ParticleManager(Main main) {
    this.main = main;
  }

  public ParticleEffect reset(Player player) {
    return playerParticles.remove(player);
  }

  ParticleEffect getParticleEffect(Player player) {
    return playerParticles.get(player);
  }

  public void useParticle(Player player, ParticleEffect particleEffect) {
    //playerParticles.put(player, particleEffect);
    //EffectTask task = new EffectTask(this, player, particleEffect);
    //task.runTaskTimerAsynchronously(main, 0L, TICKS_BETWEEN_TASK);
  }
}

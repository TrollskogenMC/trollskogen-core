package com.github.hornta.trollskogen;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class EffectTask extends BukkitRunnable {
  private ParticleManager particleManager;
  private Player player;
  private ParticleEffect particleEffect;

  private int step;

  // 4.5 degrees
  private static final double ANGLE_INCREMENT = 0.0785398163;
  private static final double RADIUS = 0.45;
  private static final int PLAYER_PARTICLE_HEIGHT = 2;
  private static final int STEP_INCREMENT = 4;

  EffectTask(ParticleManager particleManager, Player player, ParticleEffect particleEffect) {
    this.particleManager = particleManager;
    this.player = player;
    this.particleEffect = particleEffect;
  }

  @Override
  public void run() {
    if(particleManager.getParticleEffect(player) == null || particleManager.getParticleEffect(player) != particleEffect) {
      this.cancel();
      return;
    }

    double angle = step * ANGLE_INCREMENT;

    Vector v = new Vector();
    v.setX(Math.cos(angle) * RADIUS);
    v.setZ(Math.sin(angle) * RADIUS);

    float speed = particleEffect.isVolatile() ? 0 : 5;
    int amount = particleEffect == ParticleEffect.SLIME ? 5 : 1;
    particleEffect.display(player.getLocation().add(v).add(0, PLAYER_PARTICLE_HEIGHT, 0), speed, amount);

    this.step += STEP_INCREMENT;
  }
}

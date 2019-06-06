package com.github.hornta.trollskogen.racing.objects;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.effects.ParticleEffect;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RaceCheckpoint implements Comparable<RaceCheckpoint> {
  private int id;
  private int position;
  private Location center;
  private int radius;
  private ParticleTask task;
  private Vector vector;
  private List<Player> players = new ArrayList<>();
  private Hologram hologram;

  public RaceCheckpoint(int id, int position, Location center, int radius) {
    this.id = id;
    this.position = position;
    this.center = center;
    this.radius = radius;
    vector = center.toVector();
  }

  public int getId() {
    return id;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public Location getCenter() {
    return center.clone();
  }

  public int getRadius() {
    return radius;
  }

  public void stopTask() {
    task.cancel();
    task = null;
  }

  public void startTask(Main main) {
    startTask(main, false);
  }

  public void startTask(Main main, boolean isEditing) {
    this.task = new ParticleTask(this, isEditing);
    this.task.runTaskTimerAsynchronously(main, 0, 1L);
  }

  public boolean isInside(Entity entity) {
    return entity.getLocation().toVector().subtract(vector).lengthSquared() <= radius * radius;
  }

  public void addPlayer(Player player) {
    players.add(player);
  }

  public void removePlayer(Player player) {
    players.remove(player);
  }

  public List<Player> getPlayers() {
    return new ArrayList<>(players);
  }

  public Hologram getHologram() {
    return hologram;
  }

  public void setHologram(Hologram hologram) {
    this.hologram = hologram;
  }

  @Override
  public int compareTo(RaceCheckpoint o) {
    return Integer.compare(position, o.getPosition());
  }

  public static class ParticleTask extends BukkitRunnable {
    private final ParticleEffect particleEffect = ParticleEffect.getParticleEffect("REDSTONE");
    private int step;
    // 2 degrees
    private static final double ANGLE_INCREMENT = 0.034906585;
    private static final int STEP_INCREMENT = 2;
    private static final int NUMBER_ORBS = 2;
    private static final double ANGLE_INCREMENT_ORB = (2 * Math.PI) / NUMBER_ORBS;
    private RaceCheckpoint checkpoint;
    private boolean isEditing;

    ParticleTask(RaceCheckpoint checkpoint, boolean isEditing) {
      this.checkpoint = checkpoint;
      this.isEditing = isEditing;
    }

    @Override
    public void run() {
      List<Player> players = checkpoint.getPlayers();
      for(int i = 0; i < NUMBER_ORBS; ++i) {
        double angle = step * (ANGLE_INCREMENT + i * ANGLE_INCREMENT_ORB);
        Vector v = new Vector();
        v.setX(Math.cos(angle) * checkpoint.getRadius());
        v.setZ(Math.sin(angle) * checkpoint.getRadius());
        if(isEditing) {
          particleEffect.displayColor(this.checkpoint.getCenter().add(v), 255, 0, 0, 0, 2);
        } else {
          for (Player player : players) {
            particleEffect.displayColor(this.checkpoint.getCenter().add(v), player, 255, 0, 0, 0, 2);
          }
        }
      }
      this.step += STEP_INCREMENT;
    }
  }
}

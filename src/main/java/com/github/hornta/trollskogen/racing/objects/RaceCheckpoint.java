package com.github.hornta.trollskogen.racing.objects;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.effects.ParticleEffect;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
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
    return entity.getLocation().toVector().isInSphere(vector, radius);
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
    private double t;

    private static final double ANGLE_INCREMENT = Math.toRadians(4);
    private static final int NUMBER_ORBS = 2;
    private static final double ANGLE_OFFSET = (2 * Math.PI) / NUMBER_ORBS;
    private RaceCheckpoint checkpoint;
    private boolean isEditing;

    ParticleTask(RaceCheckpoint checkpoint, boolean isEditing) {
      this.checkpoint = checkpoint;
      this.isEditing = isEditing;
    }

    @Override
    public void run() {
      List<Player> players = checkpoint.getPlayers();

      boolean isInside = false;
      if(isEditing) {
        for(Player player : Bukkit.getOnlinePlayers()) {
          if(checkpoint.isInside(player)) {
            isInside = true;
            break;
          }
        }
      }

      Vector dir = checkpoint.getCenter().getDirection();

      for(int i = 0; i < NUMBER_ORBS; ++i) {
        double offset = i * ANGLE_OFFSET;
        double angle = t + offset;

        // rotate dir by 90 degrees CW on the y-axis
        Vector p = new Vector(-dir.getZ(), dir.getY(), dir.getX());
        Vector c = p.crossProduct(dir);
        Vector v = c.rotateAroundAxis(dir, angle).normalize().multiply(checkpoint.getRadius());

        if(isEditing) {
          particleEffect.displayColor(checkpoint.getCenter().add(v), isInside ? 0 : 255, isInside ? 255 : 0, 0, 0, 2);
        } else {
          for (Player player : players) {
            particleEffect.displayColor(checkpoint.getCenter().add(v), player, 255, 0, 0, 0, 2);
          }
        }
      }
      t += ANGLE_INCREMENT;
    }
  }
}

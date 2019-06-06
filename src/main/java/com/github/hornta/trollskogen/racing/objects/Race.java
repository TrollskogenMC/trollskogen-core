package com.github.hornta.trollskogen.racing.objects;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.enums.RaceState;
import com.github.hornta.trollskogen.racing.enums.RacingType;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.minecraft.server.v1_13_R2.MinecraftServer;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.xml.transform.Result;
import java.time.LocalDateTime;
import java.util.*;

public class Race implements Listener {
  private Main main;
  private int id;
  private String name;
  private Location spawn;
  private List<RaceCheckpoint> checkpoints;
  private List<RaceStartPoint> startPoints;
  private boolean isEnabled;
  private boolean isEditing;
  private LocalDateTime createdAt;
  private RacingType type;
  private RaceState state = RaceState.IDLE;
  private Set<Player> participants = new HashSet<>();
  private Map<Player, RaceCheckpoint> participantNextCheckpoints = new HashMap<>();
  private List<Integer> startTimerTasks = new ArrayList<>();
  private Countdown countdownTask;
  private RadioSongPlayer songPlayerStart;
  private RadioSongPlayer songPlayerDuring;

  // cache player data between race
  private Map<Player, Float> playerWalkSpeeds = new HashMap<>();
  private Map<Player, Integer> playerFoodLevels = new HashMap<>();
  private Map<Player, ItemStack[]> playerInventories = new HashMap<>();
  private Map<Player, Collection<PotionEffect>> playerPotionEffects = new HashMap<>();
  private Map<Player, Double> playerHealth = new HashMap<>();

  private int raceStartTick;
  private Map<Player, RaceResult> results = new HashMap<>();

  private static final long FIVE_MINUTES = 10 * 20; //20L * 60 * 5;
  private static final long THREE_MINUTES = 7 * 20; //20L * 60 * 3;
  private static final long ONE_MINUTE = 4 * 20; // 20L * 60;
  private static final long THIRTY_SECONDS = 3 * 20; //20L * 30;
  public static final int REQUIRED_START_POINTS = 1; // 2
  private static final int COUNTDOWN_IN_SECONDS = 10;
  private static final int HALF_SECOND = 10;
  private static final int ONE_SECOND = 20;
  private static final int PREVENT_SPRINT_FOOD_LEVEL = 6;
  private static final int MAX_FOOD_LEVEL = 20;
  private static final float DEFAULT_WALK_SPEED = 0.2F;
  private static final double MAX_HEALTH = 20;

  public Race(
    Main main,
    int id,
    String name,
    Location spawn,
    boolean isEnabled,
    boolean isEditing,
    LocalDateTime createdAt,
    List<RaceCheckpoint> checkpoints,
    List<RaceStartPoint> startPoints,
    RacingType type
  ) {
    this.main = main;
    this.id = id;
    this.name = name;
    this.spawn = spawn;
    this.isEnabled = isEnabled;
    this.isEditing = isEditing;
    this.createdAt = createdAt;
    this.checkpoints = new ArrayList<>(checkpoints);
    this.startPoints = new ArrayList<>(startPoints);
    this.type = type;

    songPlayerStart = new RadioSongPlayer(main.getSongManager().getSongByName("Race_Start"));
    songPlayerDuring = new RadioSongPlayer(main.getSongManager().getSongByName("Were_not_Gonna_Take_It"));
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Location getSpawn() {
    return spawn.clone();
  }

  public List<RaceCheckpoint> getCheckpoints() {
    return new ArrayList<>(checkpoints);
  }

  public List<RaceStartPoint> getStartPoints() {
    return new ArrayList<>(startPoints);
  }

  public RaceCheckpoint getCheckpoint(int position) {
    for(RaceCheckpoint checkpoint : checkpoints) {
      if(checkpoint.getPosition() == position) {
        return checkpoint;
      }
    }
    return null;
  }

  public RaceStartPoint getStartPoint(int position) {
    for(RaceStartPoint startPoint : startPoints) {
      if(startPoint.getPosition() == position) {
        return startPoint;
      }
    }
    return null;
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public boolean isEditing() {
    return isEditing;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setSpawn(Location spawn) {
    this.spawn = new Location(spawn.getWorld(),
      Math.round(spawn.getBlock().getLocation().getX()),
      Math.round(spawn.getBlock().getLocation().getY()),
      Math.round(spawn.getBlock().getLocation().getZ()),
      spawn.getYaw(),
      spawn.getPitch());
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEnabled(boolean enabled) {
    isEnabled = enabled;
  }

  public void setEditing(boolean editing) {
    isEditing = editing;
  }

  public void addPoint(RaceCheckpoint point) {
    checkpoints.add(point);
  }

  public void delPoint(RaceCheckpoint point) {
    checkpoints.remove(point);
  }

  public void deleteStartPoint(RaceStartPoint startPoint) {
    startPoints.remove(startPoint);
  }

  public void addStartPoint(RaceStartPoint startPoint) {
    startPoints.add(startPoint);
  }

  public void setType(RacingType type) {
    this.type = type;
  }

  public RacingType getType() {
    return type;
  }

  public RaceState getState() {
    return state;
  }

  public void setState(RaceState state) {
    this.state = state;
  }

  public Set<Player> getParticipants() {
    return participants;
  }

  public void start(Player player) {
    Bukkit.getServer().getPluginManager().registerEvents(this, main);
    Bukkit.getServer().spigot().broadcast(new ComponentBuilder("")
      .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Klicka här för att delta").create()))
      .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/race join " + name))
      .append(player.getName() + " har startat race")
      .color(ChatColor.YELLOW)
      .append(" " + name)
      .color(ChatColor.GREEN)
      .append(" som startar om 5 min. Skriv")
      .color(ChatColor.YELLOW)
      .append(" /race join " + name)
      .color(ChatColor.DARK_GRAY)
      .append(" för att delta.")
      .color(ChatColor.YELLOW).create());

    setState(RaceState.PREPARING);
    addStartTimerTask(Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> Bukkit.getServer().spigot().broadcast(getTimeLeftMessage("3 minuter")), FIVE_MINUTES - THREE_MINUTES));
    addStartTimerTask(Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> Bukkit.getServer().spigot().broadcast(getTimeLeftMessage("1 minut")), FIVE_MINUTES - ONE_MINUTE));
    addStartTimerTask(Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> Bukkit.getServer().spigot().broadcast(getTimeLeftMessage("30 sekunder")), FIVE_MINUTES - THIRTY_SECONDS));
    addStartTimerTask(Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
      if(participants.size() < REQUIRED_START_POINTS) {
        main.getMessageManager().broadcast("race_canceled");
        stop();
        return;
      }

      // check if players are online before countdown starts
      for(Player player1 : participants) {
        if(!player1.isOnline()) {
          participants.remove(player1);
          for(Player player2 : participants) {
            main.getMessageManager().setValue("player_name", player1.getName());
            main.getMessageManager().sendMessage(player2, "race_start_noshow_disqualified");
          }
        }
      }

      if(participants.isEmpty()) {
        main.getMessageManager().broadcast("race_canceled");
        stop();
        return;
      }

      setState(RaceState.COUNTDOWN);

      List<Player> listParticipants = new ArrayList<>(participants);
      Collections.shuffle(listParticipants);

      for(RaceCheckpoint checkpoint : checkpoints) {
        checkpoint.startTask(main);
      }

      for(int i = 0; i < listParticipants.size(); ++i) {
        Player participant = listParticipants.get(i);

        // prevent taking damage and playing fall damage particles when teleporting during falling
        participant.setFallDistance(0);

        participant.teleport(startPoints.get(i).getLocation());

        playerWalkSpeeds.put(participant, participant.getWalkSpeed());
        participant.setWalkSpeed(0);

        playerPotionEffects.put(participant, new ArrayList<>(participant.getActivePotionEffects()));
        for(PotionEffect effect : participant.getActivePotionEffects()) {
          participant.removePotionEffect(effect.getType());
        }
        participant.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, COUNTDOWN_IN_SECONDS * MinecraftServer.TPS, 128));

        playerFoodLevels.put(participant, participant.getFoodLevel());
        participant.setFoodLevel(PREVENT_SPRINT_FOOD_LEVEL);

        playerInventories.put(participant, player.getInventory().getContents());
        participant.getInventory().clear();

        playerHealth.put(participant, player.getHealth());
        participant.setHealth(MAX_HEALTH);

        participant.closeInventory();

        participant.playSound(participant.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
        songPlayerStart.addPlayer(participant);

        tryIncrementCheckpoint(participant);
      }
      this.countdownTask = new Countdown(() -> {
        setState(RaceState.STARTED);
        for(int i = 0; i < listParticipants.size(); ++i) {
          Player participant = listParticipants.get(i);
          participant.setWalkSpeed(DEFAULT_WALK_SPEED);
          participant.setFoodLevel(MAX_FOOD_LEVEL);
          participant.removePotionEffect(PotionEffectType.JUMP);
          songPlayerDuring.addPlayer(participant);
        }
        songPlayerDuring.setTick((short)0);
        songPlayerDuring.setPlaying(true);
        raceStartTick = MinecraftServer.currentTick;
      });
      this.countdownTask.runTaskTimer(main, HALF_SECOND, ONE_SECOND);
    }, FIVE_MINUTES));
  }

  public void stop() {
    if(this.countdownTask != null) {
      this.countdownTask.cancel();
    }

    for(int taskId : startTimerTasks) {
      Bukkit.getScheduler().cancelTask(taskId);
    }

    for(Player player : participants) {
      restorePlayer(player);
    }

    playerWalkSpeeds.clear();
    playerPotionEffects.clear();
    playerFoodLevels.clear();
    playerInventories.clear();
    playerHealth.clear();

    if(state != RaceState.PREPARING) {
      for (RaceCheckpoint checkpoint : checkpoints) {
        checkpoint.stopTask();
      }
    }

    songPlayerDuring.setPlaying(false);
    HandlerList.unregisterAll(this);
    state = RaceState.IDLE;
  }

  private void restorePlayer(Player player) {
    player.setWalkSpeed(playerWalkSpeeds.get(player));
    player.addPotionEffects(playerPotionEffects.get(player));
    player.setFoodLevel(playerFoodLevels.get(player));
    player.getInventory().setContents(playerInventories.get(player));
    player.setHealth(playerHealth.get(player));
    songPlayerDuring.removePlayer(player);
    participants.remove(player);
    participantNextCheckpoints.remove(player);
    results.remove(player);
    for (RaceCheckpoint checkpoint : checkpoints) {
      checkpoint.removePlayer(player);
    }
  }

  public boolean isFull() {
    return participants.size() == startPoints.size();
  }

  public boolean isParticipating(Player player) {
    return participants.contains(player);
  }

  public void participate(Player player) {
    participants.add(player);
  }

  public void addStartTimerTask(int id) {
    startTimerTasks.add(id);
  }

  @EventHandler
  void onPlayerTeleport(PlayerTeleportEvent event) {
    if(isParticipating(event.getPlayer()) && (
      event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL ||
      event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT
      )) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  void onPlayerMove(PlayerMoveEvent event) {
    if(isParticipating(event.getPlayer())) {
      tryIncrementCheckpoint(event.getPlayer());
    }
  }

  @EventHandler
  void onFoodLevelChange(FoodLevelChangeEvent event) {
    if(event.getEntityType() == EntityType.PLAYER && isParticipating((Player)event.getEntity())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  void onEntityTarget(EntityTargetEvent event) {
    if((event.getTarget() instanceof Player) && isParticipating((Player) event.getTarget()) && state == RaceState.COUNTDOWN) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  void onPlayerQuit(PlayerQuitEvent event) {
    if(isParticipating(event.getPlayer()) && (state == RaceState.COUNTDOWN || state == RaceState.STARTED)) {
      restorePlayer(event.getPlayer());
      for(Player player : participants) {
        main.getMessageManager().setValue("player_name", event.getPlayer().getName());
        main.getMessageManager().sendMessage(player, "race_start_quit_disqualified");
      }

      if(participants.isEmpty()) {
        stop();
      }
    }
  }

  private void tryIncrementCheckpoint(Player player) {
    if(!participantNextCheckpoints.containsKey(player)) {
      participantNextCheckpoints.put(player, checkpoints.get(0));
      checkpoints.get(0).addPlayer(player);
    } else {
      RaceCheckpoint checkpoint = participantNextCheckpoints.get(player);
      if(checkpoint.isInside(player)) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        checkpoint.removePlayer(player);
        int checkpointIndex = checkpoints.indexOf(checkpoint);
        if(checkpointIndex == checkpoints.size() - 1) {
          RaceResult result = new RaceResult(this, player, results.size() + 1, MinecraftServer.currentTick - raceStartTick);
          results.put(player, result);
          if(results.size() == participants.size()) {
            handleComplete();
            stop();
          }
        } else {
          participantNextCheckpoints.put(player, checkpoints.get(checkpointIndex + 1));
          checkpoints.get(checkpointIndex + 1).addPlayer(player);
        }
      }
    }
  }

  private void handleComplete() {
    for(RaceResult result : results.values()) {
      if(result.getPosition() == 1) {
        main.getMessageManager().setValue("player_name", result.getPlayer().getName());
        main.getMessageManager().setValue("race_name", result.getRace().getName());
        main.getMessageManager().setValue("time", result.getTime() / 20);
        main.getMessageManager().broadcast("race_win");
        break;
      }
    }
  }

  private class Countdown extends BukkitRunnable {
    private int countdown = COUNTDOWN_IN_SECONDS;
    private Runnable runnable;

    Countdown(Runnable runnable) {
      this.runnable = runnable;
    }

    @Override
    public void run() {
      if(countdown == 0) {
        cancel();
        runnable.run();
        return;
      }

      for(Player participant : participants) {
        participant.sendTitle(String.valueOf(countdown), "sekunder innan racet börjar", 0, 21, 0);
        countdown -= 1;
      }
    }
  }

  private BaseComponent[] getTimeLeftMessage(String timeLeft) {
    return new ComponentBuilder("")
      .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Klicka här för att delta").create()))
      .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/race join " + name))
      .append("Det är " + timeLeft + " kvar innan racet startar. Skriv")
      .color(ChatColor.YELLOW)
      .append(" /race join " + name)
      .color(ChatColor.DARK_GRAY)
      .append(" för att delta.")
      .color(ChatColor.YELLOW).create();
  }
}

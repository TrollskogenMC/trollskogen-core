package com.github.hornta.trollskogen.racing.objects;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.enums.RaceState;
import com.github.hornta.trollskogen.racing.enums.RacingType;
import com.github.hornta.trollskogen.racing.events.PlayerSessionFinishRaceEvent;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.minecraft.server.v1_13_R2.MinecraftServer;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
  private List<BukkitTask> startTimerTasks = new ArrayList<>();
  private RaceCountdown countdown = new RaceCountdown(this);
  private RadioSongPlayer songPlayerStart;
  private RadioSongPlayer songPlayerDuring;

  private int startTick;
  private int numFinished;

  private Set<Player> participants = new HashSet<>();
  private Map<Player, RacePlayerSession> playerSessions = new HashMap<>();

  private static final long FIVE_MINUTES = 20L * 60 * 5;
  private static final long THREE_MINUTES = 20L * 60 * 3;
  private static final long ONE_MINUTE = 20L * 60;
  private static final long THIRTY_SECONDS = 20L * 30;

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

  public RaceCheckpoint getCheckpoint(Location location) {
    for(RaceCheckpoint checkpoint : checkpoints) {
      if(
        checkpoint.getCenter().getBlockX() == location.getBlockX() &&
        checkpoint.getCenter().getBlockY() == location.getBlockY() &&
        checkpoint.getCenter().getBlockZ() == location.getBlockZ()) {
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
      spawn.getBlockX(),
      spawn.getBlockY(),
      spawn.getBlockZ(),
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

  public Map<Player, RacePlayerSession> getPlayerSessions() {
    return playerSessions;
  }

  public void setState(RaceState state) {
    log("Change state from " + this.state + " to " + state);
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
    addStartTimerTask(Bukkit.getScheduler().scheduleSyncDelayedTask(main, this::actualStart, FIVE_MINUTES));
  }

  private void actualStart() {
    if(playerSessions.size() < Main.getTrollskogenConfig().getRequiredStartPoints()) {
      main.getMessageManager().broadcast("race_canceled");
      stop();
      return;
    }

    // check if players are online before countdown starts
    for(RacePlayerSession session : playerSessions.values()) {
      if(!session.getPlayer().isOnline()) {
        participants.remove(session.getPlayer());
        playerSessions.remove(session.getPlayer());
        for(RacePlayerSession session1 : playerSessions.values()) {
          main.getMessageManager().setValue("player_name", session.getPlayer().getName());
          main.getMessageManager().sendMessage(session1.getPlayer(), "race_start_noshow_disqualified");
        }
      }
    }

    if(playerSessions.isEmpty()) {
      main.getMessageManager().broadcast("race_canceled");
      stop();
      return;
    }

    setState(RaceState.COUNTDOWN);

    List<RacePlayerSession> shuffledSessions = new ArrayList<>(playerSessions.values());
    Collections.shuffle(shuffledSessions);

    for(RaceCheckpoint checkpoint : checkpoints) {
      checkpoint.startTask(main);
    }

    int startPointIndex = 0;
    for(RacePlayerSession session : shuffledSessions) {
      session.setStartPoint(startPoints.get(startPointIndex));
      session.setBossBar(Bukkit.createBossBar(name, BarColor.BLUE, BarStyle.SOLID));
      session.startCooldown();
      session.tryIncrementCheckpoint(this);
      songPlayerStart.addPlayer(session.getPlayer());
      startPointIndex += 1;
    }

    countdown.start(() -> {
      setState(RaceState.STARTED);
      for(RacePlayerSession session : playerSessions.values()) {
        session.startRace();
        songPlayerDuring.addPlayer(session.getPlayer());
      }
      songPlayerDuring.setTick((short)0);
      songPlayerDuring.setPlaying(true);
      startTick = MinecraftServer.currentTick;
    });
  }

  public void skipToCountdown() {
    for(BukkitTask task : startTimerTasks) {
      task.cancel();
    }

    if(state != RaceState.COUNTDOWN) {
      actualStart();
    }
  }

  public void stop() {
    log("Stopped");
    countdown.stop();

    for(BukkitTask task : startTimerTasks) {
      task.cancel();
    }
    startTimerTasks.clear();

    for (RacePlayerSession session : playerSessions.values()) {
      session.restore();
      songPlayerDuring.removePlayer(session.getPlayer());
    }
    songPlayerDuring.setPlaying(false);

    participants.clear();
    playerSessions.clear();

    if(state != RaceState.PREPARING) {
      for (RaceCheckpoint checkpoint : checkpoints) {
        checkpoint.stopTask();
      }
    }

    numFinished = 0;

    HandlerList.unregisterAll(this);
    setState(RaceState.IDLE);
  }

  public boolean isFull() {
    return participants.size() == startPoints.size();
  }

  public boolean isParticipating(Player player) {
    return participants.contains(player);
  }

  public void participate(Player player) {
    participants.add(player);
    playerSessions.put(player, new RacePlayerSession(this, player));
  }

  public void addStartTimerTask(int id) {
    startTimerTasks.add(Bukkit.getScheduler().getPendingTasks().stream().filter(t -> t.getTaskId() == id).findFirst().get());
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
    if(isParticipating(event.getPlayer()) && (state == RaceState.COUNTDOWN || state == RaceState.STARTED)) {
      playerSessions.get(event.getPlayer()).tryIncrementCheckpoint(this);

      if(
        Double.compare(event.getFrom().getX(), event.getTo().getX()) == 0 &&
        Double.compare(event.getFrom().getY(), event.getTo().getY()) == 0 &&
        Double.compare(event.getFrom().getZ(), event.getTo().getZ()) == 0
      ) {
        return;
      }

      // prevent player from moving after being teleported to the start point
      // will happen when player for example is holding walk forward button while being teleported
      if(state == RaceState.COUNTDOWN) {
        event.setTo(new Location(
          event.getFrom().getWorld(),
          event.getFrom().getX(),
          event.getFrom().getY(),
          event.getFrom().getZ(),
          event.getTo().getYaw(),
          event.getTo().getPitch()
        ));
      }
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
    Player player = event.getPlayer();
    if(isParticipating(player) && (state == RaceState.COUNTDOWN || state == RaceState.STARTED)) {
      playerSessions.get(player).restore();
      playerSessions.remove(player);
      participants.remove(player);
      for(Player player1 : participants) {
        main.getMessageManager().setValue("player_name", player.getName());
        main.getMessageManager().sendMessage(player1, "race_start_quit_disqualified");
      }

      if(playerSessions.isEmpty()) {
        stop();
      }
    }
  }

  @EventHandler
  void onPlayerDamage(EntityDamageEvent event) {
    if(event.getEntity() instanceof Player && isParticipating((Player) event.getEntity()) && event.getFinalDamage() >= ((Player) event.getEntity()).getHealth()) {
      playerSessions.get(event.getEntity()).respawnSafely(event);
    }
  }

  @EventHandler
  void onVehicleEnter(VehicleEnterEvent event) {
    if(event.getEntered() instanceof Player && isParticipating((Player) event.getEntered())) {
      RacePlayerSession session = playerSessions.get(event.getEntered());

      // if player is already mounted we need to cancel a new attempt to mount
      if(!session.isAllowedToEnterVehicle()) {
        event.setCancelled(true);
        log("Deny enter vehicle " + event.getVehicle().getEntityId());

        // because the player attempted to mount another vehicle, they become automatically dismounted from their current vehicle
        if(session.getVehicle() != event.getVehicle()) {
          // remount them onto their real vehicle
          Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
            session.enterVehicle();
            log("Reenter into vehicle " + session.getVehicle().getEntityId());
          });
        }

        return;
      }

      if(type == RacingType.PIG && event.getVehicle().getType() != EntityType.PIG) {
        event.setCancelled(true);
        log("Deny enter vehicle " + event.getVehicle().getEntityId());
      }

      if(type == RacingType.HORSE && event.getVehicle().getType() != EntityType.HORSE) {
        event.setCancelled(true);
        log("Deny enter vehicle " + event.getVehicle().getEntityId());
      }
    }
  }

  @EventHandler
  void onVehicleExit(VehicleExitEvent event) {
    if(
      event.getExited() instanceof Player &&
      isParticipating((Player) event.getExited()) &&
      !playerSessions.get(event.getExited()).isAllowedToExitVehicle()
    ) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  void onPlayerSessionFinishRace(PlayerSessionFinishRaceEvent event) {
    numFinished += 1;
    event.getSession().getResult().setPosition(numFinished);
    event.getSession().getResult().setTime(MinecraftServer.currentTick - startTick);

    if(numFinished == playerSessions.size()) {
      handleComplete();
      stop();
    }
  }

  @EventHandler
  void onPlayerDropItem(PlayerDropItemEvent event) {
    if(isParticipating(event.getPlayer()) && event.getItemDrop().getItemStack().getType() == Material.CARROT_ON_A_STICK) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  void onPlayerItemDamageEvent(PlayerItemDamageEvent event) {
    if(isParticipating(event.getPlayer()) && type == RacingType.PIG && event.getItem().getType() == Material.CARROT_ON_A_STICK) {
      event.setCancelled(true);
    }
  }

  private void handleComplete() {
    for(RacePlayerSession session : playerSessions.values()) {
      if(session.getResult().getPosition() == 1) {
        main.getMessageManager().setValue("player_name", session.getPlayer().getName());
        main.getMessageManager().setValue("race_name", name);
        main.getMessageManager().setValue("time", session.getResult().getTime() / 20);
        main.getMessageManager().broadcast("race_win");
        break;
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

  void log(String message) {
    Bukkit.getLogger().info("§a[Race " + name + "] " + message);
  }
}

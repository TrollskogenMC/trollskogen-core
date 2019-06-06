package com.github.hornta.trollskogen.racing;

import com.github.hornta.trollskogen.HandleRequest;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.commands.*;
import com.github.hornta.trollskogen.racing.commands.completers.PointCompleter;
import com.github.hornta.trollskogen.racing.commands.completers.RaceCompleter;
import com.github.hornta.trollskogen.racing.commands.completers.RacingTypeCompleter;
import com.github.hornta.trollskogen.racing.commands.completers.StartPointCompleter;
import com.github.hornta.trollskogen.racing.commands.validators.PointExistValidator;
import com.github.hornta.trollskogen.racing.commands.validators.RaceExistValidator;
import com.github.hornta.trollskogen.racing.commands.validators.RacingTypeValidator;
import com.github.hornta.trollskogen.racing.commands.validators.StartPointExistValidator;
import com.github.hornta.trollskogen.racing.enums.RacingType;
import com.github.hornta.trollskogen.racing.events.*;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.objects.RaceCheckpoint;
import com.github.hornta.trollskogen.racing.objects.RaceStartPoint;
import com.github.hornta.trollskogen.validators.RegexValidator;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class Racing implements Listener {
  private Main main;
  private Map<String, Race> races = new ConcurrentHashMap<>();
  private static final Pattern raceNamePattern = Pattern.compile("^\\S{6,20}$");
  private static final Vector HologramOffset = new Vector(0, 1, 0);
  private static final Vector CheckpointOffset = new Vector(0.5, 0.5, 0.5);
  private static final Vector StartPointOffset = new Vector(0.5, 0, 0.5);

  public Racing(Main main) {
    this.main = main;

    Bukkit.getLogger().info("Loading races...");
    main.getAsyncHttpClient()
      .prepareGet(main.getTrollskogenConfig().getAPIUrl() + "/races")
      .execute()
      .toCompletableFuture()
      .thenAccept(new HandleRequest((JsonElement json) -> {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {
          JsonArray jsonRaces = json.getAsJsonObject().get("races").getAsJsonArray();
          for(JsonElement jsonRace : jsonRaces) {
            Race race = parseRace(jsonRace.getAsJsonObject());
            races.put(race.getName(), race);
            Bukkit.getPluginManager().callEvent(new AddRaceEvent(race));
          }
          Bukkit.getLogger().info("Finished loading " + races.size() + " races.");
        });
      }));
    setupCommands();
  }

  public void shutdown() {
    for(Race race : races.values()) {
      race.stop();
    }
  }

  @EventHandler
  void onAddRace(AddRaceEvent event) {
    if(!event.getRace().isEditing()) {
      return;
    }

    for(RaceCheckpoint checkpoint : event.getRace().getCheckpoints()) {
      checkpoint.startTask(main, true);
      checkpoint.setHologram(getCheckpointHologram(checkpoint));
    }

    for(RaceStartPoint startPoint : event.getRace().getStartPoints()) {
      startPoint.setHologram(getStartPointHologram(startPoint));
    }
  }

  @EventHandler
  void onDeleteRace(DeleteRaceEvent event) {
    for(RaceCheckpoint checkpoint : event.getRace().getCheckpoints()) {
      checkpoint.stopTask();
      checkpoint.getHologram().delete();
      checkpoint.setHologram(null);
    }

    for(RaceStartPoint startPoint : event.getRace().getStartPoints()) {
      if(startPoint.getHologram() != null) {
        startPoint.getHologram().delete();
        startPoint.setHologram(null);
      }
    }
  }

  @EventHandler
  void onAddRaceCheckpoint(AddRaceCheckpointEvent event) {
    event.getCheckpoint().startTask(main, true);
    event.getCheckpoint().setHologram(getCheckpointHologram(event.getCheckpoint()));
  }

  @EventHandler
  void onDeleteRaceCheckpoint(DeleteRaceCheckpointEvent event) {
    event.getCheckpoint().stopTask();

    if(event.getCheckpoint().getHologram() != null) {
      event.getCheckpoint().getHologram().delete();
      event.getCheckpoint().setHologram(null);
    }
  }

  @EventHandler
  void onAddRaceStartPoint(AddRaceStartPointEvent event) {
    event.getStartPoint().setHologram(getStartPointHologram(event.getStartPoint()));
  }

  @EventHandler
  void onDeleteRaceStartPoint(DeleteRaceStartPointEvent event) {
    if(event.getStartPoint().getHologram() != null) {
      event.getStartPoint().getHologram().delete();
      event.getStartPoint().setHologram(null);
    }

    for (RaceStartPoint startPoint : event.getRace().getStartPoints()) {
      if (startPoint.getPosition() > event.getStartPoint().getPosition()) {
        if(startPoint.getHologram() != null) {
          startPoint.getHologram().delete();
          startPoint.setHologram(getStartPointHologram(startPoint));
        }
      }
    }
  }

  @EventHandler
  void onEditingRace(EditingRaceEvent event) {
    try {
      for (RaceCheckpoint checkpoint : event.getRace().getCheckpoints()) {
        if (event.isEditing()) {
          checkpoint.startTask(main, true);
          checkpoint.setHologram(getCheckpointHologram(checkpoint));
        } else {
          checkpoint.stopTask();
          checkpoint.getHologram().delete();
          checkpoint.setHologram(null);
        }
      }

      for (RaceStartPoint startPoint : event.getRace().getStartPoints()) {
        if (event.isEditing()) {
          startPoint.setHologram(getStartPointHologram(startPoint));
        } else {
          startPoint.getHologram().delete();
          startPoint.setHologram(null);
        }
      }
    } catch(Exception e) {
      Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
    }
  }

  public void createRace(Location location, String name, Consumer<Race> callback) {
    JsonObject json = new JsonObject();
    json.addProperty("name", name);
    json.addProperty("is_editing", true);
    json.addProperty("is_enabled", false);
    json.addProperty("is_started", false);
    json.addProperty("spawn_x", Math.round(location.getBlock().getLocation().getX()));
    json.addProperty("spawn_y", Math.round(location.getBlock().getLocation().getY()));
    json.addProperty("spawn_z", Math.round(location.getBlock().getLocation().getZ()));
    json.addProperty("spawn_pitch", location.getPitch());
    json.addProperty("spawn_yaw", location.getYaw());
    json.addProperty("spawn_world", location.getWorld().getName());
    json.addProperty("type", RacingType.PLAYER.toString().toLowerCase(Locale.ENGLISH));

    main.getAsyncHttpClient()
      .preparePost(main.getTrollskogenConfig().getAPIUrl() + "/races/race")
      .setBody(json.toString())
      .setHeader("Content-Type", "application/json")
      .execute()
      .toCompletableFuture()
      .exceptionally((Throwable t) -> {
        Bukkit.getLogger().log(Level.SEVERE, t.getMessage(), t);
        return null;
      })
      .thenAccept(new HandleRequest((JsonElement object) -> Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {
        Race race = parseRace(object.getAsJsonObject().get("race").getAsJsonObject());
        races.put(race.getName(), race);
        Bukkit.getPluginManager().callEvent(new AddRaceEvent(race));
        callback.accept(race);
      })));
  }

  public void deleteRace(Race race, Runnable runnable) {
    JsonPrimitive json = new JsonPrimitive(race.getId());
    main.getAsyncHttpClient()
      .prepareDelete(main.getTrollskogenConfig().getAPIUrl() + "/races/race")
      .setBody(json.toString())
      .setHeader("Content-Type", "application/json")
      .execute()
      .toCompletableFuture()
      .exceptionally((Throwable t) -> {
        Bukkit.getLogger().log(Level.SEVERE, t.getMessage(), t);
        return null;
      })
      .thenAccept(new HandleRequest((JsonElement object) -> Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {
        races.remove(race.getName());
        Bukkit.getPluginManager().callEvent(new DeleteRaceEvent(race));
        runnable.run();
      })));
  }

  public void addRaceStart(Location location, Race race, Consumer<RaceStartPoint> callback) {
    Location copiedLocation = location.clone();
    copiedLocation.setX(Math.round(location.getBlock().getLocation().getX()));
    copiedLocation.setY(Math.round(location.getBlock().getLocation().getY()));
    copiedLocation.setZ(Math.round(location.getBlock().getLocation().getZ()));

    int position = race.getStartPoints().size() + 1;

    JsonObject json = new JsonObject();
    json.addProperty("race_id", race.getId());
    json.addProperty("position", position);
    json.addProperty("location_x", copiedLocation.getX());
    json.addProperty("location_y", copiedLocation.getY());
    json.addProperty("location_z", copiedLocation.getZ());
    json.addProperty("location_yaw", location.getYaw());
    json.addProperty("location_pitch", location.getPitch());
    json.addProperty("location_world", location.getWorld().getName());

    main.getAsyncHttpClient()
      .preparePost(main.getTrollskogenConfig().getAPIUrl() + "/races/race/addStart")
      .setBody(json.toString())
      .setHeader("Content-Type", "application/json")
      .execute()
      .toCompletableFuture()
      .thenAccept(new HandleRequest((JsonElement object) -> Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {
        int id = object.getAsJsonObject().get("startPointId").getAsInt();
        RaceStartPoint startPoint = new RaceStartPoint(id, copiedLocation.add(StartPointOffset), position);
        race.addStartPoint(startPoint);
        callback.accept(startPoint);
      })));
  }

  public void deleteRaceStart(Race race, RaceStartPoint startPoint, Runnable callback) {
    JsonObject json = new JsonObject();
    json.addProperty("race_id", race.getId());
    json.addProperty("start_position", startPoint.getPosition());

    main.getAsyncHttpClient()
      .prepareDelete(main.getTrollskogenConfig().getAPIUrl() + "/races/race/start")
      .setBody(json.toString())
      .setHeader("Content-Type", "application/json")
      .execute()
      .toCompletableFuture()
      .thenAccept(new HandleRequest((JsonElement object) -> Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {
        race.deleteStartPoint(startPoint);

        for (RaceStartPoint startPoint1 : race.getStartPoints()) {
          if (startPoint1.getPosition() > startPoint.getPosition()) {
            startPoint1.setPosition(startPoint1.getPosition() - 1);
          }
        }
        Bukkit.getPluginManager().callEvent(new DeleteRaceStartPointEvent(race, startPoint));

        callback.run();
      })));
  }

  public void addPoint(Location location, Race race, Consumer<RaceCheckpoint> callback) {
    Location copiedLocation = location.clone();
    copiedLocation.setX(location.getBlockX());
    copiedLocation.setY(location.getBlockY());
    copiedLocation.setZ(location.getBlockZ());

    int position = race.getCheckpoints().size() + 1;
    int radius = 3;

    main.getAsyncHttpClient()
      .preparePost(main.getTrollskogenConfig().getAPIUrl() + "/race/point")
      .addQueryParam("race_id", String.valueOf(race.getId()))
      .addQueryParam("position", String.valueOf(position))
      .addQueryParam("location_x", String.valueOf(copiedLocation.getX()))
      .addQueryParam("location_y", String.valueOf(copiedLocation.getY()))
      .addQueryParam("location_z", String.valueOf(copiedLocation.getZ()))
      .addQueryParam("location_pitch", String.valueOf(copiedLocation.getPitch()))
      .addQueryParam("location_yaw", String.valueOf(copiedLocation.getYaw()))
      .addQueryParam("location_world", copiedLocation.getWorld().getName())
      .addQueryParam("radius", String.valueOf(radius))
      .execute()
      .toCompletableFuture()
      .exceptionally((Throwable t) -> {
        Bukkit.getLogger().log(Level.SEVERE, t.getMessage(), t);
        return null;
      })
      .thenAccept(new HandleRequest((JsonElement object) -> Bukkit.getScheduler().scheduleSyncDelayedTask(main,() -> {
        RaceCheckpoint checkpoint = new RaceCheckpoint(object.getAsJsonObject().get("racePointId").getAsInt(), position, copiedLocation.add(CheckpointOffset), radius);
        race.addPoint(checkpoint);
        Bukkit.getPluginManager().callEvent(new AddRaceCheckpointEvent(race, checkpoint));
        callback.accept(checkpoint);
      })));
  }

  public void deletePoint(Race race, RaceCheckpoint checkpoint, Runnable runnable) {
    main.getAsyncHttpClient()
      .prepareDelete(main.getTrollskogenConfig().getAPIUrl() + "/race/point")
      .addQueryParam("race_id", String.valueOf(race.getId()))
      .addQueryParam("race_point", String.valueOf(checkpoint.getPosition()))
      .execute()
      .toCompletableFuture()
      .exceptionally((Throwable t) -> {
        Bukkit.getLogger().log(Level.SEVERE, t.getMessage(), t);
        return null;
      })
      .thenAccept(new HandleRequest((JsonElement object) -> Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {
        race.delPoint(checkpoint);

        for (RaceCheckpoint checkpoint1 : race.getCheckpoints()) {
          if (checkpoint1.getPosition() > checkpoint.getPosition()) {
            checkpoint1.setPosition(checkpoint1.getPosition() - 1);
          }
        }

        Bukkit.getPluginManager().callEvent(new DeleteRaceCheckpointEvent(race, checkpoint));
        runnable.run();
      })));
  }

  public Race getRace(String name) {
    return races.get(name);
  }

  public List<Race> getRaces() {
    return new ArrayList<>(races.values());
  }

  public boolean hasRace(String name) {
    return races.containsKey(name);
  }

  private void setupCommands() {
    RaceExistValidator raceShouldExist = new RaceExistValidator(main, true);
    RaceExistValidator raceShouldNotExist = new RaceExistValidator(main, false);
    RaceCompleter raceCompleter = new RaceCompleter(this);
    RegexValidator raceNameValidator = new RegexValidator(main, raceNamePattern, "race_name_format");
    PointExistValidator pointShouldExist = new PointExistValidator(main, true);
    PointCompleter pointCompleter = new PointCompleter(this);
    StartPointExistValidator startPointShouldExist = new StartPointExistValidator(main, true);
    StartPointCompleter startPointCompleter = new StartPointCompleter(this);

    main.getCarbon()
      .addCommand("race", "create")
      .withHandler(new CommandCreateRace(main))
      .setHelpText("/race create <race>")
      .setNumberOfArguments(1)
      .validateArgument(0, raceShouldNotExist)
      .validateArgument(0, raceNameValidator)
      .requiresPermission("ts.race.create")
      .preventConsoleCommandSender();

    main.getCarbon()
      .addCommand("race", "delete")
      .withHandler(new CommandDeleteRace(main))
      .setHelpText("/race delete <race>")
      .setNumberOfArguments(1)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .requiresPermission("ts.race.delete");

    main.getCarbon()
      .addCommand("race", "list")
      .withHandler(new CommandRaces(main))
      .setHelpText("/race list")
      .requiresPermission("ts.race.races");

    main.getCarbon()
      .addCommand("race", "addpoint")
      .withHandler(new CommandRaceAddPoint(main))
      .setHelpText("/race addpoint <race>")
      .setNumberOfArguments(1)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .requiresPermission("ts.race.addpoint")
      .preventConsoleCommandSender();

    main.getCarbon()
      .addCommand("race", "delpoint")
      .withHandler(new CommandRaceDelPoint(main))
      .setHelpText("/race delpoint <race> <point>")
      .setNumberOfArguments(2)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .validateArgument(new int[] { 0, 1 }, pointShouldExist)
      .setTabComplete(new int[] { 0, 1 }, pointCompleter)
      .requiresPermission("ts.race.delpoint");

    main.getCarbon()
      .addCommand("race", "tppoint")
      .withHandler(new CommandRaceTeleportPoint(main))
      .setHelpText("/race tppoint <race> <point>")
      .setNumberOfArguments(2)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .validateArgument(new int[] { 0, 1 }, pointShouldExist)
      .setTabComplete(new int[] { 0, 1 }, pointCompleter)
      .requiresPermission("ts.race.tppoint")
      .preventConsoleCommandSender();

    main.getCarbon()
      .addCommand("race", "spawn")
      .withHandler(new CommandRaceSpawn(main))
      .setHelpText("/race spawn <race>")
      .setNumberOfArguments(1)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .requiresPermission("ts.race.spawn")
      .preventConsoleCommandSender();

    main.getCarbon()
      .addCommand("race", "setspawn")
      .withHandler(new CommandRaceSetSpawn(main))
      .setHelpText("/race setspawn <race>")
      .setNumberOfArguments(1)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .requiresPermission("ts.race.setspawn")
      .preventConsoleCommandSender();

    main.getCarbon()
      .addCommand("race", "disable")
      .withHandler(new CommandDisableRace(main))
      .setHelpText("/race disable <race>")
      .setNumberOfArguments(1)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .requiresPermission("ts.race.disable");

    main.getCarbon()
      .addCommand("race", "enable")
      .withHandler(new CommandEnableRace(main))
      .setHelpText("/race enable <race>")
      .setNumberOfArguments(1)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .requiresPermission("ts.race.enable");

    main.getCarbon()
      .addCommand("race", "setname")
      .withHandler(new CommandSetRaceName(main))
      .setHelpText("/race setname <race> <name>")
      .setNumberOfArguments(2)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .requiresPermission("ts.race.setname");

    main.getCarbon()
      .addCommand("race", "startedit")
      .withHandler(new CommandStartEditRace(main))
      .setHelpText("/race startedit <race>")
      .setNumberOfArguments(1)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .requiresPermission("ts.race.startedit");

    main.getCarbon()
      .addCommand("race", "stopedit")
      .withHandler(new CommandStopEditRace(main))
      .setHelpText("/race stopedit <race>")
      .setNumberOfArguments(1)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .requiresPermission("ts.race.stopedit");

    main.getCarbon()
      .addCommand("race", "addstart")
      .withHandler(new CommandAddStart(main))
      .setHelpText("/race addstart <race>")
      .setNumberOfArguments(1)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .requiresPermission("ts.race.addstart")
      .preventConsoleCommandSender();

    main.getCarbon()
      .addCommand("race", "delstart")
      .withHandler(new CommandDelStart(main))
      .setHelpText("/race delstart <race> <position>")
      .setNumberOfArguments(2)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .validateArgument(new int[] { 0, 1 }, startPointShouldExist)
      .setTabComplete(new int[] { 0, 1 }, startPointCompleter)
      .requiresPermission("ts.race.delstart");

    main.getCarbon()
      .addCommand("race", "tpstart")
      .withHandler(new CommandRaceTeleportStart(main))
      .setHelpText("/race tpstart <race> <position>")
      .setNumberOfArguments(2)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .validateArgument(new int[] { 0, 1 }, startPointShouldExist)
      .setTabComplete(new int[] { 0, 1 }, startPointCompleter)
      .requiresPermission("ts.race.tpstart")
      .preventConsoleCommandSender();

    main.getCarbon()
      .addCommand("race", "settype")
      .withHandler(new CommandSetType(main))
      .setHelpText("/race settype <race> <type>")
      .setNumberOfArguments(2)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .validateArgument(1, new RacingTypeValidator(main))
      .setTabComplete(1, new RacingTypeCompleter())
      .requiresPermission("ts.race.settype");

    main.getCarbon()
      .addCommand("race", "start")
      .withHandler(new CommandStartRace(main))
      .setHelpText("/race start <race>")
      .setNumberOfArguments(1)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .requiresPermission("ts.race.start")
      .preventConsoleCommandSender();

    main.getCarbon()
      .addCommand("race", "join")
      .withHandler(new CommandJoinRace(main))
      .setHelpText("/race join <race>")
      .setNumberOfArguments(1)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .requiresPermission("ts.race.join")
      .preventConsoleCommandSender();

    main.getCarbon()
      .addCommand("race", "stop")
      .withHandler(new CommandStopRace(main))
      .setHelpText("/race stop <race>")
      .setNumberOfArguments(1)
      .validateArgument(0, raceShouldExist)
      .setTabComplete(0, raceCompleter)
      .requiresPermission("ts.race.stop");
  }

  public void updateRace(Race race, Runnable runnable) {
    JsonObject json = new JsonObject();
    json.addProperty("id", race.getId());
    json.addProperty("name", race.getName());
    json.addProperty("is_editing", race.isEditing());
    json.addProperty("is_enabled", race.isEnabled());
    json.addProperty("spawn_x", Math.round(race.getSpawn().getX()));
    json.addProperty("spawn_y", Math.round(race.getSpawn().getX()));
    json.addProperty("spawn_z", Math.round(race.getSpawn().getX()));
    json.addProperty("spawn_pitch", race.getSpawn().getPitch());
    json.addProperty("spawn_yaw", race.getSpawn().getYaw());
    json.addProperty("spawn_world", race.getSpawn().getWorld().getName());

    main.getAsyncHttpClient()
      .preparePut(main.getTrollskogenConfig().getAPIUrl() + "/races/race")
      .setBody(json.toString())
      .setHeader("Content-Type", "application/json")
      .execute()
      .toCompletableFuture()
      .exceptionally((Throwable t) -> {
        Bukkit.getLogger().log(Level.SEVERE, t.getMessage(), t);
        return null;
      })
      .thenAccept(new HandleRequest((JsonElement response) -> Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, runnable)));
  }

  private Race parseRace(JsonObject json) {
    World world = Bukkit.getWorld(json.get("spawn_world").getAsString());
    Location location = new Location(
      world,
      json.get("spawn_x").getAsDouble(),
      json.get("spawn_y").getAsDouble(),
      json.get("spawn_z").getAsDouble(),
      json.get("spawn_yaw").getAsFloat(),
      json.get("spawn_pitch").getAsFloat());

    List<RaceCheckpoint> checkpoints = new ArrayList<>();
    JsonArray jsonCheckpoints = json.get("points").getAsJsonArray();
    for (JsonElement jsonCheckpoint : jsonCheckpoints) {
      JsonObject checkpointObject = jsonCheckpoint.getAsJsonObject();
      World checkpointWorld = Bukkit.getWorld(checkpointObject.get("location_world").getAsString());
      Location checkpointLocation = new Location(
        checkpointWorld,
        checkpointObject.get("location_x").getAsDouble(),
        checkpointObject.get("location_y").getAsDouble(),
        checkpointObject.get("location_z").getAsDouble(),
        checkpointObject.get("location_yaw").getAsFloat(),
        checkpointObject.get("location_pitch").getAsFloat()
      );
      checkpoints.add(new RaceCheckpoint(
        checkpointObject.get("id").getAsInt(),
        checkpointObject.get("position").getAsInt(),
        checkpointLocation.add(CheckpointOffset),
        checkpointObject.get("radius").getAsInt()
      ));
    }

    Collections.sort(checkpoints);

    List<RaceStartPoint> startPoints = new ArrayList<>();
    JsonArray jsonStartPoints = json.get("startPoints").getAsJsonArray();
    for(JsonElement jsonStartPoint : jsonStartPoints) {
      JsonObject startPointObject = jsonStartPoint.getAsJsonObject();
      World startPointWorld = Bukkit.getWorld(startPointObject.get("location_world").getAsString());
      Location checkpointLocation = new Location(
        startPointWorld,
        startPointObject.get("location_x").getAsDouble(),
        startPointObject.get("location_y").getAsDouble(),
        startPointObject.get("location_z").getAsDouble(),
        startPointObject.get("location_yaw").getAsFloat(),
        startPointObject.get("location_pitch").getAsFloat());
      int id = startPointObject.get("id").getAsInt();
      startPoints.add(new RaceStartPoint(
        id,
        checkpointLocation.add(StartPointOffset),
        startPointObject.get("position").getAsInt()
      ));
    }

    return new Race(
      main,
      json.get("id").getAsInt(),
      json.get("name").getAsString(),
      location,
      json.get("is_enabled").getAsBoolean(),
      json.get("is_editing").getAsBoolean(),
      LocalDateTime.parse(json.get("created_at").getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME),
      checkpoints,
      startPoints,
      RacingType.fromString(json.get("type").getAsString())
    );
  }

  private Hologram getStartPointHologram(RaceStartPoint startPoint) {
    Hologram hologram = HologramsAPI.createHologram(main, startPoint.getLocation().add(HologramOffset));
    hologram.appendTextLine("§d" + startPoint.getPosition());
    return hologram;
  }

  private Hologram getCheckpointHologram(RaceCheckpoint checkpoint) {
    Hologram hologram = HologramsAPI.createHologram(main, checkpoint.getCenter().add(HologramOffset));
    hologram.appendTextLine("§d" + checkpoint.getPosition());
    return hologram;
  }
}

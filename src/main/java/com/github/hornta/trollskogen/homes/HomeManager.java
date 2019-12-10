package com.github.hornta.trollskogen.homes;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.hornta.carbon.CarbonArgument;
import com.github.hornta.trollskogen.*;
import com.github.hornta.trollskogen.commands.argumentHandlers.HomeArgumentHandler;
import com.github.hornta.trollskogen.commands.argumentHandlers.OpenHomePlayersArgumentHandler;
import com.github.hornta.trollskogen.commands.argumentHandlers.PlayerHomeArgumentHandler;
import com.github.hornta.trollskogen.commands.argumentHandlers.PlayerOpenHomeArgumentHandler;
import com.github.hornta.trollskogen.homes.commands.*;
import com.github.hornta.trollskogen.homes.deserializers.HomesDeserializer;
import com.github.hornta.trollskogen.homes.deserializers.PatchedHomeDeserializer;
import com.github.hornta.trollskogen.homes.deserializers.PostedHomeDeserializer;
import com.github.hornta.trollskogen.homes.events.*;
import com.github.hornta.trollskogen.users.UserObject;
import com.github.hornta.trollskogen.users.events.LoadUsersEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.asynchttpclient.Response;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class HomeManager implements Listener {
  private RTree<Home, Point> tree;
  private List<Home> homes;
  private HashMap<Integer, List<Home>> userToHomes;
  private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
  private boolean hasLoadedAllHomes;

  public HomeManager()  {
    tree = RTree.create();
    homes = new ArrayList<>();
    userToHomes = new HashMap<>();
    hasLoadedAllHomes = false;
  }

  public Home getNearestPublicHome(UserObject user, Location location) {
    Entry<Home, Point> homePointEntry = tree
      .search(Geometries.circle(location.getX(), location.getZ(), 32))
      .filter((Entry<Home, Point> entry) -> {
        if(!entry.value().isPublic()) {
          return false;
        }

        return entry.value().getOwner() != (user.getId());
      }).toBlocking().firstOrDefault(null);

    if(homePointEntry == null) {
      return null;
    }

    return homePointEntry.value();
  }

  public Home getHome(String name, int owner) {
    List<Home> homes = userToHomes.getOrDefault(owner, Collections.emptyList());
    for(Home home : homes) {
      if(home.getName().equalsIgnoreCase(name)) {
        return home;
      }
    }
    return null;
  }

  public List<Home> getHomes(int userId) {
    return userToHomes.getOrDefault(userId, Collections.emptyList());
  }

  public boolean hasOpenHomes(UserObject user) {
    if(!userToHomes.containsKey(user.getId())) {
      return false;
    }
    for(Home home : userToHomes.get(user.getId())) {
      if(home.isPublic()) {
        return true;
      }
    }
    return false;
  }

  public boolean hasLoadedAllHomes() {
    return hasLoadedAllHomes;
  }

  @EventHandler
  void onLoadUsers(LoadUsersEvent event) {
    loadAllHomes();
  }

  @EventHandler
  void onLoadHomes(LoadHomesEvent event) {
    for(Home home : homes) {
      tree = tree.add(home, Geometries.point(home.getLocation().getX(), home.getLocation().getZ()));
    }
  }

  @EventHandler
  void onRequestSetHome(RequestSetHomeEvent event) {
    Home existing = getHome(event.getName(), event.getUser().getId());
    if(existing == null) {
      postHome(event.getName(), event.getLocation(), event.getUser());
      return;
    }
    JsonObject json = generateHomeJson(existing);
    json.addProperty("pitch", event.getLocation().getPitch());
    json.addProperty("yaw", event.getLocation().getYaw());
    json.addProperty("world", event.getLocation().getWorld().getName());
    json.addProperty("x", event.getLocation().getX());
    json.addProperty("y", event.getLocation().getY());
    json.addProperty("z", event.getLocation().getZ());
    patchHome(existing, json);
  }

  @EventHandler
  void onRequestDeleteHome(RequestDeleteHomeEvent event) {
    deleteHome(event.getHome());
  }

  @EventHandler
  void onRequestToggleCommands(RequestToggleCommandsEvent event) {
    JsonObject json = generateHomeJson(event.getHome());
    json.addProperty("allow_commands", !event.getHome().isAllowCommands());
    patchHome(event.getHome(), json);
  }

  @EventHandler
  void onRequestCloseHome(RequestCloseHomeEvent event) {
    JsonObject json = generateHomeJson(event.getHome());
    json.addProperty("is_open", false);
    patchHome(event.getHome(), json);
  }

  @EventHandler
  void onRequestOpenHome(RequestOpenHomeEvent event) {
    JsonObject json = generateHomeJson(event.getHome());
    json.addProperty("is_open", true);
    patchHome(event.getHome(), json);
  }

  @EventHandler
  void onDeleteHome(DeleteHomeEvent event) {
    tree = tree.delete(event.getHome(), event.getHome().getGeometry());
  }

  public static void setupCommands(Main main, CarbonArgument playerArg) {
    HomeArgumentHandler homeArgumentHandler = new HomeArgumentHandler(main);
    CarbonArgument homeArgument = new CarbonArgument.Builder("home")
      .setHandler(homeArgumentHandler)
      .setDefaultValue(Player.class, (CommandSender sender, String[] args) -> {
        UserObject user = main.getUser(((Player)sender).getUniqueId());
        List<Home> homes = main.getHomeManager().getHomes(user.getId());
        if(homes.size() > 0) {
          return homes.get(0).getName();
        }
        return "";
      })
      .create();

    main
      .getCarbon()
      .addCommand("home")
      .withArgument(homeArgument)
      .withHandler(new CommandHome(main))
      .requiresPermission("ts.home")
      .preventConsoleCommandSender();

    main
      .getCarbon()
      .addCommand("sethome")
      .withArgument(
        new CarbonArgument.Builder("home")
          .setPattern(Pattern.compile("[a-z0-9_]+", Pattern.CASE_INSENSITIVE))
          .setDefaultValue(Player.class, Home.DEFAULT_HOME_NAME)
          .create()
      )
      .withHandler(new CommandSetHome(main))
      .requiresPermission("ts.sethome")
      .preventConsoleCommandSender();

    main
      .getCarbon()
      .addCommand("delhome")
      .withArgument(
        new CarbonArgument.Builder("home")
          .setHandler(homeArgumentHandler)
          .create()
      )
      .withHandler(new CommandDelHome(main))
      .requiresPermission("ts.delhome")
      .preventConsoleCommandSender();

    main
      .getCarbon()
      .addCommand("homes")
      .withHandler(new CommandHomes(main))
      .requiresPermission("ts.homes")
      .preventConsoleCommandSender();

    CarbonArgument playerHomeArgumentHandler = new CarbonArgument.Builder("home")
      .setHandler(new PlayerHomeArgumentHandler(main))
      .dependsOn(playerArg)
      .create();

    main
      .getCarbon()
      .addCommand("ahome")
      .withHandler(new CommandAHome(main))
      .withArgument(playerArg)
      .withArgument(playerHomeArgumentHandler)
      .requiresPermission("ts.ahome")
      .preventConsoleCommandSender();

    main
      .getCarbon()
      .addCommand("openhome")
      .withHandler(new CommandOpenHome(main))
      .withArgument(homeArgument)
      .requiresPermission("ts.openhome")
      .preventConsoleCommandSender();

    main
      .getCarbon()
      .addCommand("closehome")
      .withHandler(new CommandCloseHome(main))
      .withArgument(homeArgument)
      .requiresPermission("ts.closehome")
      .preventConsoleCommandSender();

    main
      .getCarbon()
      .addCommand("togglehomecmds")
      .withHandler(new CommandHomeToggleCommands(main))
      .withArgument(homeArgument)
      .requiresPermission("ts.togglehomecmds")
      .preventConsoleCommandSender();

    OpenHomePlayersArgumentHandler openHomePlayersArgumentHandler = new OpenHomePlayersArgumentHandler(main);
    Bukkit.getPluginManager().registerEvents(openHomePlayersArgumentHandler, main);

    CarbonArgument openHomePlayerArgument = new CarbonArgument.Builder("player")
      .setHandler(openHomePlayersArgumentHandler)
      .create();


    CommandPHome phome = new CommandPHome(main);
    Bukkit.getPluginManager().registerEvents(phome, main);
    main
      .getCarbon()
      .addCommand("phome")
      .withHandler(phome)
      .withArgument(openHomePlayerArgument)
      .withArgument(
        new CarbonArgument.Builder("home")
          .dependsOn(openHomePlayerArgument)
          .setHandler(new PlayerOpenHomeArgumentHandler(main))
          .setDefaultValue(CommandSender.class, (CommandSender sender, String[] args) -> {
            UserObject user = main.getUser(args[0]);
            List<Home> userHomes = main.getHomeManager().getHomes(user.getId());
            for(Home home : userHomes) {
              if(home.isPublic()) {
                return home.getName();
              }
            }
            return "";
          })
          .create()
      )
      .requiresPermission("ts.phome")
      .preventConsoleCommandSender();
  }

  private void postHome(String name, Location location, UserObject userObject) {
    JsonObject json = new JsonObject();
    json.addProperty("allow_commands", false);
    json.addProperty("is_open", false);
    json.addProperty("name", name);
    json.addProperty("user_id", userObject.getId());
    json.addProperty("pitch", location.getPitch());
    json.addProperty("yaw", location.getYaw());
    json.addProperty("world", location.getWorld().getName());
    json.addProperty("x", location.getX());
    json.addProperty("y", location.getY());
    json.addProperty("z", location.getZ());

    scheduledExecutor.submit(() -> {
      Main.request("POST", "/home", json, (Response response) -> {
        Gson gson = new GsonBuilder()
          .registerTypeAdapter(Home.class, new PostedHomeDeserializer())
          .create();
        Home home;
        try {
          home = gson.fromJson(response.getResponseBody(), Home.class);
        } catch (Throwable ex) {
          Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
          return;
        }
        Bukkit.getScheduler().callSyncMethod(Main.getPlugin(), () -> {
          homes.add(home);
          userToHomes.putIfAbsent(home.getOwner(), new ArrayList<>());
          userToHomes.get(home.getOwner()).add(home);
          SetHomeEvent event = new SetHomeEvent(home);
          Bukkit.getPluginManager().callEvent(event);
          return null;
        });
      });
    });
  }

  private void patchHome(Home home, JsonObject json) {
    scheduledExecutor.submit(() -> {
      Main.request("PATCH", "/home/" + home.getId(), json, (Response response) -> {
        Gson gson = new GsonBuilder()
          .registerTypeAdapter(Home.class, new PatchedHomeDeserializer())
          .create();
        Home parsedHome;
        try {
          parsedHome = gson.fromJson(response.getResponseBody(), Home.class);
        } catch (Throwable ex) {
          Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
          return;
        }
        Bukkit.getScheduler().callSyncMethod(Main.getPlugin(), () -> {
          Point prevGeometry = home.getGeometry();
          home.setLocation(parsedHome.getLocation());
          home.setAllowCommands(parsedHome.isAllowCommands());
          home.setPublic(parsedHome.isPublic());
          tree = tree.delete(home, prevGeometry);
          tree = tree.add(home, home.getGeometry());
          SetHomeEvent event = new SetHomeEvent(home);
          Bukkit.getPluginManager().callEvent(event);
          return null;
        });
      });
    });
  }

  private JsonObject generateHomeJson(Home home) {
    JsonObject json = new JsonObject();
    json.addProperty("allow_commands", home.isAllowCommands());
    json.addProperty("is_open", home.isPublic());
    json.addProperty("name", home.getName());
    json.addProperty("user_id", home.getOwner());
    json.addProperty("pitch", home.getLocation().getPitch());
    json.addProperty("yaw", home.getLocation().getYaw());
    json.addProperty("world", home.getLocation().getWorld().getName());
    json.addProperty("x", home.getLocation().getX());
    json.addProperty("y", home.getLocation().getY());
    json.addProperty("z", home.getLocation().getZ());
    return json;
  }

  private void deleteHome(Home home) {
    scheduledExecutor.submit(() -> {
      Main.request("DELETE", "/home/" + home.getId(), (Response response) -> {
        Bukkit.getScheduler().callSyncMethod(Main.getPlugin(), () -> {
          if(response.getStatusCode() == 200) {
            homes.remove(home);
            userToHomes.get(home.getOwner()).remove(home);
            if(userToHomes.get(home.getOwner()).isEmpty()) {
              userToHomes.remove(home.getOwner());
            }
          }
          DeleteHomeEvent event = new DeleteHomeEvent(home);
          Bukkit.getPluginManager().callEvent(event);
          return null;
        });
      });
    });
  }

  private void loadAllHomes() {
    hasLoadedAllHomes = false;
    scheduledExecutor.submit(() -> {
      Main.request("GET", "/homes", (Response response) -> {
        Gson gson = new GsonBuilder()
          .registerTypeAdapter(Home[].class, new HomesDeserializer())
          .create();
        Home[] parsedHomes;
        try {
          parsedHomes = gson.fromJson(response.getResponseBody(), Home[].class);
        } catch (Throwable ex) {
          Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
          return;
        }

        Bukkit.getScheduler().callSyncMethod(Main.getPlugin(), () -> {
          tree = RTree.create();
          homes.clear();
          userToHomes.clear();
          for(Home home : parsedHomes) {
            homes.add(home);
            userToHomes.putIfAbsent(home.getOwner(), new ArrayList<>());
            userToHomes.get(home.getOwner()).add(home);
          }
          Bukkit.getLogger().info("Loaded " + parsedHomes.length + " homes");
          Bukkit.getPluginManager().callEvent(new LoadHomesEvent(this));
          hasLoadedAllHomes = true;
          return null;
        });
      });
    });
  }

  public static Home parseHome(JsonObject json) {
    World world = Bukkit.getWorld(json.get("world").getAsString());
    if(world == null) {
      return null;
    }

    Location location = new Location(
      world,
      json.get("x").getAsDouble(),
      json.get("y").getAsDouble(),
      json.get("z").getAsDouble(),
      json.get("yaw").getAsFloat(),
      json.get("pitch").getAsFloat()
    );

    return new Home(
      json.get("id").getAsInt(),
      json.get("name").getAsString(),
      location,
      json.get("is_open").getAsBoolean(),
      json.get("user_id").getAsInt(),
      json.get("allow_commands").getAsBoolean()
    );
  }
}

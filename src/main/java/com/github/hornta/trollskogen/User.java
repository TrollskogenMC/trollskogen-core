package com.github.hornta.trollskogen;

import com.github.hornta.trollskogen.events.BanUserEvent;
import com.github.hornta.trollskogen.events.UnbanUserEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;

public class User {
  private Main main;
  private OfflinePlayer player;
  private String lastSeenAs;
  private ParticleEffect selectedEffect;
  private boolean isVerifiedDiscord;
  private boolean isBanned;
  private LocalDateTime banExpiration;
  private String banReason;
  private List<Home> homes = Collections.emptyList();

  User(Main main, UUID uuid) {
    this.main = main;
    player = Bukkit.getOfflinePlayer(uuid);
    lastSeenAs = player.getName();
    selectedEffect = null;
    isVerifiedDiscord = false;
    isBanned = false;
    banReason = null;
    banExpiration = null;
  }

  User(Main main, String uuid, ConfigurationSection config) {
    this.main = main;
    lastSeenAs = config.getString("lastSeenAs");
    selectedEffect = ParticleEffect.getParticleEffect(config.getString("selectedEffect"));
    isVerifiedDiscord = config.getBoolean("isVerifiedDiscord");
    isBanned = config.getBoolean("isBanned");
    banReason = config.getString("banReason");

    String banExpiration = config.getString("banExpiration", null);
    if(banExpiration == null) {
      this.banExpiration = null;
    } else {
      this.banExpiration = LocalDateTime.parse(banExpiration);
    }

    player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

    ConfigurationSection homesSection = config.getConfigurationSection("homes");
    if(homesSection != null) {
      homes = new ArrayList<>();
      for(String key : homesSection.getKeys(false)) {
        ConfigurationSection homeSection = homesSection.getConfigurationSection(key);
        World world = Bukkit.getWorld(homeSection.getString("world"));
        if(world == null) {
          Bukkit.getLogger().log(Level.WARNING, "World `{0}` does not exist", homeSection.getString("world"));
          continue;
        }
        homes.add(new Home(key,
          new Location(
            world,
            homeSection.getDouble("x"),
            homeSection.getDouble("y"),
            homeSection.getDouble("z"),
            (float)homeSection.getDouble("yaw"),
            (float)homeSection.getDouble("pitch"))));
      }
    } else {
      homes = Collections.emptyList();
    }
  }

  boolean setSelectedEffect(ParticleEffect selectedEffect) {
    if(
      (selectedEffect == null && this.selectedEffect == null) ||
        (selectedEffect == this.selectedEffect)
      ) {
      return false;
    }

    this.selectedEffect = selectedEffect;
    setDirty();

    if(!(player instanceof Player)) {
      return false;
    }

    if(selectedEffect != null) {
      main.getParticleManager().useParticle((Player) player, selectedEffect);
    } else {
      main.getParticleManager().reset((Player) player);
    }

    return true;
  }

  public void setPlayer(Player player) {
    this.player = player;
    if(selectedEffect != null) {
      main.getParticleManager().useParticle(player, selectedEffect);
    }

    if(!lastSeenAs.equals(player.getName())) {
      lastSeenAs = player.getName();
      setDirty();
    }
  }

  public OfflinePlayer getPlayer() {
    return player;
  }

  public String getLastSeenAs() {
    return lastSeenAs;
  }

  public ParticleEffect getSelectedEffect() {
    return selectedEffect;
  }

  public boolean isVerifiedDiscord() {
    return isVerifiedDiscord;
  }

  boolean isBanned() {
    return isBanned;
  }

  public String getBanReason() {
    return banReason;
  }

  public LocalDateTime getBanExpiration() {
    return banExpiration;
  }

  List<Home> getHomes() {
    return homes;
  }

  Home getHome(String name) {
    for(Home home : homes) {
      if(home.getName().equalsIgnoreCase(name)) {
        return home;
      }
    }
    return null;
  }

  String getFirstHomeName() {
    if(homes.isEmpty()) {
      return null;
    }

    return homes.get(0).getName();
  }

  int getMaxHomes() {
    int numOfHomes = main.getTrollskogenConfig().getNumHomesPermission(this);

    if(isVerifiedDiscord) {
      numOfHomes += main.getTrollskogenConfig().getDiscordVerifiedNumHomes();
    }

    return numOfHomes;
  }

  void setHome(String name, Location location) {
    Home home = getHome(name);
    if(home == null) {
      if(homes.isEmpty()) {
        homes = new ArrayList<>();
      }
      homes.add(new Home(name, location));
      setDirty();
    } else {
      if(!home.getLocation().equals(location)) {
        setDirty();
      }
      home.setLocation(location);
    }
  }

  Home deleteHome(String name) {
    Home home = getHome(name);
    if(home != null) {
      homes.remove(home);
      setDirty();
    }
    return home;
  }

  void ban(String banReason, LocalDateTime banExpiration) {
    boolean oldIsBanned = isBanned;
    String oldBanReason = this.banReason;
    LocalDateTime oldBanExpiration = this.banExpiration;

    isBanned = true;
    this.banReason = banReason;
    this.banExpiration = banExpiration;

    if(!oldIsBanned || !oldBanReason.equals(this.banReason) || !Objects.equals(oldBanExpiration, this.banExpiration)) {
      setDirty();
      Bukkit.getPluginManager().callEvent(new BanUserEvent(this));
    }
  }

  void unban() {
    boolean oldIsBanned = isBanned;
    String oldBanReason = banReason;
    LocalDateTime oldBanExpiration = banExpiration;

    isBanned = false;
    banReason = null;
    banExpiration = null;

    if(oldIsBanned || oldBanReason != null || oldBanExpiration != null) {
      setDirty();
      Bukkit.getPluginManager().callEvent(new UnbanUserEvent(this));
    }
  }

  void unbanIfExpired() {
    if(banExpiration == null) {
      return;
    }

    LocalDateTime now = LocalDateTime.now();
    if(banExpiration.isBefore(now) || banExpiration.isEqual(now)) {
      Bukkit.getLogger().info(() -> String.format("%s's ban expired, unbanning.", lastSeenAs));
      unban();
    }
  }

  private void setDirty() {
    main.getUserManager().setPendingWrite();
  }

  String getBanMessage() {
    String messageType;
    if(banExpiration == null) {
      messageType = "kickban_permanent";
    } else {
      messageType = "kickban_temporary";
      main.getMessageManager().setValue("time_left", DateUtils.formatDateDiff(banExpiration));
    }
    main.getMessageManager().setValue("reason", banReason);
    return main.getMessageManager().getMessage(messageType);
  }
}

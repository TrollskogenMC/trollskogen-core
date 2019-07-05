package com.github.hornta.trollskogen;

import com.github.hornta.trollskogen.messagemanager.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TrollskogenConfig {
  private Config file;
  private ItemStack[] starterInventory;
  private int discordVerifiedNumHomes;
  private Map<String, Integer> homePermissions = Collections.emptyMap();
  private List<String> allowedMaintenance;
  private String APIUrl;
  private int requiredStartPoints;

  TrollskogenConfig(Main main) {
    this.file = new Config(main, "config.yml");
    this.file.saveDefault();
    this.load();
  }

  private void load() {
    List<?> itemStacks = file.getConfig().getList("starter-inventory");
    if(itemStacks != null) {
      starterInventory = new ItemStack[itemStacks.size()];
      file.getConfig().getList("starter-inventory").toArray(starterInventory);
    }

    discordVerifiedNumHomes = file.getConfig().getInt("discord_verified_num_homes", 0);

    ConfigurationSection homePermsSection = file.getConfig().getConfigurationSection("home_perms");
    if(homePermsSection != null) {
      homePermissions = new HashMap<>();
      for(String key : homePermsSection.getKeys(false)) {
        homePermissions.put("ts.sethome." + key, homePermsSection.getInt(key));
      }
    }

    allowedMaintenance = file.getConfig().getStringList("maintenance");
    APIUrl = file.getConfig().getString("api_url");
    requiredStartPoints = file.getConfig().getInt("racing_required_start_points");
  }

  public void reload() {
    file.reloadConfig();
    this.load();
  }

  private void save() {
    file.getConfig().set("starter-inventory", starterInventory);
    file.save();
  }

  public void setStarterKit(Inventory starterInventory) {
    this.starterInventory = new ItemStack[starterInventory.getContents().length];
    System.arraycopy(starterInventory.getContents(), 0, this.starterInventory, 0, starterInventory.getContents().length);
    this.save();
  }

  public void setStarterInventory(User user) {
    if(starterInventory == null) {
      return;
    }

    ((Player)user.getPlayer()).getInventory().setContents(starterInventory);
  }

  int getDiscordVerifiedNumHomes() {
    return discordVerifiedNumHomes;
  }

  int getNumHomesPermission(User user) {
    int numHomes = 0;
    for(Map.Entry<String, Integer> entry : homePermissions.entrySet()) {
      if(((Player)user.getPlayer()).hasPermission(entry.getKey()) && entry.getValue() > numHomes) {
        numHomes = entry.getValue();
      }
    }
    return numHomes;
  }

  public Map<String, Integer> getHomePermissions() {
    return homePermissions;
  }

  public List<String> getAllowedMaintenance() {
    return allowedMaintenance;
  }

  public String getAPIUrl() {
    return APIUrl;
  }

  public int getRequiredStartPoints() {
    return requiredStartPoints;
  }
}

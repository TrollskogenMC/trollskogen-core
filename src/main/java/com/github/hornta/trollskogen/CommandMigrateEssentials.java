package com.github.hornta.trollskogen;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import se.hornta.carbon.ICommandHandler;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;

public class CommandMigrateEssentials implements ICommandHandler {
  private Main main;

  CommandMigrateEssentials(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args) {
    File userDataFolder = main.getDataFolder().toPath().getParent().resolve("Essentials/userdata").toFile();
    if(!userDataFolder.exists()) {
      sender.sendMessage(userDataFolder.toString() + " does not exist");
      return;
    }

    File[] files = userDataFolder.listFiles();
    if(files == null) {
      Bukkit.getLogger().severe("Failed to list files");
      return;
    }

    for(File file : files) {
      String filename = file.getName();
      UUID uuid;
      try {
        YamlConfiguration userConf = YamlConfiguration.loadConfiguration(file);
        if(userConf.getBoolean("npc")) {
          continue;
        }
        uuid = UUID.fromString(filename.substring(0, filename.lastIndexOf('.')));
        Bukkit.getLogger().info("Migrating UUID " + uuid.toString());
        User user = main.getUserManager().getUser(uuid);
        ConfigurationSection homesSection = userConf.getConfigurationSection("homes");
        if(homesSection != null) {
          for(String key : homesSection.getKeys(false)) {
            ConfigurationSection homeSection = homesSection.getConfigurationSection(key);
            World world = Bukkit.getWorld(homeSection.getString("world"));
            if(world == null) {
              Bukkit.getLogger().log(Level.WARNING, "World `{0}` does not exist", homeSection.getString("world"));
              continue;
            }
            user.setHome(key,
              new Location(
                world,
                homeSection.getDouble("x"),
                homeSection.getDouble("y"),
                homeSection.getDouble("z"),
                (float)homeSection.getDouble("yaw"),
                (float)homeSection.getDouble("pitch")));
          }
        }
      } catch (IllegalArgumentException ex) {
        Bukkit.getLogger().log(Level.WARNING, ex.getMessage(), ex);
      }
    }
  }
}

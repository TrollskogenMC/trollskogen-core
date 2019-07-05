package com.github.hornta.trollskogen.messagemanager;

import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class Config {
  private File file;
  private JavaPlugin plugin;
  private FileConfiguration newConfig;
  private String relativeFilepath;

  public Config(JavaPlugin plugin, String relativeFilepath) {
    this.plugin = plugin;
    this.relativeFilepath = relativeFilepath;
    file = new File(plugin.getDataFolder(), relativeFilepath);
  }

  public void saveDefault() {
    if (!file.exists()) {
      plugin.saveResource(relativeFilepath, false);
    }
  }

  public FileConfiguration getConfig() {
    if (newConfig == null) {
      reloadConfig();
    }
    return newConfig;
  }

  public void save() {
    try {
      getConfig().save(file);
    } catch (IOException ex) {
      Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + file, ex);
    }
  }

  public void reloadConfig() {
    saveDefault();
    newConfig = YamlConfiguration.loadConfiguration(file);

    final InputStream defConfigStream = plugin.getResource(file.getPath());
    if (defConfigStream == null) {
      return;
    }

    newConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
  }
}

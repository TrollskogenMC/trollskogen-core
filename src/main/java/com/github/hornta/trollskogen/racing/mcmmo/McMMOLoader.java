package com.github.hornta.trollskogen.racing.mcmmo;

import com.github.hornta.trollskogen.Main;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class McMMOLoader {

  public McMMOLoader() {
    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("mcMMO");

    if (!(plugin instanceof mcMMO)) {
      return;
    }

    Bukkit.getPluginManager().registerEvents(new McMMOListener(), Main.getPlugin());
  }
}

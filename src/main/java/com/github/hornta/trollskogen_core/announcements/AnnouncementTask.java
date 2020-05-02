package com.github.hornta.trollskogen_core.announcements;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AnnouncementTask extends BukkitRunnable {
  private final JavaPlugin plugin;
  private final AnnouncementManager announcements;

  AnnouncementTask(JavaPlugin plugin, AnnouncementManager announcements) {
    this.plugin = plugin;
    this.announcements = announcements;
  }

  @Override
  public void run() {
    Announcement announcement = announcements.getNextAnnouncement();
    if(announcement == null) {
      return;
    }

    plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', announcement.getMessage()));
  }
}

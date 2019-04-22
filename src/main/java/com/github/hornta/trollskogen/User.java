package com.github.hornta.trollskogen;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class User {
  private Main main;
  private OfflinePlayer player;
  private String lastSeenAs;
  private ParticleEffect selectedEffect;
  private boolean isVerifiedDiscord;
  private boolean isBanned;
  private LocalDateTime banExpiration;
  private String banReason;

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

  void ban(String banReason, LocalDateTime banExpiration) {
    boolean oldIsBanned = isBanned;
    String oldBanReason = this.banReason;
    LocalDateTime oldBanExpiration = this.banExpiration;

    isBanned = true;
    this.banReason = banReason;
    this.banExpiration = banExpiration;

    if(!oldIsBanned || !oldBanReason.equals(this.banReason) || !Objects.equals(oldBanExpiration, this.banExpiration)) {
      setDirty();
    }
  }

  void unban() {
    boolean oldIsBanned = isBanned;
    String oldBanReason = this.banReason;
    LocalDateTime oldBanExpiration = this.banExpiration;

    isBanned = false;
    this.banReason = null;
    this.banExpiration = null;

    if(oldIsBanned || oldBanReason != null || oldBanExpiration != null) {
      setDirty();
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

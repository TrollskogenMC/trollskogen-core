package com.github.hornta.trollskogen.users;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.*;

public class UserObject {
  private Integer id;
  private String name;
  private UUID uuid;
  private boolean isVerified;
  private String discordUserId;
  private String verifyToken;
  private Instant verifyTokenCreated;
  private Instant verifyDate;
  private Instant lastJoinDate;

  public UserObject(int id, String name, UUID uuid, boolean isVerified, String discordUserId, String verifyToken, Instant verifyTokenCreated, Instant verifyDate, Instant lastJoinDate) {
    this.id = id;
    this.name = name;
    this.uuid = uuid;
    this.isVerified = isVerified;
    this.discordUserId = discordUserId;
    this.verifyToken = verifyToken;
    this.verifyTokenCreated = verifyTokenCreated;
    this.verifyDate = verifyDate;
    this.lastJoinDate = lastJoinDate;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setLastJoinDate(Instant lastJoinDate) {
    this.lastJoinDate = lastJoinDate;
  }

  public void setVerified(boolean verified) {
    isVerified = verified;
  }

  public void setVerifyDate(Instant verifyDate) {
    this.verifyDate = verifyDate;
  }

  public void setVerifyToken(String verifyToken) {
    this.verifyToken = verifyToken;
  }

  public void setVerifyTokenCreated(Instant verifyTokenCreated) {
    this.verifyTokenCreated = verifyTokenCreated;
  }

  public void setDiscordUserId(String discordUserId) {
    this.discordUserId = discordUserId;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public UUID getUuid() {
    return uuid;
  }

  public boolean isVerified() {
    return isVerified;
  }

  public String getDiscordUserId() {
    return discordUserId;
  }

  public String getVerifyToken() {
    return verifyToken;
  }

  public Instant getVerifyTokenCreated() {
    return verifyTokenCreated;
  }

  public Instant getVerifyDate() {
    return verifyDate;
  }

  public Instant getLastJoinDate() {
    return lastJoinDate;
  }

  public String getFormattedVerifyTokenCreated() {
    if(verifyTokenCreated == null) {
      return null;
    }

    return UserManager.formatter.format(verifyTokenCreated);
  }

  public String getFormattedVerifyDate() {
    if(verifyDate == null) {
      return null;
    }

    return UserManager.formatter.format(verifyDate);
  }

  public String getFormattedLastJoinDate() {
    if(lastJoinDate == null) {
      return null;
    }

    return UserManager.formatter.format(lastJoinDate);
  }

  public boolean isOnline() {
    return Bukkit.getPlayer(uuid) != null;
  }

  public Player getPlayer() {
    return Bukkit.getPlayer(uuid);
  }
}

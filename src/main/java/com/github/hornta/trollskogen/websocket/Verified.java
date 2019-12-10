package com.github.hornta.trollskogen.websocket;

public class Verified extends BaseClass {
  private int userId;
  private String discordUserId;
  private boolean isVerified;
  private String verifyDate;
  private String verifyToken;

  public int getUserId() {
    return userId;
  }

  public String getDiscordUserId() {
    return discordUserId;
  }

  public String getVerifyDate() {
    return verifyDate;
  }

  public String getVerifyToken() {
    return verifyToken;
  }

  public boolean isVerified() {
    return isVerified;
  }
}

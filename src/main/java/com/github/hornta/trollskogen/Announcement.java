package com.github.hornta.trollskogen;

public class Announcement {
  private String id;
  private String message;

  Announcement(String id, String message) {
    this.id = id;
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public String getMessage() {
    return message;
  }
}

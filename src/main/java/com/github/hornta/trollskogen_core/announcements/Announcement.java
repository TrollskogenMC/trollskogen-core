package com.github.hornta.trollskogen_core.announcements;

public class Announcement {
  private final int id;
  private final String name;
  private String message;

  Announcement(int id, String name, String message) {
    this.id = id;
    this.name = name;
    this.message = message;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

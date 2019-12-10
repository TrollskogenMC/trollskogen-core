package com.github.hornta.trollskogen.announcements;

public class Announcement {
  private int id;
  private String name;
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

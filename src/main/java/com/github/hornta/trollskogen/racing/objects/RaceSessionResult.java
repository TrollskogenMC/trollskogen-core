package com.github.hornta.trollskogen.racing.objects;

public class RaceSessionResult {
  private int position;
  private int time;

  RaceSessionResult() {
  }

  public int getPosition() {
    return position;
  }

  public int getTime() {
    return time;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public void setTime(int time) {
    this.time = time;
  }
}

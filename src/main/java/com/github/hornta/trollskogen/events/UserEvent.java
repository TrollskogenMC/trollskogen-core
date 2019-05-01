package com.github.hornta.trollskogen.events;

import com.github.hornta.trollskogen.User;

public class UserEvent extends BaseTrollEvent {
  protected User user;

  UserEvent(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }
}

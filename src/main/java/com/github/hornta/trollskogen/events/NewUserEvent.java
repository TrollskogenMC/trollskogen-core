package com.github.hornta.trollskogen.events;

import com.github.hornta.trollskogen.User;

public class NewUserEvent extends UserEvent {
  public NewUserEvent(User user) {
    super(user);
  }
}

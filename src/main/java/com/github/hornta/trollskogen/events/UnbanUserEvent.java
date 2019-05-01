package com.github.hornta.trollskogen.events;

import com.github.hornta.trollskogen.User;

public class UnbanUserEvent extends UserEvent {
  public UnbanUserEvent(User user) {
    super(user);
  }
}

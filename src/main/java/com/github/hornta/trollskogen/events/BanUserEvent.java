package com.github.hornta.trollskogen.events;

import com.github.hornta.trollskogen.User;

public class BanUserEvent extends UserEvent {
  public BanUserEvent(User user) {
    super(user);
  }
}

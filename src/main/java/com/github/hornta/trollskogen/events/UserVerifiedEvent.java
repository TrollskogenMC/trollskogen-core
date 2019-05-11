package com.github.hornta.trollskogen.events;

import com.github.hornta.trollskogen.User;

public class UserVerifiedEvent extends UserEvent {
  private boolean isVerified;

  public UserVerifiedEvent(User user, boolean isVerified) {
    super(user);
    this.isVerified = isVerified;
  }

  public boolean isVerified() {
    return isVerified;
  }
}

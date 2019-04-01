package com.github.hornta.trollskogen;

import se.hornta.carbon.ArgumentValidator;

public class AnnouncementExistValidator extends ArgumentValidator {
  private Main main;

  public AnnouncementExistValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(String arg) {
    return main
      .getAnnouncements().containsAnnouncement(arg);
  }

  @Override
  public void setMessageValues(String s) {
    main.getMessageManager().setValue("announcement_id", s);
  }

  @Override
  public String getErrorMessage() {
    return "announcement_not_found";
  }
}

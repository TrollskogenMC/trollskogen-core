package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.ArgumentValidator;

public class AnnouncementExistValidator extends ArgumentValidator {
  private Main main;

  public AnnouncementExistValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(CommandSender sender, String arg) {
    return main
      .getAnnouncements().containsAnnouncement(arg);
  }

  @Override
  public void setMessageValues(CommandSender sender, String s) {
    main.getMessageManager().setValue("announcement_id", s);
  }

  @Override
  public String getErrorMessage(CommandSender commandSender) {
    return "announcement_not_found";
  }
}

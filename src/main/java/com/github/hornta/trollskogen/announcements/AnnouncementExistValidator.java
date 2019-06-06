package com.github.hornta.trollskogen.announcements;

import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ValidationHandler;

public class AnnouncementExistValidator extends ValidationHandler {
  private Main main;

  public AnnouncementExistValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(CommandSender sender, String[] args) {
    return main
      .getAnnouncements().containsAnnouncement(args[0]);
  }

  @Override
  public void setMessageValues(CommandSender sender, String[] args) {
    main.getMessageManager().setValue("announcement_id", args[0]);
  }

  @Override
  public String getErrorMessage(CommandSender commandSender, String[] args) {
    return "announcement_not_found";
  }
}

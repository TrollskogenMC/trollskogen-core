package com.github.hornta.trollskogen.announcements;

import com.github.hornta.ValidationHandler;
import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;

public class AnnouncementExistValidator implements ValidationHandler {
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
  public void whenInvalid(CommandSender commandSender, String[] args) {
    main.getMessageManager().setValue("announcement_id", args[0]);
    main.getMessageManager().sendMessage(commandSender, "announcement_not_found");
  }
}

package com.github.hornta.trollskogen.announcements.commands;

import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;

public class CommandAnnouncementDisable implements ICommandHandler {
  private Main main;
  public CommandAnnouncementDisable(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    main.getAnnouncements().setEnabled(false);
    main.getMessageManager().sendMessage(commandSender, "disabled");
  }
}

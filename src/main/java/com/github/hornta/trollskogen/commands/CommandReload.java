package com.github.hornta.trollskogen.commands;

import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;

public class CommandReload implements ICommandHandler {
  Main main;

  public CommandReload(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] strings, int typedArgs) {
    try {
      main
        .getConfiguration().reload();
    } catch (Exception e) {
      return;
    }
    main.getUserManager().loadAllUsers();
    main.getAnnouncementManager().loadAllAnnouncements();
    MessageManager.sendMessage(commandSender, MessageKey.CONFIG_RELOADED_SUCCESS);
  }
}

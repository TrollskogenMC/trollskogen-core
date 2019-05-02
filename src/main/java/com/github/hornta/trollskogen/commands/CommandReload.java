package com.github.hornta.trollskogen.commands;

import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

public class CommandReload implements ICommandHandler {
  Main main;

  public CommandReload(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] strings) {
    main.getMessageManager().getConfig().reloadConfig();
    main.getAnnouncements().reload();
    main.getUserManager().load();
    main.getTrollskogenConfig().reload();
    main.getMessageManager().sendMessage(commandSender, "config-reloaded-success");
  }
}

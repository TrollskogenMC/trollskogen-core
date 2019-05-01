package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

import java.util.regex.Pattern;

public class CommandSetHome implements ICommandHandler {
  private Main main;
  private static final Pattern pattern = Pattern.compile("[a-z0-9_]+", Pattern.CASE_INSENSITIVE);

  CommandSetHome(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args) {
    User user = main.getUser(sender);
    Home existingHome = user.getHome(args[0]);

    int maxHomes = user.getMaxHomes();
    if(existingHome == null && user.getHomes().size() == maxHomes) {
      main.getMessageManager().setValue("num_homes", maxHomes);
      main.getMessageManager().sendMessage(sender, "max_homes");
      return;
    }

    user.setHome(args[0], ((Player)user.getPlayer()).getLocation());
    main.getMessageManager().sendMessage(sender, "home_set");
  }
}
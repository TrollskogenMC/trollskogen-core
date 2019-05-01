package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CommandHomes implements ICommandHandler {
  private Main main;

  CommandHomes(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args) {
    User user = main.getUser(sender);
    String homes = user
      .getHomes()
      .stream()
      .map((Home h) -> {
        main.getMessageManager().setValue("home_name", h.getName());

        String messageType = "homes_home";
        if(user.getHomes().indexOf(h) >= user.getMaxHomes()) {
          messageType = "homes_inactive_home";
        }
        return main.getMessageManager().getMessage(messageType);
      })
      .collect(Collectors.joining("§f, "));

    main.getMessageManager().setValue("homes", homes);
    main.getMessageManager().setValue("num_homes", user.getHomes().size());
    main.getMessageManager().setValue("max_homes", user.getMaxHomes());
    main.getMessageManager().sendMessage(sender, "homes");
  }
}

package com.github.hornta.trollskogen.homes.commands;

import com.github.hornta.trollskogen.homes.Home;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import org.bukkit.command.CommandSender;
import com.github.hornta.ICommandHandler;

import java.util.stream.Collectors;

public class CommandHomes implements ICommandHandler {
  private Main main;

  public CommandHomes(Main main) {
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

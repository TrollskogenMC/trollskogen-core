package com.github.hornta.trollskogen.homes.commands;

import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.users.UserObject;
import com.github.hornta.trollskogen.homes.Home;
import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class CommandHomes implements ICommandHandler {
  private Main main;

  public CommandHomes(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    UserObject user = main.getUser((Player) sender);
    String homes = main.getHomeManager().getHomes(user.getId())
      .stream()
      .map((Home h) -> {
        MessageManager.setValue("home_name", h.getName());

        MessageKey messageType = MessageKey.HOMES_HOME;
        if(main.getHomeManager().getHomes(user.getId()).indexOf(h) >= main.getMaxHomes(user)) {
          messageType = MessageKey.HOMES_INACTIVE_HOME;
        }
        String home = MessageManager.getMessage(messageType);

        if(h.isPublic()) {
          home += "§f(" + MessageManager.getMessage(MessageKey.HOME_PUBLIC);

          if(!h.isAllowCommands()) {
            home += "§f, " + MessageManager.getMessage(MessageKey.HOME_PUBLIC_DISALLOW_COMMANDS);
          }

          home += "§f)";
        }

        return home;
      })
      .collect(Collectors.joining("§f, "));

    MessageManager.setValue("homes", homes);
    MessageManager.setValue("num_homes", main.getHomeManager().getHomes(user.getId()).size());
    MessageManager.setValue("max_homes", main.getMaxHomes(user));
    MessageManager.sendMessage(sender, MessageKey.HOMES);
  }
}

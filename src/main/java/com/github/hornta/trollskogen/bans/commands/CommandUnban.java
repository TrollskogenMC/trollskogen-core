package com.github.hornta.trollskogen.bans.commands;

import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.bans.Ban;
import com.github.hornta.trollskogen.bans.events.RequestUnbanEvent;
import com.github.hornta.trollskogen.users.UserObject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import com.github.hornta.carbon.ICommandHandler;

import java.util.List;
import java.util.logging.Level;

public class CommandUnban implements ICommandHandler {
  private Main main;

  public CommandUnban(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    UserObject user = main.getUser(args[0]);
    Ban ban = main.getBanManager().getLongestBan(user);
    if(ban == null) {
      MessageManager.setValue("player", user.getName());
      MessageManager.sendMessage(commandSender, MessageKey.UNBAN_NOT_BANNED);
      return;
    }

    RequestUnbanEvent event = new RequestUnbanEvent(user, commandSender);
    Bukkit.getPluginManager().callEvent(event);
  }
}

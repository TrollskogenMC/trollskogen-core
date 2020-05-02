package com.github.hornta.trollskogen_core.bans.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.MessageKey;
import com.github.hornta.trollskogen_core.bans.Ban;
import com.github.hornta.trollskogen_core.bans.events.RequestUnbanEvent;
import com.github.hornta.trollskogen_core.users.UserObject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandUnban implements ICommandHandler {
  private final TrollskogenCorePlugin main;

  public CommandUnban(TrollskogenCorePlugin main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    UserObject user = TrollskogenCorePlugin.getUser(args[0]);
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

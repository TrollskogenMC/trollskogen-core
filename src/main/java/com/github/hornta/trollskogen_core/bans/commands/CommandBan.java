package com.github.hornta.trollskogen_core.bans.commands;

import com.github.hornta.commando.ICommandHandler;
import se.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.BaseCommand;
import com.github.hornta.trollskogen_core.DateUtils;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.MessageKey;
import com.github.hornta.trollskogen_core.bans.events.RequestBanEvent;
import com.github.hornta.trollskogen_core.users.UserObject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CommandBan extends BaseCommand implements ICommandHandler {
  private TrollskogenCorePlugin main;

  public CommandBan(TrollskogenCorePlugin main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    String time = getLastArg(args, 1);
    String reason = DateUtils.removeTimePattern(time);

    if (reason.isEmpty()) {
      MessageManager.sendMessage(commandSender, MessageKey.ERR_BAN_NO_REASON);
      return;
    }

    LocalDateTime timestamp = DateUtils.parseDateDiff(time, true);
    UserObject target = main.getUser(args[0]);

    if (timestamp == null && !commandSender.hasPermission("ts.permban")) {
      MessageManager.sendMessage(commandSender, MessageKey.NO_PERMISSION_PERMBAN);
      return;
    }

    Integer issuerId = null;
    if (commandSender instanceof Player) {
      issuerId = main.getUser((Player) commandSender).getId();
    }

    Instant expiryDate = null;
    if(timestamp != null) {
      expiryDate = timestamp.atZone(ZoneId.systemDefault()).toInstant();
    }

    RequestBanEvent event = new RequestBanEvent(expiryDate, reason, target.getId(), issuerId, commandSender);
    Bukkit.getPluginManager().callEvent(event);
  }
}

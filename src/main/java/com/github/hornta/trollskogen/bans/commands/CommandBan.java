package com.github.hornta.trollskogen.bans.commands;

import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.BaseCommand;
import com.github.hornta.trollskogen.DateUtils;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.bans.events.RequestBanEvent;
import com.github.hornta.trollskogen.users.UserObject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.hornta.carbon.ICommandHandler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.logging.Level;

public class CommandBan extends BaseCommand implements ICommandHandler {
  private Main main;

  public CommandBan(Main main) {
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

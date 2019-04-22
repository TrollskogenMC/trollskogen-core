package com.github.hornta.trollskogen;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.logging.Level;

public class CommandBan extends BaseCommand implements ICommandHandler {
  private Main main;

  CommandBan(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    String time = getLastArg(args, 1);
    String reason = DateUtils.removeTimePattern(time);

    if(reason.isEmpty()) {
      main.getMessageManager().sendMessage(commandSender, "err_ban_no_reason");
      return;
    }

    User target = main.getUser(args[0]);
    LocalDateTime timestamp = DateUtils.parseDateDiff(time, true);
    target.ban(reason, timestamp);
    if(target.getPlayer() instanceof Player) {
      ((Player) target.getPlayer()).kickPlayer(target.getBanMessage());
    }

    main.getMessageManager().setValue("reason", reason);
    main.getMessageManager().setValue("player", target.getLastSeenAs());
    if(timestamp != null) {
      main.getMessageManager().setValue("time_left", DateUtils.formatDateDiff(timestamp));
    }
    String messageType = timestamp == null ? "player_ban_permanent" : "player_ban_temporary";
    String banMessage = main.getMessageManager().getMessage(messageType);

    Bukkit.getLogger().log(Level.INFO, banMessage);
    Bukkit.broadcastMessage(banMessage);
  }
}

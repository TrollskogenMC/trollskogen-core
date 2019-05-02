package com.github.hornta.trollskogen.commands;

import com.github.hornta.trollskogen.BaseCommand;
import com.github.hornta.trollskogen.DateUtils;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

import java.time.LocalDateTime;
import java.util.logging.Level;

public class CommandBan extends BaseCommand implements ICommandHandler {
  private Main main;

  public CommandBan(Main main) {
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

    LocalDateTime timestamp = DateUtils.parseDateDiff(time, true);
    User target = main.getUser(args[0]);

    if(timestamp == null && !commandSender.hasPermission("ts.permban")) {
      main.getMessageManager().sendMessage(commandSender, "no_permission_permban");
      return;
    }

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

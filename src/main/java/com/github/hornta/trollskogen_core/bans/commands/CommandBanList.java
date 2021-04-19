package com.github.hornta.trollskogen_core.bans.commands;

import com.github.hornta.commando.ICommandHandler;
import se.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.BaseCommand;
import com.github.hornta.trollskogen_core.DateUtils;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.MessageKey;
import com.github.hornta.trollskogen_core.bans.Ban;
import org.bukkit.command.CommandSender;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

public class CommandBanList extends BaseCommand implements ICommandHandler {
  private TrollskogenCorePlugin main;

  public CommandBanList(TrollskogenCorePlugin main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args, int typedArgs) {
    commandSender.sendMessage(main
      .getBanManager()
      .getBans()
      .stream()
      .map((Ban ban) -> {
        MessageKey messageType;
        if(ban.getExpiryDate() == null) {
          messageType = MessageKey.BAN_LIST_PLAYER_PERMANENT;
        } else {
          messageType = MessageKey.BAN_LIST_PLAYER_TEMPORARY;
          MessageManager.setValue("time_left", DateUtils.formatDateDiff(LocalDateTime.ofInstant(ban.getExpiryDate(), ZoneOffset.ofHours(1))));
        }
        MessageManager.setValue("player", main.getUser(ban.getUserId()).getName());
        MessageManager.setValue("reason", ban.getReason());
        return MessageManager.getMessage(messageType);
      })
      .collect(Collectors.joining( "\n" )));
  }
}

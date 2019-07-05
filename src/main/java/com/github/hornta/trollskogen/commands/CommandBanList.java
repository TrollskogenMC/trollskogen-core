package com.github.hornta.trollskogen.commands;

import com.github.hornta.trollskogen.BaseCommand;
import com.github.hornta.trollskogen.DateUtils;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import org.bukkit.command.CommandSender;
import com.github.hornta.ICommandHandler;

import java.util.Comparator;
import java.util.stream.Collectors;

public class CommandBanList extends BaseCommand implements ICommandHandler {
  private Main main;

  public CommandBanList(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    commandSender.sendMessage(main.getUserManager()
      .getUsers()
      .values()
      .stream()
      .filter(User::isBanned)
      .sorted(Comparator.comparing(User::getLastSeenAs))
      .map((User u) -> {
        String messageType;
        if(u.getBanExpiration() == null) {
          messageType = "ban_list_player_permanent";
        } else {
          messageType = "ban_list_player_temporary";
          main.getMessageManager().setValue("time_left", DateUtils.formatDateDiff(u.getBanExpiration()));
        }
        main.getMessageManager().setValue("player", u.getLastSeenAs());
        main.getMessageManager().setValue("reason", u.getBanReason());
        return main.getMessageManager().getMessage(messageType);
      })
      .collect(Collectors.joining( "\n" )));
  }
}

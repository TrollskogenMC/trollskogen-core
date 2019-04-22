package com.github.hornta.trollskogen;

import org.bukkit.command.CommandSender;
import se.hornta.carbon.ICommandHandler;

import java.util.Comparator;
import java.util.stream.Collectors;

public class CommandBanList extends BaseCommand implements ICommandHandler {
  private Main main;

  CommandBanList(Main main) {
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
        main.getMessageManager().setValue("player", u.getLastSeenAs());
        main.getMessageManager().setValue("time_left", DateUtils.formatDateDiff(u.getBanExpiration()));
        main.getMessageManager().setValue("reason", u.getBanReason());
        return main.getMessageManager().getMessage("ban_list_player");
      })
      .collect(Collectors.joining( "," )));
  }
}

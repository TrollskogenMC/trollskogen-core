package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

import java.util.HashMap;

public class CommandCreateRace implements ICommandHandler {
  private Main main;
  public CommandCreateRace(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    Player player = (Player) commandSender;
    main.getRacing().createRace(player.getLocation(), args[0], (Race race) -> {
      main.getMessageManager().setValue("race_name", race.getName());
      main.getMessageManager().sendMessage(player, "create_race_success");
    });
  }
}

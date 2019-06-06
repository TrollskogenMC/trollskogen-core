package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.enums.RaceState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

public class CommandJoinRace implements ICommandHandler {
  private Main main;
  public CommandJoinRace(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    Race race = main.getRacing().getRace(args[0]);

    if(race.getState() != RaceState.PREPARING) {
      main.getMessageManager().sendMessage(commandSender, "error_join_race_state");
      return;
    }

    if(race.isFull()) {
      main.getMessageManager().sendMessage(commandSender, "error_join_race_full");
      return;
    }

    Player player = (Player)commandSender;

    if(race.isParticipating(player)) {
      main.getMessageManager().sendMessage(commandSender, "error_join_race_participating");
      return;
    }

    race.participate(player);
    main.getMessageManager().setValue("player_name", player.getName());
    main.getMessageManager().setValue("race_name", race.getName());
    main.getMessageManager().setValue("current_participants", race.getParticipants().size());
    main.getMessageManager().setValue("max_participants", race.getStartPoints().size());
    main.getMessageManager().broadcast("success_join_race");
  }
}

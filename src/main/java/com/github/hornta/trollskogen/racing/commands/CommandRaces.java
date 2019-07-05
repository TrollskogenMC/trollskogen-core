package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import org.bukkit.command.CommandSender;
import com.github.hornta.ICommandHandler;

import java.util.List;
import java.util.stream.Collectors;

public class CommandRaces implements ICommandHandler {
  private Main main;
  public CommandRaces(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    List<Race> races = main.getRacing().getRaces();

    main.getMessageManager().setValue("races", races
      .stream()
      .map((Race race) -> {
        main.getMessageManager().setValue("race", race.getName());

        String messageType;
        if(race.isEditing()) {
          messageType = "race_list_item_wip";
        } else if(!race.isEnabled()) {
          messageType = "race_list_item_disabled";
        } else {
          messageType = "race_list_item";
        }
        return main.getMessageManager().getMessage(messageType);
      })
      .collect(Collectors.joining("§f, ")));
    main.getMessageManager().setValue("num_race", races.size());
    main.getMessageManager().sendMessage(commandSender, "race_list");
  }
}

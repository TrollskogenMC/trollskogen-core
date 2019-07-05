package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.Race;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.hornta.ICommandHandler;

public class CommandRaceSpawn implements ICommandHandler {
  private Main main;

  private static final float SNAP_DEGREES = 45F;
  private static final double HALF_BLOCK = 0.5;

  public CommandRaceSpawn(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    Race race = main.getRacing().getRace(args[0]);
    if(race.isEditing() && !commandSender.hasPermission("ts.race.spawn.editing")) {
      main.getMessageManager().sendMessage(commandSender, "race_is_editing");
      return;
    }

    if(!race.isEnabled() && !commandSender.hasPermission("ts.race.spawn.disabled")) {
      main.getMessageManager().sendMessage(commandSender, "race_is_disabled");
      return;
    }

    Location loc = race.getSpawn().clone();
    loc.setPitch(SNAP_DEGREES * (Math.round(loc.getPitch() / SNAP_DEGREES)));
    loc.setYaw(SNAP_DEGREES * (Math.round(loc.getYaw() / SNAP_DEGREES)));
    loc.add(HALF_BLOCK, 0, HALF_BLOCK);
    ((Player)commandSender).teleport(loc);
  }
}

package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.RaceStartPoint;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import com.github.hornta.ICommandHandler;

public class CommandRaceTeleportStart implements ICommandHandler {
  private Main main;
  public CommandRaceTeleportStart(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    RaceStartPoint checkpoint = main.getRacing().getRace(args[0]).getStartPoint(Integer.parseInt(args[1]));
    Player player = (Player)commandSender;

    Location loc = checkpoint.getLocation();
    loc.setPitch(45F * (Math.round(loc.getPitch() / 45F)));
    loc.setYaw(45F * (Math.round(loc.getYaw() / 45F)));
    player.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
  }
}

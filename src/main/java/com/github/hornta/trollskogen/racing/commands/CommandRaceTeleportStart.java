package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.RaceStartPoint;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import se.hornta.carbon.ICommandHandler;

public class CommandRaceTeleportStart implements ICommandHandler {
  private Main main;
  public CommandRaceTeleportStart(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    RaceStartPoint checkpoint = main.getRacing().getRace(args[0]).getStartPoint(Integer.parseInt(args[1]));
    Player player = (Player)commandSender;
    player.teleport(checkpoint.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
  }
}

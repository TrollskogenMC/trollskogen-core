package com.github.hornta.trollskogen.racing.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.objects.RaceCheckpoint;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import se.hornta.carbon.ICommandHandler;

public class CommandRaceTeleportPoint implements ICommandHandler {
  private Main main;
  public CommandRaceTeleportPoint(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] args) {
    RaceCheckpoint checkpoint = main.getRacing().getRace(args[0]).getCheckpoint(Integer.parseInt(args[1]));
    Player player = (Player)commandSender;
    player.teleport(checkpoint.getCenter(), PlayerTeleportEvent.TeleportCause.COMMAND);
  }
}

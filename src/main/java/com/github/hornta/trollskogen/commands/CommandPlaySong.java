package com.github.hornta.trollskogen.commands;

import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.carbon.ICommandHandler;

public class CommandPlaySong implements ICommandHandler {
  private Main main;

  public CommandPlaySong(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args) {
    main.getSongManager().playSong(main.getSongManager().getSongByName(args[0]), (Player)sender);
  }
}

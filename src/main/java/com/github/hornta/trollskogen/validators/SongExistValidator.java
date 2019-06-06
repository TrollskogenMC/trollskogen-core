package com.github.hornta.trollskogen.validators;

import com.github.hornta.trollskogen.Main;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.ValidationHandler;

public class SongExistValidator extends ValidationHandler {
  private Main main;

  public SongExistValidator(Main main) {
    this.main = main;
  }

  @Override
  public boolean test(CommandSender sender, String[] args) {
    return main.getSongManager().getSongByName(args[0]) != null;
  }

  @Override
  public void setMessageValues(CommandSender sender, String[] args) {
    main.getMessageManager().setValue("song_name", args[0]);
  }

  @Override
  public String getErrorMessage(CommandSender commandSender, String[] args) {
    return "song_not_found";
  }
}

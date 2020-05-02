package com.github.hornta.trollskogen_core.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.messenger.Translation;
import com.github.hornta.trollskogen_core.ConfigKey;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.MessageKey;
import org.bukkit.command.CommandSender;

public class CommandReload implements ICommandHandler {
  TrollskogenCorePlugin main;

  public CommandReload(TrollskogenCorePlugin main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] strings, int typedArgs) {
    TrollskogenCorePlugin.getConfiguration().reload();
    Translation translation = TrollskogenCorePlugin.getInstance().getTranslations().createTranslation(TrollskogenCorePlugin.getConfiguration().get(ConfigKey.LANGUAGE));
    MessageManager.getInstance().setTranslation(translation);
    main.getUserManager().loadAllUsers();
    main.getAnnouncementManager().loadAllAnnouncements();
    MessageManager.sendMessage(commandSender, MessageKey.CONFIG_RELOADED_SUCCESS);
  }
}

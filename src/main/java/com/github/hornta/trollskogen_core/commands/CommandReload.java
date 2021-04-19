package com.github.hornta.trollskogen_core.commands;

import com.github.hornta.commando.ICommandHandler;
import se.hornta.messenger.MessageManager;
import se.hornta.messenger.MessengerException;
import se.hornta.messenger.Translation;
import com.github.hornta.trollskogen_core.ConfigKey;
import com.github.hornta.trollskogen_core.MessageKey;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.versioned_config.ConfigurationException;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

public class CommandReload implements ICommandHandler {
  TrollskogenCorePlugin main;

  public CommandReload(TrollskogenCorePlugin main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] strings, int typedArgs) {
    try {
      TrollskogenCorePlugin.getConfiguration().reload();
    } catch (ConfigurationException e) {
      TrollskogenCorePlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to reload config", e);
      return;
    }
    Translation translation;
    try {
      translation = TrollskogenCorePlugin.getInstance().getTranslations().createTranslation(TrollskogenCorePlugin.getConfiguration().get(ConfigKey.LANGUAGE));
    } catch (MessengerException e) {
      TrollskogenCorePlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to reload messages", e);
      return;
    }
    MessageManager.getInstance().setTranslation(translation);
    TrollskogenCorePlugin.getUserManager().loadAllUsers();
    main.getAnnouncementManager().loadAllAnnouncements();
    MessageManager.sendMessage(commandSender, MessageKey.CONFIG_RELOADED_SUCCESS);
  }
}

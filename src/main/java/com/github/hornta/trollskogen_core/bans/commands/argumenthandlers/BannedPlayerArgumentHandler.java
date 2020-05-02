package com.github.hornta.trollskogen_core.bans.commands.argumenthandlers;

import com.github.hornta.commando.ValidationResult;
import com.github.hornta.commando.completers.IArgumentHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.MessageKey;
import com.github.hornta.trollskogen_core.PrefixMatcher;
import com.github.hornta.trollskogen_core.bans.Ban;
import com.github.hornta.trollskogen_core.bans.events.BanEvent;
import com.github.hornta.trollskogen_core.bans.events.LoadBansEvent;
import com.github.hornta.trollskogen_core.bans.events.UnbanEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

public class BannedPlayerArgumentHandler implements IArgumentHandler, Listener {
  private final PrefixMatcher bannedUsernames = new PrefixMatcher();

  @EventHandler
  private void onLoadBans(LoadBansEvent event) {
    for(Ban ban : event.getBanManager().getBans()) {
      bannedUsernames.insert(TrollskogenCorePlugin.getUser(ban.getUserId()).getName());
    }
  }

  @EventHandler
  private void onBan(BanEvent event) {
    bannedUsernames.insert(TrollskogenCorePlugin.getUser(event.getBan().getUserId()).getName());
  }

  @EventHandler
  private void onUnbanUser(UnbanEvent event) {
    bannedUsernames.delete(TrollskogenCorePlugin.getUser(event.getBan().getUserId()).getName());
  }

  @Override
  public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
    return bannedUsernames.find(argument);
  }

  @Override
  public boolean test(Set<String> items, String argument) {
    return items.contains(argument);
  }

  @Override
  public void whenInvalid(ValidationResult validationResult) {
    MessageManager.sendMessage(validationResult.getCommandSender(), MessageKey.PLAYER_NOT_FOUND);
  }
}

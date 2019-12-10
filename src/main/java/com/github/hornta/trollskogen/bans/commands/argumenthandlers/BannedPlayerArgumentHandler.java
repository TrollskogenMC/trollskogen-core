package com.github.hornta.trollskogen.bans.commands.argumenthandlers;

import com.github.hornta.carbon.ValidationResult;
import com.github.hornta.carbon.completers.IArgumentHandler;
import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.PrefixMatcher;
import com.github.hornta.trollskogen.bans.Ban;
import com.github.hornta.trollskogen.bans.events.BanEvent;
import com.github.hornta.trollskogen.bans.events.LoadBansEvent;
import com.github.hornta.trollskogen.bans.events.UnbanEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

public class BannedPlayerArgumentHandler implements IArgumentHandler, Listener {
  private Main main;
  private PrefixMatcher bannedUsernames = new PrefixMatcher();

  public BannedPlayerArgumentHandler(Main main) {
    this.main = main;
  }

  @EventHandler
  private void onLoadBans(LoadBansEvent event) {
    for(Ban ban : event.getBanManager().getBans()) {
      bannedUsernames.insert(main.getUser(ban.getUserId()).getName());
    }
  }

  @EventHandler
  private void onBan(BanEvent event) {
    bannedUsernames.insert(main.getUser(event.getBan().getUserId()).getName());
  }

  @EventHandler
  private void onUnbanUser(UnbanEvent event) {
    bannedUsernames.delete(main.getUser(event.getBan().getUserId()).getName());
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

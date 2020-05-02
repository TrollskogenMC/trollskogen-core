package com.github.hornta.trollskogen_core.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.MessageKey;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CommandHat implements ICommandHandler {
  TrollskogenCorePlugin main;

  public CommandHat(TrollskogenCorePlugin main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] strings, int typedArgs) {
    Player player = (Player)commandSender;
    PlayerInventory inv = player.getInventory();
    ItemStack held = inv.getItemInMainHand();
    ItemStack helm = inv.getHelmet();
    if (held.getAmount() == 1 || held.getType() == Material.AIR) {
      inv.setHelmet(held);
      inv.setItemInMainHand(helm);
      player.updateInventory();
      MessageManager.sendMessage(commandSender, MessageKey.HAT_SWITCH_SUCCESS);
    } else {
      MessageManager.sendMessage(commandSender, MessageKey.HAT_SWITCH_DENY);
    }
  }
}

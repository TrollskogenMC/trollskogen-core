package com.github.hornta.trollskogen.commands;

import com.github.hornta.trollskogen.Main;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import se.hornta.carbon.ICommandHandler;

public class CommandHat implements ICommandHandler {
  Main main;

  public CommandHat(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender commandSender, String[] strings) {
    Player player = (Player)commandSender;
    PlayerInventory inv = player.getInventory();
    ItemStack held = inv.getItemInMainHand();
    ItemStack helm = inv.getHelmet();
    if (held.getAmount() == 1 || held.getType() == Material.AIR) {
      inv.setHelmet(held);
      inv.setItemInMainHand(helm);
      player.updateInventory();
      main.getMessageManager().sendMessage(commandSender, "hat-switch-success");
    } else {
      main.getMessageManager().sendMessage(commandSender, "hat-switch-deny");
    }
  }
}
